package nova.committee.moreleads.init;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;
import nova.committee.moreleads.Static;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Description:
 * Author: cnlimiter
 * Date: 2022/4/12 13:30
 * Version: 1.0
 */
@Mod.EventBusSubscriber(modid = Static.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfig {
    public static final Common COMMON;
    public static final ForgeConfigSpec CONFIG_SPEC;

    static {
        final Pair<Common, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(Common::new);
        COMMON = specPair.getLeft();
        CONFIG_SPEC = specPair.getRight();
    }

    public static class Common {
        public final ForgeConfigSpec.ConfigValue<Boolean> VILLAGERS_ENABLED, HOSTILES_ENABLED, WATER_CREATURES_ENABLED, TURTLES_ENABLED, AMBIENTS_ENABLED, PANDAS_ENABLED;

        public Common(ForgeConfigSpec.Builder builder) {
            VILLAGERS_ENABLED = builder.comment("allow villager").define("VILLAGERS_ENABLED", true);
            HOSTILES_ENABLED = builder.comment("all hostiles").define("HOSTILES_ENABLED", true);
            WATER_CREATURES_ENABLED = builder.comment("allow water creatures").define("WATER_CREATURES_ENABLED", true);
            TURTLES_ENABLED = builder.comment("allow turtles").define("TURTLES_ENABLED", true);
            AMBIENTS_ENABLED = builder.comment("allow ambients").define("AMBIENTS_ENABLED", true);
            PANDAS_ENABLED = builder.comment("allow pandas").define("PANDAS_ENABLED", true);

        }

    }
}
