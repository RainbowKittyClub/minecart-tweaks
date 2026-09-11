package club.rainbowkitty.minecarttweaks.init;

import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTabOutput;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import club.rainbowkitty.minecarttweaks.MinecartTweaks;
import club.rainbowkitty.minecarttweaks.item.WrenchItem;

/** Non-block item registrations. */
public final class MTItems {

    // Registry key the wrench is registered and given its settings under.
    private static final ResourceKey<Item> WRENCH_KEY =
            ResourceKey.create(Registries.ITEM, MinecartTweaks.id("wrench"));

    /** The wrench item. */
    public static final Item WRENCH =
            new WrenchItem(new Item.Properties().stacksTo(1).setId(WRENCH_KEY));

    private MTItems() {}

    /** Registers all non-block items into the vanilla registry. */
    public static void init() {
        Registry.register(BuiltInRegistries.ITEM, WRENCH_KEY, WRENCH);
    }

    /** Adds this mod's non-block items to the given creative-tab entry builder. */
    public static void addCreativeTabEntries(FabricCreativeModeTabOutput output) {
        output.insertAfter(Items.ACTIVATOR_RAIL, WRENCH);
    }
}
