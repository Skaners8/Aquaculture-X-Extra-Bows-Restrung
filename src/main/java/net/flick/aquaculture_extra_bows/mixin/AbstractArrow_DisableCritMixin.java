package net.flick.aquaculture_extra_bows.mixin;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Prevent setting the crit flag so vanilla never applies the random critical extra damage.
 * We cancel setCritArrow(true) calls while allowing setCritArrow(false) (if ever used).
 */
@Mixin(AbstractArrow.class)
public abstract class AbstractArrow_DisableCritMixin {

    @Inject(method = "setCritArrow", at = @At("HEAD"), cancellable = true)
    private void onSetCritArrow(boolean crit, CallbackInfo ci) {
        // If code tries to enable crit behaviour, block it.
        if (crit) {
            ci.cancel();
        }
        // if crit == false, allow method to proceed so state can be cleared normally (safe).
    }
}
