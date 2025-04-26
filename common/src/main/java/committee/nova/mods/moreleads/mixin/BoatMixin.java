package committee.nova.mods.moreleads.mixin;

import committee.nova.mods.moreleads.api.AreaLeash;
import committee.nova.mods.moreleads.api.ILeash;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/27 04:37
 * @Description:
 */
@Mixin(Boat.class)
public abstract class BoatMixin extends Entity implements ILeash {
    @Unique
    Boat moreLeads$self = (Boat) (Object) this;
    @Unique
    @Nullable
    private Entity moreLeads$leashHolder;
    @Unique
    private int moreLeads$delayedLeashHolderId;
    @Unique
    @Nullable
    private CompoundTag moreLeads$leashInfoTag;

    public BoatMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "addAdditionalSaveData", at = @At(value = "TAIL"))
    public void moreleads$addAdditionalSaveDatas(CompoundTag compoundTag, CallbackInfo ci) {
        this.saveData(compoundTag);
    }

    @Inject(method = "readAdditionalSaveData", at = @At(value = "TAIL"))
    public void moreleads$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        this.loadData(compoundTag);
    }

    @Inject(method = "tick", at = @At(value = "TAIL"))
    public void moreleads$tick(CallbackInfo ci) {
        if (!this.level().isClientSide) {
            this.tickLeash();
        }
    }

    @Inject(method = "interact", at = @At(value = "HEAD"), cancellable = true)
    public void moreleads$interact(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (moreLeads$self.isAlive()
                && player.isSecondaryUseActive()
            //&& !self.isBaby()
        ) {
            List<Mob> list = AreaLeash.leashableInArea(moreLeads$self, leashablex -> leashablex.getLeashHolder() == player);
            if (!list.isEmpty()) {
                for (Mob leashable2 : list) {
                    leashable2.setLeashedTo(moreLeads$self, true);
                }

                moreLeads$self.level().gameEvent(GameEvent.ENTITY_INTERACT, moreLeads$self.blockPosition(), GameEvent.Context.of(player));
                cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide));
            }
        }

        if (itemStack.is(Items.SHEARS) && shearOffAllLeashConnections(player)) {
            itemStack.hurtAndBreak(1, player, playerx -> playerx.broadcastBreakEvent(interactionHand));
            cir.setReturnValue(InteractionResult.SUCCESS);
        } else {
            if (itemStack.is(Items.LEAD) && this.canBeLeashed(player)) {
                this.setLeashedTo(player, true);
                itemStack.shrink(1);
                cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide));
            }
        }
    }

    @Override
    public void removeAfterChangingDimensions() {
        this.dropLeash(true, false);
        this.getAllSlots().forEach(itemStack -> {
            if (!itemStack.isEmpty()) {
                itemStack.setCount(0);
            }
        });
    }

    @Override
    public boolean startRiding(Entity entity, boolean bl) {
        return moreleads$startRidingV(super.startRiding(entity, bl), entity, bl);
    }

    @Override
    public @Nullable Entity moreLeads$getLeashHolder() {
        if (this.moreLeads$leashHolder == null && this.moreLeads$delayedLeashHolderId != 0 && this.level().isClientSide) {
            this.moreLeads$leashHolder = this.level().getEntity(this.moreLeads$delayedLeashHolderId);
        }

        return this.moreLeads$leashHolder;
    }

    @Override
    public void moreLeads$setLeashHolder(@Nullable Entity leashHolder) {
        this.moreLeads$leashHolder = leashHolder;
    }

    @Override
    public @Nullable CompoundTag moreLeads$getLeashInfoTag() {
        return moreLeads$leashInfoTag;
    }

    @Override
    public void moreLeads$setLeashInfoTag(@Nullable CompoundTag leashInfoTag) {
        this.moreLeads$leashInfoTag = leashInfoTag;
    }

    @Override
    public void moreLeads$setDelayedLeashHolderId(int delayedLeashHolderId) {
        this.moreLeads$delayedLeashHolderId = delayedLeashHolderId;
        this.dropLeash(false, false);
    }
}
