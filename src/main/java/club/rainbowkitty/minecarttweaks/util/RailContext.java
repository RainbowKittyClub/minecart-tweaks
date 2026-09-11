package club.rainbowkitty.minecarttweaks.util;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;

/**
 * The stretch of track a cart occupies: the rail it stands on and the two its exits lead to. Held
 * so that "are these two on the same piece of track" can be answered by comparing block positions
 * rather than walking the rails, which is what makes the question cheap enough to ask from the
 * collision path.
 *
 * <p>A cart caches one of these per tick. It is therefore as of the start of the tick, and a cart
 * moving fast enough to cross a block within one has a neighbourhood a block behind where it now
 * is — harmless, because the three blocks recorded still overlap the one it moved to.
 */
public record RailContext(
        @Nullable BlockPos rail,
        @Nullable BlockPos first,
        @Nullable BlockPos second) {

    /** A cart that is not on a rail at all, and so shares track with nothing. */
    public static final RailContext OFF_RAILS = new RailContext(null, null, null);

    /** Whether the cart stands on a rail at all. */
    public boolean onRails() {
        return rail != null;
    }

    /** Reads {@code cart}'s current stretch of track. Callers should prefer the cart's own memo. */
    public static RailContext of(AbstractMinecart cart) {
        Level level = cart.level();
        BlockPos rail = cart.getCurrentBlockPosOrRailBelow();

        // Not isOnRails(): that is a flag the movement code sets, and a stale true would take the
        // neighbour read into a block that is not a rail at all.
        if (!BaseRailBlock.isRail(level.getBlockState(rail))) {
            return OFF_RAILS;
        }

        BlockPos[] around =
                RailPath.neighbours(level, rail, cart.getDeltaMovement().horizontal());

        return new RailContext(rail, around[0], around[1]);
    }

    /**
     * Whether two carts stand on the same rail or one exit apart — as far as this need see, since
     * carts more than a block apart cannot touch.
     *
     * <p>True if either says so: a junction rail picks its shape from the cart reading it, so a
     * cart on one sees only its own axis, and erring towards neighbours merely collides as vanilla
     * intends. Both sides read the start-of-tick memo, not live positions, or a cart crossing a
     * block boundary mid-tick gives the pair walls that disagree.
     */
    public static boolean sharesTrack(AbstractMinecart cart, AbstractMinecart other) {
        RailContext mine = cart.minecarttweaks$railContext();
        RailContext theirs = other.minecarttweaks$railContext();

        return mine.covers(theirs) || theirs.covers(mine);
    }

    /** Whether the rail {@code other} stands on is this stretch of track or a step off it. */
    public boolean covers(RailContext other) {
        if (!onRails() || !other.onRails()) {
            return false;
        }

        BlockPos pos = other.rail();
        return pos.equals(rail) || pos.equals(first) || pos.equals(second);
    }
}
