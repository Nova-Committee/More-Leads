package committee.nova.mods.moreleads.fabric;

import committee.nova.mods.moreleads.common.Constants;
import committee.nova.mods.moreleads.common.MoreLeadsCommon;
import net.fabricmc.api.ModInitializer;

public class MoreLeadsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        MoreLeadsCommon.init();
    }
}
