package club.rainbowkitty.minecarttweaks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.world.entity.vehicle.minecart.NewMinecartBehavior;
import net.minecraft.world.phys.AABB;

import club.rainbowkitty.minecarttweaks.util.CartInteraction;

/** Holds vanilla's pickup to the speed the resolver allows one at. */
@Mixin(NewMinecartBehavior.class)
public abstract class NewMinecartBehaviorMixin implements MinecartBehaviorAccessor {

    // Vanilla picks up on every move, whether or not the cart collided with anything, so a cart
    // barrelling into a mob would scoop it aboard rather than run it down no matter what the
    // resolver decided. This is the one place that can stop it.
    @Inject(method = "pickupEntities", at = @At("HEAD"), cancellable = true)
    private void minecarttweaks$onlyCollectSlowly(
            AABB hitbox, CallbackInfoReturnable<Boolean> cir) {
        if (!CartInteraction.gathers(minecarttweaks$cart())) {
            cir.setReturnValue(false);
        }
    }
}
