package committee.nova.mods.moreleads;

import committee.nova.mods.moreleads.api.ConfigFile;
import committee.nova.mods.moreleads.config.ModConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MoreLeads {
    public static final String MOD_ID = "moreleads";
    public static final String MOD_NAME = "MoreLeads";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    public static void init() {
        ConfigFile.sync(ModConfig.class);
    }
}
