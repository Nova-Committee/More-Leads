package committee.nova.mods.moreleads.forge;

import committee.nova.mods.moreleads.MoreLeads;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

@Mod(MoreLeads.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class MoreLeadsForge {
    public MoreLeadsForge() {
        MoreLeads.init();
    }
}
