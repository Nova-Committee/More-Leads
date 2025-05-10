package committee.nova.mods.moreleads.mixin.lead;

import committee.nova.mods.moreleads.config.ModConfig;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.item.trading.Merchant;
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
 */ // Allows trader entities (villagers and wandering traders) to be leashed
@Mixin(AbstractVillager.class)
public abstract class MerchantEntityMixin extends AgeableMob implements Npc, Merchant {
    protected MerchantEntityMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "canBeLeashed", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.isLeashed()) && ModConfig.VILLAGERS_ENABLED));
    }
}
