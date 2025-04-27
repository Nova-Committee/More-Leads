package committee.nova.mods.moreleads;

import committee.nova.mods.moreleads.api.ConfigFile;
import committee.nova.mods.moreleads.config.ModConfig;

import java.util.logging.Logger;

public final class MoreLeads {
    public static final String MOD_ID = "moreleads";
    public static final String MOD_NAME = "MoreLeads";
    public static final Logger LOGGER = Logger.getLogger(MOD_NAME);
    public static void init() {
        ConfigFile.sync(ModConfig.class);
    }
}
