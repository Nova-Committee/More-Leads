package committee.nova.mods.moreleads.mixin;

import committee.nova.mods.moreleads.ModConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2024/9/24 19:23
 * @Description:
 */
@Mixin(Mob.class)
abstract class MobEntityMixin extends LivingEntity implements Leashable {

    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }


    @Inject(method = "canBeLeashed", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.isLeashed()) && ModConfig.HOSTILES_ENABLED));
    }
}
