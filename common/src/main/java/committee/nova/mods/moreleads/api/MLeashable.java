package committee.nova.mods.moreleads.api;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

import committee.nova.mods.moreleads.utils.NbtUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/24 22:28
 * @Description:
 */
public interface MLeashable {
    String LEASH_TAG = "leash";
    double LEASH_TOO_FAR_DIST = 12.0;
    double LEASH_ELASTIC_DIST = 6.0;
    Vec3 AXIS_SPECIFIC_ELASTICITY = new Vec3(0.8, 0.2, 0.8);
    double TORSIONAL_ELASTICITY = 10.0;
    double STIFFNESS = 0.11;
    List<Vec3> ENTITY_ATTACHMENT_POINT = ImmutableList.of(new Vec3(0.0, 0.5, 0.5));
    List<Vec3> LEASHER_ATTACHMENT_POINT = ImmutableList.of(new Vec3(0.0, 0.5, 0.0));
    List<Vec3> SHARED_QUAD_ATTACHMENT_POINTS = ImmutableList.of(
            new Vec3(-0.5, 0.5, 0.5), new Vec3(-0.5, 0.5, -0.5), new Vec3(0.5, 0.5, -0.5), new Vec3(0.5, 0.5, 0.5)
    );

    @Nullable
    MLeashable.LeashData getLeashData();

    void setLeashData(@Nullable MLeashable.LeashData leashData);

    default boolean isLeashed() {
        return this.getLeashData() != null && this.getLeashData().leashHolder != null;
    }

    default boolean mayBeLeashed() {
        return this.getLeashData() != null;
    }

    default boolean canHaveALeashAttachedTo(Entity entity) {
        if (this == entity) {
            return false;
        } else {
            return !(this.leashDistanceTo(entity) > this.leashSnapDistance()) && this.canBeLeashed();
        }
    }

    default double leashDistanceTo(Entity entity) {
        return entity.getBoundingBox().getCenter().distanceTo(((Entity)this).getBoundingBox().getCenter());
    }

    default boolean canBeLeashed() {
        return true;
    }

    default void setDelayedLeashHolderId(int i) {
        this.setLeashData(new MLeashable.LeashData(i));
        dropLeash((Entity & MLeashable)this, false, false);
    }

    default void readLeashData(CompoundTag compoundTag) {
        MLeashable.LeashData leashData = NbtUtils.read(compoundTag,LEASH_TAG, MLeashable.LeashData.CODEC).orElse(null);
        if (this.getLeashData() != null && leashData == null) {
            this.removeLeash();
        }

        this.setLeashData(leashData);
    }

    default void writeLeashData(CompoundTag compoundTag, @Nullable MLeashable.LeashData leashData) {
        NbtUtils.storeNullable(compoundTag, LEASH_TAG, MLeashable.LeashData.CODEC, leashData);
    }

    private static <E extends Entity & MLeashable> void restoreLeashFromSave(E entity, MLeashable.LeashData leashData) {
        if (leashData.delayedLeashInfo != null && entity.level() instanceof ServerLevel serverLevel) {
            Optional<UUID> optional = leashData.delayedLeashInfo.left();
            Optional<BlockPos> optional2 = leashData.delayedLeashInfo.right();
            if (optional.isPresent()) {
                Entity entity2 = serverLevel.getEntity((UUID)optional.get());
                if (entity2 != null) {
                    setLeashedTo(entity, entity2, true);
                    return;
                }
            } else if (optional2.isPresent()) {
                setLeashedTo(entity, LeashFenceKnotEntity.getOrCreateKnot(serverLevel, (BlockPos)optional2.get()), true);
                return;
            }

            if (entity.tickCount > 100) {
                entity.spawnAtLocation(Items.LEAD);
                entity.setLeashData(null);
            }
        }
    }

    default void dropLeash() {
        dropLeash((Entity & MLeashable)this, true, true);
    }

    default void removeLeash() {
        dropLeash((Entity & MLeashable)this, true, false);
    }

    default void onLeashRemoved() {
    }

    private static <E extends Entity & MLeashable> void dropLeash(E entity, boolean bl, boolean bl2) {
        MLeashable.LeashData leashData = entity.getLeashData();
        if (leashData != null && leashData.leashHolder != null) {
            entity.setLeashData(null);
            entity.onLeashRemoved();
            if (entity.level() instanceof ServerLevel serverLevel) {
                if (bl2) {
                    entity.spawnAtLocation(Items.LEAD);
                }

                if (bl) {
                    serverLevel.getChunkSource().broadcast(entity, new ClientboundSetEntityLinkPacket(entity, null));
                }
                ((LeashAsHolder)leashData.leashHolder).notifyLeasheeRemoved(entity);
            }
        }
    }

    static <E extends Entity & MLeashable> void tickLeash(ServerLevel serverLevel, E entity) {
        MLeashable.LeashData leashData = entity.getLeashData();
        if (leashData != null && leashData.delayedLeashInfo != null) {
            restoreLeashFromSave(entity, leashData);
        }

        if (leashData != null && leashData.leashHolder != null) {
            if (!entity.isAlive() || !leashData.leashHolder.isAlive()) {
                if (serverLevel.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                    entity.dropLeash();
                } else {
                    entity.removeLeash();
                }
            }

            Entity entity2 = entity.getLeashHolder();
            if (entity2 != null && entity2.level() == entity.level()) {
                double d = entity.leashDistanceTo(entity2);
                entity.whenLeashedTo(entity2);
                if (d > entity.leashSnapDistance()) {
                    entity.leashTooFarBehaviour();
                } else if (d > entity.leashElasticDistance() - entity2.getBbWidth() - entity.getBbWidth() && entity.checkElasticInteractions(entity2, leashData)) {
                    entity.onElasticLeashPull();
                } else {
                    entity.closeRangeLeashBehaviour(entity2);
                }

                entity.setYRot((float)(entity.getYRot() - leashData.angularMomentum));
                leashData.angularMomentum = leashData.angularMomentum * angularFriction(entity);
            }
        }
    }

    default void onElasticLeashPull() {
        Entity entity = (Entity)this;
        if (entity.getDeltaMovement().y() > -0.5 && entity.fallDistance > 1.0) {
            entity.fallDistance = 1.0F;
        }
    }

    default double leashSnapDistance() {
        return LEASH_TOO_FAR_DIST;
    }

    default double leashElasticDistance() {
        return LEASH_ELASTIC_DIST;
    }

    static <E extends Entity & MLeashable> float angularFriction(E entity) {
        if (entity.onGround()) {
            return entity.level().getBlockState(entity.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction() * 0.91F;
        } else {
            return entity.isInWater() || entity.isInLava() ? 0.8F : 0.91F;
        }
    }

    default void whenLeashedTo(Entity entity) {
        ((LeashAsHolder)entity).notifyLeashHolder(this);
    }

    default void leashTooFarBehaviour() {
        this.dropLeash();
    }

    default void closeRangeLeashBehaviour(Entity entity) {
    }

    default boolean checkElasticInteractions(Entity entity, MLeashable.LeashData leashData) {
        boolean bl = ((LeashAsHolder)entity).supportQuadLeashAsHolder() && this.supportQuadLeash();
        List<MLeashable.Wrench> list = computeElasticInteraction(
                (Entity & MLeashable)this,
                entity,
                bl ? SHARED_QUAD_ATTACHMENT_POINTS : ENTITY_ATTACHMENT_POINT,
                bl ? SHARED_QUAD_ATTACHMENT_POINTS : LEASHER_ATTACHMENT_POINT
        );
        if (list.isEmpty()) {
            return false;
        } else {
            MLeashable.Wrench wrench = MLeashable.Wrench.accumulate(list).scale(bl ? 0.25 : 1.0);
            leashData.angularMomentum = leashData.angularMomentum + TORSIONAL_ELASTICITY * wrench.torque();
            Vec3 vec3 = entity.getDeltaMovement().subtract(((Entity)this).getDeltaMovement());
            ((Entity)this).addDeltaMovement(wrench.force().multiply(AXIS_SPECIFIC_ELASTICITY).add(vec3.scale(STIFFNESS)));
            return true;
        }
    }

    private static <E extends Entity & MLeashable> List<MLeashable.Wrench> computeElasticInteraction(E entity, Entity entity2, List<Vec3> list, List<Vec3> list2) {
        double d = entity.leashElasticDistance();
        Vec3 vec3 = entity.getDeltaMovement();
        float f = entity.getYRot() * (float) (Math.PI / 180.0);
        Vec3 vec32 = new Vec3(entity.getBbWidth(), entity.getBbHeight(), entity.getBbWidth());
        float g = entity2.getYRot() * (float) (Math.PI / 180.0);
        Vec3 vec33 = new Vec3(entity2.getBbWidth(), entity2.getBbHeight(), entity2.getBbWidth());
        List<MLeashable.Wrench> list3 = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            Vec3 vec34 = ((Vec3)list.get(i)).multiply(vec32).yRot(-f);
            Vec3 vec35 = entity.position().add(vec34);
            Vec3 vec36 = ((Vec3)list2.get(i)).multiply(vec33).yRot(-g);
            Vec3 vec37 = entity2.position().add(vec36);
            computeDampenedSpringInteraction(vec37, vec35, d, vec3, vec34).ifPresent(list3::add);
        }

        return list3;
    }

    private static Optional<MLeashable.Wrench> computeDampenedSpringInteraction(Vec3 vec3, Vec3 vec32, double d, Vec3 vec33, Vec3 vec34) {
        double e = vec32.distanceTo(vec3);
        if (e < d) {
            return Optional.empty();
        } else {
            Vec3 vec35 = vec3.subtract(vec32).normalize().scale(e - d);
            double f = MLeashable.Wrench.torqueFromForce(vec34, vec35);
            boolean bl = vec33.dot(vec35) >= 0.0;
            if (bl) {
                vec35 = vec35.scale(0.3F);
            }

            return Optional.of(new MLeashable.Wrench(vec35, f));
        }
    }

    default boolean supportQuadLeash() {
        return false;
    }

    default Vec3[] getQuadLeashOffsets() {
        return createQuadLeashOffsets((Entity)this, 0.0, 0.5, 0.5, 0.5);
    }

    static Vec3[] createQuadLeashOffsets(Entity entity, double d, double e, double f, double g) {
        float h = entity.getBbWidth();
        double i = d * h;
        double j = e * h;
        double k = f * h;
        double l = g * entity.getBbHeight();
        return new Vec3[]{new Vec3(-k, l, j + i), new Vec3(-k, l, -j + i), new Vec3(k, l, -j + i), new Vec3(k, l, j + i)};
    }

    default Vec3 getLeashOffset(float f) {
        return this.getLeashOffset();
    }

    default Vec3 getLeashOffset() {
        Entity entity = (Entity)this;
        return new Vec3(0.0, entity.getEyeHeight(), entity.getBbWidth() * 0.4F);
    }

    default void setLeashedTo(Entity entity, boolean bl) {
        if (this != entity) {
            setLeashedTo((Entity & MLeashable)this, entity, bl);
        }
    }

    private static <E extends Entity & MLeashable> void setLeashedTo(E entity, Entity entity2, boolean bl) {
        MLeashable.LeashData leashData = entity.getLeashData();
        if (leashData == null) {
            leashData = new MLeashable.LeashData(entity2);
            entity.setLeashData(leashData);
        } else {
            Entity entity3 = leashData.leashHolder;
            leashData.setLeashHolder(entity2);
            if (entity3 != null && entity3 != entity2) {
                ((LeashAsHolder)entity3).notifyLeasheeRemoved(entity);
            }
        }

        if (bl && entity.level() instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().broadcast(entity, new ClientboundSetEntityLinkPacket(entity, entity2));
        }

        if (entity.isPassenger()) {
            entity.stopRiding();
        }
    }

    @Nullable
    default Entity getLeashHolder() {
        return getLeashHolder((Entity & MLeashable)this);
    }

    @Nullable
    private static <E extends Entity & MLeashable> Entity getLeashHolder(E entity) {
        MLeashable.LeashData leashData = entity.getLeashData();
        if (leashData == null) {
            return null;
        } else {
            if (leashData.delayedLeashHolderId != 0 && entity.level().isClientSide) {
                Entity var3 = entity.level().getEntity(leashData.delayedLeashHolderId);
                if (var3 instanceof Entity) {
                    leashData.setLeashHolder(var3);
                }
            }

            return leashData.leashHolder;
        }
    }

    static List<MLeashable> leashableLeashedTo(Entity entity) {
        return leashableInArea(entity, leashable -> leashable.getLeashHolder() == entity);
    }

    static List<MLeashable> leashableInArea(Entity entity, Predicate<MLeashable> predicate) {
        return leashableInArea(entity.level(), entity.getBoundingBox().getCenter(), predicate);
    }

    static List<MLeashable> leashableInArea(Level level, Vec3 vec3, Predicate<MLeashable> predicate) {
        double d = 32.0;
        AABB aABB = AABB.ofSize(vec3, 32.0, 32.0, 32.0);
        return level.getEntitiesOfClass(Entity.class, aABB, entity -> entity instanceof MLeashable leashable && predicate.test(leashable))
                .stream()
                .map(MLeashable.class::cast)
                .toList();
    }

    public static final class LeashData {
        public static final Codec<MLeashable.LeashData> CODEC = ExtraCodecs.xor(UUIDUtil.CODEC.fieldOf("UUID").codec(), BlockPos.CODEC)
                .xmap(
                        MLeashable.LeashData::new,
                        leashData -> {
                            if (leashData.leashHolder instanceof LeashFenceKnotEntity leashFenceKnotEntity) {
                                return Either.right(leashFenceKnotEntity.getPos());
                            } else {
                                return leashData.leashHolder != null
                                        ? Either.left(leashData.leashHolder.getUUID())
                                        : Objects.requireNonNull(leashData.delayedLeashInfo, "Invalid LeashData had no attachment");
                            }
                        }
                );
        int delayedLeashHolderId;
        @Nullable
        public Entity leashHolder;
        @Nullable
        public Either<UUID, BlockPos> delayedLeashInfo;
        public double angularMomentum;

        private LeashData(@Nullable Either<UUID, BlockPos> either) {
            this.delayedLeashInfo = either;
        }

        LeashData(@Nullable Entity entity) {
            this.leashHolder = entity;
        }

        LeashData(int i) {
            this.delayedLeashHolderId = i;
        }

        public void setLeashHolder(Entity entity) {
            this.leashHolder = entity;
            this.delayedLeashInfo = null;
            this.delayedLeashHolderId = 0;
        }
    }

    public record Wrench(Vec3 force, double torque) {
        static MLeashable.Wrench ZERO = new MLeashable.Wrench(Vec3.ZERO, 0.0);

        static double torqueFromForce(Vec3 vec3, Vec3 vec32) {
            return vec3.z * vec32.x - vec3.x * vec32.z;
        }

        static MLeashable.Wrench accumulate(List<MLeashable.Wrench> list) {
            if (list.isEmpty()) {
                return ZERO;
            } else {
                double d = 0.0;
                double e = 0.0;
                double f = 0.0;
                double g = 0.0;

                for (MLeashable.Wrench wrench : list) {
                    Vec3 vec3 = wrench.force;
                    d += vec3.x;
                    e += vec3.y;
                    f += vec3.z;
                    g += wrench.torque;
                }

                return new MLeashable.Wrench(new Vec3(d, e, f), g);
            }
        }

        public MLeashable.Wrench scale(double d) {
            return new MLeashable.Wrench(this.force.scale(d), this.torque * d);
        }
    }
}
