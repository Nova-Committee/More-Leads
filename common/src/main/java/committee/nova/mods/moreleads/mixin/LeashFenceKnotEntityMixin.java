package committee.nova.mods.moreleads.mixin;

import committee.nova.mods.moreleads.api.ILeash;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.LeashFenceKnotEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/27 03:34
 * @Description:
 */
@Mixin(LeashFenceKnotEntity.class)
public abstract class LeashFenceKnotEntityMixin extends HangingEntity {
    protected LeashFenceKnotEntityMixin(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
    }

    protected LeashFenceKnotEntityMixin(EntityType<? extends HangingEntity> entityType, Level level, BlockPos blockPos) {
        super(entityType, level, blockPos);
    }

    @Inject(method = "interact", at = @At(value = "RETURN", ordinal = 1))
    public void moreleads$interact(Player player, InteractionHand interactionHand, CallbackInfoReturnable<InteractionResult> cir) {
        boolean bl = false;
        List<ILeash> list = this.level
                .getEntitiesOfClass(Entity.class, new AABB(this.getX() - 7.0, this.getY() - 7.0, this.getZ() - 7.0, this.getX() + 7.0, this.getY() + 7.0, this.getZ() + 7.0),
                        entity -> entity instanceof ILeash)
                .stream()
                .map(ILeash.class::cast)
                .collect(Collectors.toList());

        for (ILeash mob : list) {
            if (mob.moreLeads$getLeashHolder() == player) {
                mob.setLeashedTo(this, true);
                bl = true;
            }
        }

        if (!bl) {
            this.remove();
            if (player.abilities.instabuild) {
                for (ILeash iLeash : list) {
                    if (iLeash.isLeashed() && iLeash.moreLeads$getLeashHolder() == this) {
                        iLeash.removeLeash();
                    }
                }
            }
        }
    }
}
