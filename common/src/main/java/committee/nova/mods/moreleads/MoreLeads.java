package committee.nova.mods.moreleads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MoreLeads {

    public final static String MOD_ID = "moreleads";
    public final static Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static void init() {
        ConfigFile.sync(ModConfig.class);
    }
}
