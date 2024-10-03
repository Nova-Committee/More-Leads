package committee.nova.mods.moreleads.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2024/10/4 02:38
 * @Description:
 */
@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity implements Leashable {

    @Unique
    @Nullable
    private Leashable.LeashData moreLeads$leashData;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Nullable
    @Override
    public Leashable.LeashData getLeashData() {
        return this.moreLeads$leashData;
    }


    @Override
    public void setLeashData(@Nullable Leashable.LeashData leashData) {
        this.moreLeads$leashData = leashData;
    }

    @Override
    public @NotNull Vec3 getLeashOffset() {
        return new Vec3(0.0, 0.88F * this.getEyeHeight(), this.getBbWidth() * 0.64F);
    }
    @Override
    public void elasticRangeLeashBehaviour(Entity entity, float f) {
        // 计算朝向持有者的拉力方向
        double dX = (entity.getX() - this.getX()) / (double) f;
        double dY = (entity.getY() - this.getY()) / (double) f;
        double dZ = (entity.getZ() - this.getZ()) / (double) f;

        // 拉力大小，距离越远拉力越强，但施加一个最大限制
        double pullStrength = Math.min((f - 6) * 0.1, 1.0); // 限制最大拉力
        this.setDeltaMovement(
                this.getDeltaMovement().add(
                        dX * pullStrength,
                        dY * pullStrength,
                        dZ * pullStrength
                )
        );
    }


    @Inject(
            method = "tick",
            at = @At(
                    value = "HEAD"
            )
    )
    public void moreLeads$tick(CallbackInfo ci) {
        if (!this.level().isClientSide) {
            LeashData leashData = ((Leashable)this).getLeashData();
            if (leashData != null && leashData.delayedLeashInfo != null) {
                Leashable.restoreLeashFromSave(this, leashData);
            }

            if (leashData != null && leashData.leashHolder != null) {
                if (!this.isAlive() || !leashData.leashHolder.isAlive()) {
                    dropLeash(true, true);
                }
            }
        }
        Entity entity2 = this.getLeashHolder();
        if (entity2 != null && entity2.level() == this.level()) {
            float f = this.distanceTo(entity2);
            if (!this.handleLeashAtDistance(entity2, f)) {
                return;
            }

            if ((double)f > 10.0) {
                this.leashTooFarBehaviour();
            } else if ((double)f > 6.0) {
                this.elasticRangeLeashBehaviour(entity2, f);
                this.checkSlowFallDistance();
            } else {
                this.closeRangeLeashBehaviour(entity2);
            }
        }
    }

    @Inject(
            method = "addAdditionalSaveData",
            at = @At(
                    value = "HEAD"
            )
    )
    public void moreLeads$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        this.writeLeashData(compoundTag, this.moreLeads$leashData);
    }

    @Inject(
            method = "readAdditionalSaveData",
            at = @At(
                    value = "HEAD"
            )
    )
    public void moreLeads$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        this.moreLeads$leashData = this.readLeashData(compoundTag);
    }

}
