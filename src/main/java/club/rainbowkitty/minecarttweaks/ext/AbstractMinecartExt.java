package club.rainbowkitty.minecarttweaks.ext;

import org.jspecify.annotations.Nullable;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.phys.Vec3;

import club.rainbowkitty.minecarttweaks.rail.RailContext;
import club.rainbowkitty.minecarttweaks.train.TrainSnapshot;

/**
 * Access interface injected onto {@link AbstractMinecart} to expose link state.
 */
public interface AbstractMinecartExt extends Linkable {

    /**
     * This cart's own top speed, ignoring any train it is part of. A train's ceiling is the highest
     * of its members', and asking a member for {@code getMaxSpeed} re-enters the walk that raises
     * it, so members are asked through here instead.
     */
    double minecarttweaks$maxSpeedAlone(ServerLevel level);

    /**
     * How hard this cart drives itself along the track in blocks per tick squared, or zero if it is
     * not under its own power. Only a burning, unparked furnace cart returns anything.
     */
    default double minecarttweaks$driveAcceleration() {
        return 0.0;
    }

    /** The direction a cart under its own power is pushing in, or zero if it is not under any. */
    default Vec3 minecarttweaks$driveHeading() {
        return Vec3.ZERO;
    }

    /**
     * How many bends this cart has moved onto since it was last asked: one for a corner, and one
     * for each step the grade changes by, so a crest counts double. Consuming, so it must be called
     * exactly once a tick. A corner that only reverses the one before it counts for nothing.
     */
    int minecarttweaks$bendsEntered();

    /**
     * The stretch of track this cart stands on, read once a tick and held. Collision asks this of
     * every candidate pair, so it must not walk the rails again on each call.
     */
    RailContext minecarttweaks$railContext();

    /** The last snapshot taken of this cart's train, whether or not it is still good to use. */
    @Nullable TrainSnapshot minecarttweaks$train();

    /** Hands this cart the snapshot it appears in, sparing its siblings taking their own. */
    void minecarttweaks$setTrain(TrainSnapshot train);

    /** The game time this cart's tracker withholds position updates until, or zero for none. */
    long minecarttweaks$resyncAt();

    /** Withholds this cart's position updates until {@code gameTime}, or zero to stop. */
    void minecarttweaks$setResyncAt(long gameTime);
}
