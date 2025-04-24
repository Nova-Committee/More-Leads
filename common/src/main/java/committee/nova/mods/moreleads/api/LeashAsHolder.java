package committee.nova.mods.moreleads.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/24 22:59
 * @Description:
 */
public interface LeashAsHolder {
    public default Vec3[] getQuadLeashHolderOffsets() {
        return MLeashable.createQuadLeashOffsets((Entity) this, 0.0, 0.5, 0.5, 0.0);
    }

    public default boolean supportQuadLeashAsHolder() {
        return false;
    }

    public void notifyLeashHolder(MLeashable leashable);

    public void notifyLeasheeRemoved(MLeashable leashable);

    public default Vec3 getRopeHoldPosition(float f) {
        return ((Entity)this).getPosition(f).add(0.0, ((Entity)this).getEyeHeight() * 0.7, 0.0);
    }
}
