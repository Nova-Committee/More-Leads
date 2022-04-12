package nova.committee.moreleads.init.mixin;

import net.minecraft.entity.*;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.world.World;
import nova.committee.moreleads.init.ModConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/12 13:24
 * Version: 1.0
 */
@Mixin(MobEntity.class)
abstract class MobEntityMixin extends LivingEntity {
    protected MobEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract boolean getLeashed();

    @Inject(method = "canBeLeashedTo", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.getLeashed()) && ModConfig.COMMON.HOSTILES_ENABLED.get()));
    }
}


// Allows trader entities (villagers and wandering traders) to be leashed
@Mixin(AbstractVillagerEntity.class)
abstract class MerchantEntityMixin extends AgeableEntity implements INPC, IMerchant {
    protected MerchantEntityMixin(EntityType<? extends AbstractVillagerEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "canBeLeashedTo", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.getLeashed()) && ModConfig.COMMON.VILLAGERS_ENABLED.get()));
    }
}

// Allows water creatures (fish) to be leashed
@Mixin(WaterMobEntity.class)
abstract class WaterCreatureEntityMixin extends CreatureEntity {
    protected WaterCreatureEntityMixin(EntityType<? extends WaterMobEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "canBeLeashedTo", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.getLeashed()) && ModConfig.COMMON.WATER_CREATURES_ENABLED.get()));
    }
}

// Allows turtles to be leashed
@Mixin(TurtleEntity.class)
abstract class TurtleEntityMixin extends AnimalEntity {
    protected TurtleEntityMixin(EntityType<? extends TurtleEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "canBeLeashedTo", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.getLeashed()) && ModConfig.COMMON.TURTLES_ENABLED.get()));
    }
}

// Allows ambient entities (bats) to be leashed
@Mixin(AmbientEntity.class)
abstract class AmbientEntityMixin extends MobEntity {
    protected AmbientEntityMixin(EntityType<? extends AmbientEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "canBeLeashedTo", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.getLeashed()) && ModConfig.COMMON.AMBIENTS_ENABLED.get()));
    }
}

// Allows pandas to be leashed
@Mixin(PandaEntity.class)
abstract class PandaEntityMixin extends AnimalEntity {
    protected PandaEntityMixin(EntityType<? extends PandaEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "canBeLeashedTo", at = @At("RETURN"), cancellable = true)
    private void onCanBeLeashedBy(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue((cir.getReturnValue() || (!this.getLeashed()) && ModConfig.COMMON.PANDAS_ENABLED.get()));
    }
}


// Deny enderman teltport
@Mixin(EndermanEntity.class)
abstract class EndermanTeleportMixin extends MonsterEntity {
    public EndermanTeleportMixin(EntityType<? extends EndermanEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "teleportRandomly", at = @At("RETURN"), cancellable = true)
    private void teleportRandomly(CallbackInfoReturnable<Boolean> cir) {
        if (this.getLeashed()) {
            cir.setReturnValue(false);
        }
    }
}
