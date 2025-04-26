package committee.nova.mods.moreleads.mixin;

import committee.nova.mods.moreleads.api.AreaLeash;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
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
public abstract class MobMixin extends LivingEntity{
    @Unique
    Mob moreLeads$self = (Mob) (Object) this;
    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "checkAndHandleImportantInteractions", at = @At(value = "HEAD"), cancellable = true)
    public void moreleads$checkAndHandleImportantInteractions(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (moreLeads$self.isAlive()
                && player.isSecondaryUseActive()
                && moreLeads$self.canBeLeashed(player)
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

        if (itemStack.is(Items.SHEARS) && AreaLeash.shearOffAllLeashConnections(moreLeads$self, player)) {
            itemStack.hurtAndBreak(1, player, playerx -> playerx.broadcastBreakEvent(interactionHand));
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
