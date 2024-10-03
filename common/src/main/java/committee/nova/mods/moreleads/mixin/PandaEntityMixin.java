package committee.nova.mods.moreleads.mixin;

import committee.nova.mods.moreleads.ModConfig;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Panda;
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
 */ // Allows pandas to be leashed
@Mixin(Panda.class)
abstract class PandaEntityMixin extends Animal {
    protected PandaEntityMixin(EntityType<? extends Panda> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "canBeLeashed", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.isLeashed()) && ModConfig.PANDAS_ENABLED));
    }
}
