package net.flick.aquaculture_extra_bows.mixin;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrow_CustomDamageMixin {

    // Désactive le Mth.ceil() vanilla
    @Redirect(
            method = "onHitEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/Mth;ceil(D)I"
            )
    )
    private int disableVanillaCeil(double value) {
        return (int) value; // ignoré ensuite
    }

    // Applique nos dégâts custom FIXES
    @Redirect(
            method = "onHitEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
            )
    )
    private boolean applyCustomDamage(
            Entity target,
            DamageSource source,
            float ignoredVanillaDamage
    ) {

        AbstractArrow arrow = (AbstractArrow)(Object)this;

        Entity owner = arrow.getOwner();
        if (!(owner instanceof LivingEntity living)) {
            return target.hurt(source, ignoredVanillaDamage);
        }

        ItemStack bow = living.getMainHandItem();
        if (!(bow.getItem() instanceof BowItem)) {
            return target.hurt(source, ignoredVanillaDamage);
        }

        // ----------- POWER -----------

        int power = EnchantmentHelper.getItemEnchantmentLevel(
                arrow.level().registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT)
                        .getHolderOrThrow(Enchantments.POWER),
                bow
        );

        // ----------- FORMULE EXACTE QUE TU VEUX -----------

        double damage = 5.0 + (power * 0.5);

        // ----------- ARRONDI .0 / .5 -----------

        damage = Math.round(damage * 2.0) / 2.0;

        // ----------- DEBUG -----------

        System.out.println("==== CUSTOM ARROW DMG ====");
        System.out.println("POWER LVL    = " + power);
        System.out.println("FINAL DMG    = " + damage);
        System.out.println("==========================");

        return target.hurt(source, (float)damage);
    }
}
