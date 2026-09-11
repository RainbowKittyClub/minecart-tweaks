package club.rainbowkitty.minecarttweaks.attachment;

import java.util.Optional;
import java.util.UUID;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.UUIDUtil;

/**
 * A cart's link state: two unordered neighbour slots, each holding a linked cart's UUID.
 *
 * @param first one neighbour slot
 * @param second the other neighbour slot
 */
public record LinkableData(Optional<UUID> first, Optional<UUID> second) {

    public static final LinkableData EMPTY = new LinkableData(Optional.empty(), Optional.empty());

    public static final Codec<LinkableData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    UUIDUtil.CODEC.optionalFieldOf("first").forGetter(LinkableData::first),
                    UUIDUtil.CODEC.optionalFieldOf("second").forGetter(LinkableData::second)
            ).apply(instance, LinkableData::new));

    /** Returns whether either slot is in use. */
    public boolean hasLink() {
        return first.isPresent() || second.isPresent();
    }

    /** Returns whether either slot is free. */
    public boolean hasFreeSlot() {
        return first.isEmpty() || second.isEmpty();
    }

    /**
     * Returns a copy holding the given UUID, unchanged if it is already held or both slots are
     * occupied. Idempotent so that a slot can never end up holding the same cart twice.
     */
    public LinkableData withNeighbour(UUID id) {
        if (first.filter(id::equals).isPresent() || second.filter(id::equals).isPresent()) {
            return this;
        }
        if (first.isEmpty()) {
            return new LinkableData(Optional.of(id), second);
        }
        if (second.isEmpty()) {
            return new LinkableData(first, Optional.of(id));
        }
        return this;
    }

    /** Returns a copy with the given UUID cleared from both slots. */
    public LinkableData withoutNeighbour(UUID id) {
        return new LinkableData(
                first.filter(held -> !held.equals(id)),
                second.filter(held -> !held.equals(id)));
    }
}
