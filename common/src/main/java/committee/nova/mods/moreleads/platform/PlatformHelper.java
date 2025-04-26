package committee.nova.mods.moreleads.platform;

import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/24 23:10
 * @Description:
 */
public interface PlatformHelper {
    @ExpectPlatform
    public static Path getConfigPath() {
        throw new IllegalStateException();
    }
}
