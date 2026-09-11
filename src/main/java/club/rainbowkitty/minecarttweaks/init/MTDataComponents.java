package club.rainbowkitty.minecarttweaks.init;

import eu.pb4.polymer.core.api.other.PolymerComponent;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;

import club.rainbowkitty.minecarttweaks.MinecartTweaks;
import club.rainbowkitty.minecarttweaks.datacomponent.SelectedCart;

/** Data-component type registrations. */
public final class MTDataComponents {

    /** Stores the UUID of the cart selected as one end during a link operation. */
    public static final DataComponentType<SelectedCart> SELECTED_CART =
            DataComponentType.<SelectedCart>builder()
                    .persistent(SelectedCart.CODEC)
                    .networkSynchronized(SelectedCart.STREAM_CODEC)
                    .build();

    private MTDataComponents() {}

    /** Registers all data component types into the vanilla registry. */
    public static void init() {
        Registry.register(
                BuiltInRegistries.DATA_COMPONENT_TYPE,
                MinecartTweaks.id("selected_cart"),
                SELECTED_CART);

        // Marks the type server-only so registry sync ignores it and the value is stripped from
        // client-bound stacks; only the server ever reads it.
        PolymerComponent.registerDataComponent(SELECTED_CART);
    }
}
