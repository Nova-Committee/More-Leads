package committee.nova.mods.moreleads.mixin.lead;

import committee.nova.mods.moreleads.config.ModConfig;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


// Allows water creatures (fish) to be leashed
@Mixin(WaterAnimal.class)
public abstract class WaterCreatureEntityMixin extends PathfinderMob {
    protected WaterCreatureEntityMixin(EntityType<? extends WaterAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "canBeLeashed", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.isLeashed()) && ModConfig.WATER_CREATURES_ENABLED));
    }
}


