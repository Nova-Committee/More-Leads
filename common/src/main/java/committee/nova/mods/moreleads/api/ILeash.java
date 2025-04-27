package committee.nova.mods.moreleads.api;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.protocol.game.ClientboundSetEntityLinkPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/27 04:57
 * @Description:
 */
public interface ILeash {
    static List<ILeash> leashableLeashedTo(Entity entity) {
        return leashableInArea(entity, leashable -> leashable.moreLeads$getLeashHolder() == entity);
    }

    static List<ILeash> leashableInArea(Entity entity, Predicate<ILeash> predicate) {
        return leashableInArea(entity.level, entity.getBoundingBox().getCenter(), predicate);
    }

    static List<ILeash> leashableInArea(Level level, Vec3 vec3, Predicate<ILeash> predicate) {
        double d = 32;
        AABB aABB = new AABB(vec3.x - d / 2.0, vec3.y - d / 2.0, vec3.z - d / 2.0, vec3.x + d / 2.0, vec3.y + d / 2.0, vec3.z + d / 2.0);
        return level.getEntitiesOfClass(Entity.class, aABB, entity -> entity instanceof ILeash && predicate.test((ILeash) entity))
                .stream()
                .map(ILeash.class::cast)
                .collect(Collectors.toList());
    }
    default boolean shearOffAllLeashConnections(@Nullable Player player) {
        boolean bl = dropAllLeashConnections(player);
        if (bl && ((Entity) this).level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel) ((Entity) this).level;
            serverLevel.playSound(null, ((Entity)this).blockPosition(), SoundEvents.SHEEP_SHEAR, player != null ? player.getSoundSource() : ((Entity)this).getSoundSource(), 1, 1);
        }

        return bl;
    }

    default boolean dropAllLeashConnections(@Nullable Player player) {
        List<ILeash> list = leashableLeashedTo(((Entity)this));
        boolean bl = !list.isEmpty();
        if (isLeashed()) {
            dropLeash();
            bl = true;
        }

        for (ILeash leashable2 : list) {
            leashable2.dropLeash();
        }

        return bl;
    }

    default boolean moreleads$startRidingV(boolean bl2, Entity entity, boolean bl) {
        if (bl2 && this.isLeashed()) {
            this.dropLeash();
        }
        return bl2;
    }

    @Nullable
    Entity moreLeads$getLeashHolder();
    CompoundTag moreLeads$getLeashInfoTag();
    void moreLeads$setLeashHolder(Entity entity);
    void moreLeads$setLeashInfoTag(CompoundTag tag);
    void moreLeads$setDelayedLeashHolderId(int i);

    default boolean canBeLeashed(Player player) {
        return !this.isLeashed() ;
    }

    default boolean isLeashed() {
        return this.moreLeads$getLeashHolder() != null;
    }

    default void setLeashedTo(Entity entity, boolean bl) {
        this.moreLeads$setLeashHolder(entity);
        this.moreLeads$setLeashInfoTag(null);
        if (!((Entity) this).level.isClientSide && bl && ((Entity) this).level instanceof ServerLevel) {
            ((ServerLevel)((Entity) this).level).getChunkSource().broadcast(((Entity) this), new ClientboundSetEntityLinkPacket(((Entity) this), this.moreLeads$getLeashHolder()));
        }

        if (((Entity) this).isPassenger()) {
            ((Entity) this).stopRiding();
        }
    }

    default void dropLeash() {
        dropLeash(true, true);
    }

    default void removeLeash() {
        dropLeash(true, false);
    }

    default void dropLeash(boolean bl, boolean bl2) {
        if (this.moreLeads$getLeashHolder() != null) {
            this.moreLeads$setLeashHolder(null);
            this.moreLeads$setLeashInfoTag(null);
            if (!((Entity) this).level.isClientSide && bl2) {
                ((Entity) this).spawnAtLocation(Items.LEAD);
            }

            if (!((Entity) this).level.isClientSide && bl && ((Entity) this).level instanceof ServerLevel) {
                ((ServerLevel)((Entity) this).level).getChunkSource().broadcast(((Entity) this), new ClientboundSetEntityLinkPacket(((Entity) this), null));
            }
        }
    }

    default void tickLeash() {
        if (this.moreLeads$getLeashInfoTag() != null) {
            this.restoreLeashFromSave();
        }

        if (this.moreLeads$getLeashHolder() != null) {
            if (!((Entity) this).isAlive() || !this.moreLeads$getLeashHolder().isAlive()) {
                this.dropLeash();
            }
        }
    }

    default void restoreLeashFromSave() {
        if (this.moreLeads$getLeashInfoTag() != null && ((Entity) this).level instanceof ServerLevel) {
            if (this.moreLeads$getLeashInfoTag().hasUUID("UUID")) {
                UUID uUID = this.moreLeads$getLeashInfoTag().getUUID("UUID");
                Entity entity = ((ServerLevel)((Entity) this).level).getEntity(uUID);
                if (entity != null) {
                    this.setLeashedTo(entity, true);
                    return;
                }
            } else if (this.moreLeads$getLeashInfoTag().contains("X", 99) && this.moreLeads$getLeashInfoTag().contains("Y", 99) && this.moreLeads$getLeashInfoTag().contains("Z", 99)) {
                BlockPos blockPos = NbtUtils.readBlockPos(this.moreLeads$getLeashInfoTag());
                this.setLeashedTo(LeashFenceKnotEntity.getOrCreateKnot(((Entity) this).level, blockPos), true);
                return;
            }

            if (((Entity) this).tickCount > 100) {
                ((Entity) this).spawnAtLocation(Items.LEAD);
                this.moreLeads$setLeashInfoTag(null);
            }
        }
    }

    default void saveData(CompoundTag compoundTag) {
        if (this.moreLeads$getLeashHolder() != null) {
            CompoundTag compoundTag3 = new CompoundTag();
            if (this.moreLeads$getLeashHolder() instanceof LivingEntity) {
                UUID uUID = this.moreLeads$getLeashHolder().getUUID();
                compoundTag3.putUUID("UUID", uUID);
            } else if (this.moreLeads$getLeashHolder() instanceof HangingEntity) {
                BlockPos blockPos = ((HangingEntity)this.moreLeads$getLeashHolder()).getPos();
                compoundTag3.putInt("X", blockPos.getX());
                compoundTag3.putInt("Y", blockPos.getY());
                compoundTag3.putInt("Z", blockPos.getZ());
            }

            compoundTag.put("ILeash", compoundTag3);
        } else if (this.moreLeads$getLeashInfoTag() != null) {
            compoundTag.put("ILeash", this.moreLeads$getLeashInfoTag().copy());
        }
    }

    default void loadData(CompoundTag compoundTag) {
        if (compoundTag.contains("ILeash", 10)) {
            this.moreLeads$setLeashInfoTag(compoundTag.getCompound("ILeash"));
        }
    }
}
