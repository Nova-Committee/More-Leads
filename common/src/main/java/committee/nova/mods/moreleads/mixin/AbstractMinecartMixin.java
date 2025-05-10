package committee.nova.mods.moreleads.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/5/10 15:20
 * @Description:
 */
@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity {
    @Unique
    AbstractMinecart moreLeads$self = (AbstractMinecart) (Object) this;
    @Unique
    @Nullable
    private Entity moreLeads$leashHolder;
    @Unique
    private int moreLeads$delayedLeashHolderId;
    @Unique
    @Nullable
    private CompoundTag moreLeads$leashInfoTag;

    public AbstractMinecartMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }
}
