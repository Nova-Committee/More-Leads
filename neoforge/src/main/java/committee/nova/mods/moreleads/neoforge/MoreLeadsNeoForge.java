package committee.nova.mods.moreleads.neoforge;


import committee.nova.mods.moreleads.common.Constants;
import committee.nova.mods.moreleads.common.MoreLeadsCommon;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class MoreLeadsNeoForge {

    public MoreLeadsNeoForge(IEventBus eventBus) {

        MoreLeadsCommon.init();
    }
}