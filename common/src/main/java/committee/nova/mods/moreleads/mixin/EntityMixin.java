package committee.nova.mods.moreleads.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2024/10/4 03:50
 * @Description:
 */
@Mixin(Entity.class)
public abstract class EntityMixin {

    @Redirect(
            method = "baseTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Leashable;tickLeash(Lnet/minecraft/world/entity/Entity;)V")
    )
    <E extends Entity & Leashable> void moreLeads$baseTick(E entity) {
        if(!(entity instanceof Player)) {
            Leashable.tickLeash(entity);
        }
    }
}
