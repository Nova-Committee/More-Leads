package committee.nova.mods.moreleads.platform.forge;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/27 03:54
 * @Description:
 */
public class PlatformHelperImpl {
    public static Path getConfigPath() {
        return FMLPaths.CONFIGDIR.get();
    }
}
