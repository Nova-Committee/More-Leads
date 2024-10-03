package committee.nova.mods.moreleads.mixin;

import committee.nova.mods.moreleads.ModConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2024/10/4 03:49
 * @Description:
 */ // Allows ambient entities (bats) to be leashed
@Mixin(AmbientCreature.class)
abstract class AmbientEntityMixin extends Mob {
    protected AmbientEntityMixin(EntityType<? extends AmbientCreature> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "canBeLeashed", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.isLeashed()) && ModConfig.AMBIENTS_ENABLED));
    }
}
