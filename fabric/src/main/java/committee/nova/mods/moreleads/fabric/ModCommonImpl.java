package committee.nova.mods.moreleads.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2024/9/29 02:37
 * @Description:
 */
public class ModCommonImpl {
    public static Path getConfigPath() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
