package committee.nova.mods.moreleads.utils;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * @Project: MoreLeads
 * @Author: cnlimiter
 * @CreateTime: 2025/4/24 22:45
 * @Description:
 */
public class NbtUtils {
    public static <T> void store(CompoundTag tag, String string, Codec<T> codec, T object) {
        store(tag, string, codec, NbtOps.INSTANCE, object);
    }

    public static <T> void storeNullable(CompoundTag tag, String string, Codec<T> codec, @Nullable T object) {
        if (object != null) {
            store(tag, string, codec, object);
        }
    }

    public static <T> void store(CompoundTag tag, String string, Codec<T> codec, DynamicOps<Tag> dynamicOps, T object) {
        tag.put(string, codec.encodeStart(dynamicOps, object).getOrThrow());
    }

    public static <T> void storeNullable(CompoundTag tag, String string, Codec<T> codec, DynamicOps<Tag> dynamicOps, @Nullable T object) {
        if (object != null) {
            store(tag, string, codec, dynamicOps, object);
        }
    }

    public static <T> void store(CompoundTag tag, MapCodec<T> mapCodec, T object) {
        store(tag, mapCodec, NbtOps.INSTANCE, object);
    }

    public static <T> void store(CompoundTag tag, MapCodec<T> mapCodec, DynamicOps<Tag> dynamicOps, T object) {
        tag.merge((CompoundTag)mapCodec.encoder().encodeStart(dynamicOps, object).getOrThrow());
    }

    public static <T> Optional<T> read(CompoundTag tag, String string, Codec<T> codec) {
        return read(tag, string, codec, NbtOps.INSTANCE);
    }

    public static <T> Optional<T> read(CompoundTag tag, String string, Codec<T> codec, DynamicOps<Tag> dynamicOps) {
        Tag tag1 = tag.get(string);
        return tag1 == null
                ? Optional.empty()
                : codec.parse(dynamicOps, tag).resultOrPartial(string2 -> LOGGER.error("Failed to read field ({}={}): {}", string, tag, string2));
    }

    public static <T> Optional<T> read(CompoundTag tag, MapCodec<T> mapCodec) {
        return read(tag, mapCodec, NbtOps.INSTANCE);
    }

    public static <T> Optional<T> read(CompoundTag tag, MapCodec<T> mapCodec, DynamicOps<Tag> dynamicOps) {
        return mapCodec.decode(dynamicOps, dynamicOps.getMap(tag).getOrThrow())
                .resultOrPartial(string -> LOGGER.error("Failed to read value ({}): {}", this, string));
    }
}
