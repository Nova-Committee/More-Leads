package committee.nova.mods.moreleads.mixin;

import committee.nova.mods.moreleads.api.ILeash;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.LeadItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/27 03:34
 * @Description:
 */
@Mixin(LeadItem.class)
public abstract class LeadItemMixin extends Item {


    public LeadItemMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "useOn", at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/world/item/LeadItem;bindPlayerMobs(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/InteractionResult;"), cancellable = true)
    public void moreleads$useOn(UseOnContext useOnContext, CallbackInfoReturnable<InteractionResult> cir) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        Player player = useOnContext.getPlayer();
        LeashFenceKnotEntity leashFenceKnotEntity = null;
        boolean bl = false;
        int i = blockPos.getX();
        int j = blockPos.getY();
        int k = blockPos.getZ();

        for (ILeash mob : level
                .getEntitiesOfClass(Entity.class, new AABB((double)i - (double)7.0F, (double)j - (double)7.0F, (double)k - (double)7.0F, (double)i + (double)7.0F, (double)j + (double)7.0F, (double)k + (double)7.0F),
                        entity -> entity instanceof ILeash leashable)
                .stream()
                .map(ILeash.class::cast)
                .toList()
        ) {
            if (mob.moreLeads$getLeashHolder() == player) {
                if (leashFenceKnotEntity == null) {
                    leashFenceKnotEntity = LeashFenceKnotEntity.getOrCreateKnot(level, blockPos);
                    leashFenceKnotEntity.playPlacementSound();
                }

                mob.setLeashedTo(leashFenceKnotEntity, true);
                bl = true;
            }
        }

        if (bl) {
            level.gameEvent(GameEvent.BLOCK_ATTACH, blockPos, GameEvent.Context.of(player));
        }

        cir.setReturnValue(bl ? InteractionResult.SUCCESS : InteractionResult.PASS);
    }
}
