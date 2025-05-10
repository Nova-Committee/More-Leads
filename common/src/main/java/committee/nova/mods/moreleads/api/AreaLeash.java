package committee.nova.mods.moreleads.api;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/27 03:19
 * @Description:
 */
public interface AreaLeash {
    static List<ILeash> leashableLeashedTo(Entity entity) {
        return leashableInArea(entity, leashable -> leashable.moreLeads$getLeashHolder() == entity);
    }

    static List<ILeash> leashableInArea(Entity entity, Predicate<ILeash> predicate) {
        return leashableInArea(entity.level(), entity.getBoundingBox().getCenter(), predicate);
    }

    static List<ILeash> leashableInArea(Level level, Vec3 vec3, Predicate<ILeash> predicate) {
        double d = 32.0;
        AABB aABB = AABB.ofSize(vec3, 32.0, 32.0, 32.0);
        return level.getEntitiesOfClass(Entity.class, aABB, entity -> entity instanceof ILeash leashable && predicate.test(leashable))
                .stream()
                .map(ILeash.class::cast)
                .toList();
    }

    static boolean shearOffAllLeashConnections(Mob mob, @Nullable Player player) {
        boolean bl = dropAllLeashConnections(mob, player);
        if (bl && mob.level() instanceof ServerLevel serverLevel) {
            serverLevel.playSound(null, mob.blockPosition(), SoundEvents.SHEEP_SHEAR, player != null ? player.getSoundSource() : mob.getSoundSource());
        }

        return bl;
    }

    static boolean dropAllLeashConnections(Mob mob, @Nullable Player player) {
        List<ILeash> list = leashableLeashedTo(mob);
        boolean bl = !list.isEmpty();
        if (mob.isLeashed()) {
            mob.dropLeash(true, true);
            bl = true;
        }

        for (ILeash leashable2 : list) {
            leashable2.dropLeash(true, true);
        }

        if (bl) {
            mob.gameEvent(GameEvent.SHEAR, player);
            return true;
        } else {
            return false;
        }
    }
}
