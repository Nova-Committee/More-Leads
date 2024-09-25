package committee.nova.mods.moreleads.mixin;

import committee.nova.mods.moreleads.common.ModConfig;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ambient.AmbientCreature;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Panda;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Npc;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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
abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Shadow
    public abstract boolean isLeashed();

    @Inject(method = "canBeLeashed", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.isLeashed()) && ModConfig.HOSTILES_ENABLED));
    }
}


// Allows trader entities (villagers and wandering traders) to be leashed
@Mixin(AbstractVillager.class)
abstract class MerchantEntityMixin extends AgeableMob implements Npc, Merchant {
    protected MerchantEntityMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "canBeLeashed", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.isLeashed()) && ModConfig.VILLAGERS_ENABLED));
    }
}

// Allows water creatures (fish) to be leashed
@Mixin(WaterAnimal.class)
abstract class WaterCreatureEntityMixin extends PathfinderMob {
    protected WaterCreatureEntityMixin(EntityType<? extends WaterAnimal> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "canBeLeashed", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.isLeashed()) && ModConfig.WATER_CREATURES_ENABLED));
    }
}

// Allows turtles to be leashed
@Mixin(Turtle.class)
abstract class TurtleEntityMixin extends Animal {
    protected TurtleEntityMixin(EntityType<? extends Turtle> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "canBeLeashed", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.isLeashed()) && ModConfig.TURTLES_ENABLED));
    }
}

// Allows ambient entities (bats) to be leashed
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

// Allows pandas to be leashed
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


// Deny enderman teltport
@Mixin(EnderMan.class)
abstract class EndermanTeleportMixin extends Monster {
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