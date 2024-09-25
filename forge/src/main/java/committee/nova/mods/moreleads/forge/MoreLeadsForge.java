package committee.nova.mods.moreleads.forge;

import committee.nova.mods.moreleads.common.Constants;
import committee.nova.mods.moreleads.common.MoreLeadsCommon;
import net.minecraftforge.fml.common.Mod;

@Mod(Constants.MOD_ID)
public class MoreLeadsForge {
    
    public MoreLeadsForge() {
        MoreLeadsCommon.init();
    }
}