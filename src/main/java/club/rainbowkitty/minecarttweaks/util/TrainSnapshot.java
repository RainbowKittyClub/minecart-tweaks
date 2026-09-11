package club.rainbowkitty.minecarttweaks.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jspecify.annotations.Nullable;

import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;

import club.rainbowkitty.minecarttweaks.ext.AbstractMinecartExt;

/**
 * One train's cars in order, head first. Built once a tick by whichever of its cars ticks first and
 * handed to all the others: every coupling question is the same walk over the same links, and one
 * walk per car per question is quadratic in the length of the train.
 */
public final class TrainSnapshot {

    // A train cannot outgrow max_train_length, but a link graph corrupted by a half-written save
    // could; the walks are bounded so a bad one cannot hang the server tick.
    private static final int MAX_WALK = 1024;

    // Bumped whenever a link is written or a linked car leaves the world, so a snapshot taken
    // before the change is not reused after it.
    private static long generation;

    private final List<AbstractMinecart> cars;
    private final boolean closed;
    private final long builtTick;
    private final long builtGeneration;

    private TrainSnapshot(List<AbstractMinecart> cars, boolean closed, long builtTick) {
        this.cars = cars;
        this.closed = closed;
        this.builtTick = builtTick;
        this.builtGeneration = generation;
    }

    /** Marks every snapshot taken so far as stale. Call after any change to the link graph. */
    public static void invalidate() {
        generation++;
    }

    /**
     * The train {@code car} belongs to, reusing this tick's snapshot where one has been taken.
     * A car with no links is a train of one.
     */
    public static TrainSnapshot of(AbstractMinecart car) {
        long tick = car.level().getGameTime();

        if (car instanceof AbstractMinecartExt ext) {
            TrainSnapshot cached = ext.minecarttweaks$train();

            if (cached != null && cached.builtTick == tick
                    && cached.builtGeneration == generation) {
                return cached;
            }
        }

        TrainSnapshot built = build(car, tick);

        for (AbstractMinecart member : built.cars) {
            if (member instanceof AbstractMinecartExt ext) {
                ext.minecarttweaks$setTrain(built);
            }
        }

        return built;
    }

    /** The cars of this train, head first, or in no particular order when it closes on itself. */
    public List<AbstractMinecart> cars() {
        return cars;
    }

    /** How many cars this train has. */
    public int size() {
        return cars.size();
    }

    /** Whether {@code car} is one of this train's. */
    public boolean contains(AbstractMinecart car) {
        return cars.contains(car);
    }

    /**
     * The one car that settles this train: its head, or the lowest-UUID car of a train that closes
     * on itself and so has no head. Exactly one car of any train answers itself here.
     */
    public @Nullable AbstractMinecart settler() {
        return closed ? lowest(cars) : cars.getFirst();
    }

    /**
     * The car {@code car} follows, or null if it heads the train, is unlinked, or the train closes
     * on itself and so has no head to follow towards.
     */
    public @Nullable AbstractMinecart follows(AbstractMinecart car) {
        if (closed) {
            return null;
        }

        int index = cars.indexOf(car);

        return index > 0 ? cars.get(index - 1) : null;
    }

    // Walks the whole train from car and orders it head first. The head is whichever of the two
    // ends has the lower UUID, which is the one thing about a train that both of its ends agree on
    // without either having to be told. Only the ends are compared: a car in the middle cannot head
    // a train however low its own UUID.
    private static TrainSnapshot build(AbstractMinecart car, long tick) {
        List<AbstractMinecart> order = new ArrayList<>();
        AbstractMinecart end = walk(car, null);

        if (end == null) {
            walk(car, order);
            return new TrainSnapshot(order, true, tick);
        }

        walk(end, order);

        if (order.size() > 1 && sortsFirst(order.getLast(), order.getFirst())) {
            Collections.reverse(order);
        }

        return new TrainSnapshot(order, false, tick);
    }

    // Follows the links away from start, collecting into sink when one is given. Returns the car
    // the track runs out at, or null if it comes back round on itself.
    private static @Nullable AbstractMinecart walk(
            AbstractMinecart start, @Nullable List<AbstractMinecart> sink) {
        AbstractMinecart previous = null;
        AbstractMinecart current = start;

        for (int steps = 0; steps <= MAX_WALK; steps++) {
            if (sink != null) {
                sink.add(current);
            }

            AbstractMinecart onward = onward(current, previous);

            if (onward == null) {
                return current;
            }

            if (onward == start) {
                return null;
            }

            previous = current;
            current = onward;
        }

        return null;
    }

    // The neighbour of car that is not the one it was reached from.
    private static @Nullable AbstractMinecart onward(
            AbstractMinecart car, @Nullable AbstractMinecart previous) {
        for (AbstractMinecart neighbour : car.getNeighbours()) {
            if (neighbour != previous) {
                return neighbour;
            }
        }

        return null;
    }

    // The car of the lowest UUID, which is the tie-break every car of a train reaches the same way.
    private static @Nullable AbstractMinecart lowest(List<AbstractMinecart> cars) {
        AbstractMinecart best = null;

        for (AbstractMinecart car : cars) {
            if (best == null || sortsFirst(car, best)) {
                best = car;
            }
        }

        return best;
    }

    // Whether a's UUID sorts before b's.
    private static boolean sortsFirst(AbstractMinecart a, AbstractMinecart b) {
        return a.getUUID().compareTo(b.getUUID()) < 0;
    }
}
