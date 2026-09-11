package club.rainbowkitty.minecarttweaks.datacomponent;

import java.util.UUID;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;

/** The cart a link item has selected as one end of the link being built. */
public record SelectedCart(UUID value) {

    public static final Codec<SelectedCart> CODEC =
            UUIDUtil.CODEC.xmap(SelectedCart::new, SelectedCart::value);

    public static final StreamCodec<ByteBuf, SelectedCart> STREAM_CODEC =
            UUIDUtil.STREAM_CODEC.map(SelectedCart::new, SelectedCart::value);
}
