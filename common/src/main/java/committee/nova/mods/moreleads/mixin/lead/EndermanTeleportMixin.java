package committee.nova.mods.moreleads.mixin.lead;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/5/5 02:37
 * @Description:
 */ // Deny enderman teltport
@Mixin(EnderMan.class)
public abstract class EndermanTeleportMixin extends Monster {
    public EndermanTeleportMixin(EntityType<? extends EnderMan> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "teleport()Z", at = @At("RETURN"), cancellable = true)
    private void teleportRandomly(CallbackInfoReturnable<Boolean> cir) {
        if (this.isLeashed()) {
            cir.setReturnValue(false);
        }
    }
}
