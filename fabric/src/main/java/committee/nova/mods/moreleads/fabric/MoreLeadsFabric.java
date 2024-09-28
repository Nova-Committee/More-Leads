package committee.nova.mods.moreleads.fabric;

import committee.nova.mods.moreleads.MoreLeads;
import net.fabricmc.api.ModInitializer;

public class MoreLeadsFabric implements ModInitializer {
    
    @Override
    public void onInitialize() {
        MoreLeads.init();
    }
}
