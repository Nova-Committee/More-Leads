package committee.nova.mods.moreleads.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/27 03:19
 * @Description:
 */
public interface AreaLeash {
    static List<Mob> leashableLeashedTo(Entity entity) {
        return leashableInArea(entity, leashable -> leashable.getLeashHolder() == entity);
    }

    static List<Mob> leashableInArea(Entity entity, Predicate<Mob> predicate) {
        return leashableInArea(entity.level, entity.getBoundingBox().getCenter(), predicate);
    }

    static List<Mob> leashableInArea(Level level, Vec3 vec3, Predicate<Mob> predicate) {
        double d = 32.0;
        AABB aABB = new AABB(vec3.x - d / 2.0, vec3.y - d / 2.0, vec3.z - d / 2.0, vec3.x + d / 2.0, vec3.y + d / 2.0, vec3.z + d / 2.0);
        return level.getEntitiesOfClass(Entity.class, aABB, entity -> entity instanceof Mob && predicate.test((Mob) entity))
                .stream()
                .map(Mob.class::cast)
                .collect(Collectors.toList());
    }

    static boolean shearOffAllLeashConnections(Mob mob, @Nullable Player player) {
        boolean bl = dropAllLeashConnections(mob, player);
        if (bl && mob.level instanceof ServerLevel) {
            ServerLevel serverLevel = (ServerLevel) mob.level;
            serverLevel.playSound(null, mob.blockPosition(), SoundEvents.SHEEP_SHEAR, player != null ? player.getSoundSource() : mob.getSoundSource(), 1, 1);
        }

        return bl;
    }

    static boolean dropAllLeashConnections(Mob mob, @Nullable Player player) {
        List<Mob> list = leashableLeashedTo(mob);
        boolean bl = !list.isEmpty();
        if (mob.isLeashed()) {
            mob.dropLeash(true, true);
            bl = true;
        }

        for (Mob leashable2 : list) {
            leashable2.dropLeash(true, true);
        }

        return bl;
    }
}
