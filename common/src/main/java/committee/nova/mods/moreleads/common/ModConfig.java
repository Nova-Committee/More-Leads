package committee.nova.mods.moreleads.common;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2024/9/24 19:28
 * @Description:
 */
@ConfigFile.Comment("Configuration File")
@ConfigFile.Name("general")
@ConfigFile.File("moreleads.cfg")
public class ModConfig {

    @ConfigFile.Comment("Enable leading villagers")
    public static boolean VILLAGERS_ENABLED = true;

    @ConfigFile.Comment("Enable leading hostiles")
    public static boolean HOSTILES_ENABLED = true;

    @ConfigFile.Comment("Enable leading water creatures")
    public static boolean WATER_CREATURES_ENABLED = true;

    @ConfigFile.Comment("Enable leading turtles")
    public static boolean TURTLES_ENABLED = true;

    @ConfigFile.Comment("Enable leading ambients")
    public static boolean AMBIENTS_ENABLED = true;

    @ConfigFile.Comment("Enable leading pandas")
    public static boolean PANDAS_ENABLED = true;

}
