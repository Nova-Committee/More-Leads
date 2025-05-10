package committee.nova.mods.moreleads.mixin;

import committee.nova.mods.moreleads.api.AreaLeash;
import committee.nova.mods.moreleads.api.ILeash;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/27 03:25
 * @Description:
 */
@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity implements ILeash {
    @Shadow
    private int delayedLeashHolderId;
    @Shadow
    @Nullable
    private Entity leashHolder;
    @Unique
    Mob moreLeads$self = (Mob) (Object) this;
    @Unique
    @Nullable
    private CompoundTag moreLeads$leashInfoTag;

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "checkAndHandleImportantInteractions", at = @At(value = "HEAD"), cancellable = true)
    public void moreleads$checkAndHandleImportantInteractions(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        if (player.level().isClientSide) {
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (moreLeads$self.isAlive()
                && player.isSecondaryUseActive()
                && moreLeads$self.canBeLeashed(player)
            //&& !self.isBaby()
        ) {
            List<ILeash> list = AreaLeash.leashableInArea(moreLeads$self, leashablex -> leashablex.moreLeads$getLeashHolder() == player);
            if (!list.isEmpty()) {
                for (ILeash leashable2 : list) {
                    leashable2.setLeashedTo(moreLeads$self, true);
                }

                moreLeads$self.level().gameEvent(GameEvent.ENTITY_INTERACT, moreLeads$self.blockPosition(), GameEvent.Context.of(player));
                cir.setReturnValue(InteractionResult.sidedSuccess(this.level().isClientSide));
            }
        }

        if (itemStack.is(Items.SHEARS) && AreaLeash.shearOffAllLeashConnections(moreLeads$self, player)) {
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
    public @Nullable Entity moreLeads$getLeashHolder() {
        if (this.leashHolder == null && this.delayedLeashHolderId != 0) {
            this.leashHolder = this.level().getEntity(this.delayedLeashHolderId);
        }

        return this.leashHolder;
    }

    @Override
    public void moreLeads$setLeashHolder(@Nullable Entity leashHolder) {
        this.leashHolder = leashHolder;
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
        this.delayedLeashHolderId = delayedLeashHolderId;
        this.dropLeash(false, false);
    }
}
