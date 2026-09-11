package club.rainbowkitty.minecarttweaks.init;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentTarget;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;

import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;

import club.rainbowkitty.minecarttweaks.MinecartTweaks;

/**
 * This mod's entity attachments. Persistent but unsynced — the chain between linked carts is drawn
 * server-side (S7), so the client never needs the value.
 */
public final class MTAttachments {

    /** The link-state attachment carried by every linked cart. */
    public static final AttachmentType<LinkableData> LINK =
            AttachmentRegistry.createPersistent(MinecartTweaks.id("link"), LinkableData.CODEC);

    private MTAttachments() {}

    /** Forces class initialization so the type is registered before the first world loads. */
    public static void init() {}

    // AttachmentTarget is mixed into Entity at runtime rather than interface-injected at compile
    // time, so every call site would otherwise need this cast.
    /** Returns the cart's link state, or {@link LinkableData#EMPTY} if it has never been linked. */
    public static LinkableData getLink(AbstractMinecart minecart) {
        return ((AttachmentTarget) minecart).getAttachedOrElse(LINK, LinkableData.EMPTY);
    }

    /** Replaces the cart's link state. */
    public static void setLink(AbstractMinecart minecart, LinkableData data) {
        ((AttachmentTarget) minecart).setAttached(LINK, data);
    }
}
