package net.flick.aquaculture_extra_bows.mixin;

import net.minecraft.world.entity.projectile.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractArrow.class)
public abstract class PowerEnchantmentMixin {

    @Redirect(
            method = "applyEnchantmentEffects",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/AbstractArrow;setBaseDamage(D)V"
            )
    )
    private void aeb_blockVanillaPowerBonus(AbstractArrow arrow, double newDamage) {
        /*
            IMPORTANT :
            Cette méthode est appelée DEUX FOIS dans applyEnchantmentEffects :

            1) Une première fois pour appliquer les modifiers "Punch", "Flame", etc.
            2) Une seconde fois pour appliquer le bonus VANILLA de Power.

            On veut bloquer SEULEMENT la seconde.

            Condition : la deuxième fois, vanilla appelle setBaseDamage()
            AVEC une valeur != baseDamage initiale.

            Donc : on NE change rien dans la première invocation.
        */

        double current = arrow.getBaseDamage();

        // Si vanilla essaye d'ajouter le bonus Power -> ignore
        if (newDamage > current) {
            // On bloque le bonus vanilla en réappliquant la valeur de base (pas modifiée)
            arrow.setBaseDamage(current);
            return;
        }

        // Sinon -> c'est un appel légitime, on le laisse passer
        arrow.setBaseDamage(newDamage);
    }
}
