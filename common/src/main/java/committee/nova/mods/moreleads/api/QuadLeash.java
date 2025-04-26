package committee.nova.mods.moreleads.api;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/27 04:23
 * @Description:
 */
public interface QuadLeash {
    default Vec3[] getQuadLeashHolderOffsets(Entity entity) {
        return createQuadLeashOffsets((Entity)this, 0.0, 0.5, 0.5, 0.5);
    }

    static Vec3[] createQuadLeashOffsets(Entity entity, double d, double e, double f, double g) {
        float h = entity.getBbWidth();
        double i = d * h;
        double j = e * h;
        double k = f * h;
        double l = g * entity.getBbHeight();
        return new Vec3[]{new Vec3(-k, l, j + i), new Vec3(-k, l, -j + i), new Vec3(k, l, -j + i), new Vec3(k, l, j + i)};
    }

    default boolean supportQuadLeash() {
        return false;
    }

    default boolean supportQuadLeashAsHolder() {
        return false;
    }

    default void notifyLeashHolder(Mob leashable) {
    }

    default void notifyLeasheeRemoved(Mob leashable) {
    }
}
