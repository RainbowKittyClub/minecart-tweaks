package club.rainbowkitty.minecarttweaks.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import club.rainbowkitty.minecarttweaks.MinecartTweaks;

/** Tag keys this mod defines. */
public final class MTTags {

    // Static-only; blocks instantiation.
    private MTTags() {}

    /** Item tag keys. */
    public static final class Items {

        /** Items that link two carts together when shift-used on them. */
        public static final TagKey<Item> LINK_ITEMS =
                TagKey.create(Registries.ITEM, MinecartTweaks.id("link_items"));

        // Static-only; blocks instantiation.
        private Items() {}
    }
}
