package club.rainbowkitty.minecarttweaks.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;

import club.rainbowkitty.minecarttweaks.MinecartTweaks;

/** Damage type keys this mod defines. */
public final class MTDamageTypes {

    /** Damage a moving cart deals to anything it runs into. */
    public static final ResourceKey<DamageType> MINECART_DAMAGE =
            ResourceKey.create(Registries.DAMAGE_TYPE, MinecartTweaks.id("minecart"));

    private MTDamageTypes() {}
}
