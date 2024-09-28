package committee.nova.mods.moreleads.neoforge;

import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2024/9/29 02:37
 * @Description:
 */
public class ModCommonImpl {
    public static Path getConfigPath() {
       return FMLPaths.CONFIGDIR.get();
    }
}
