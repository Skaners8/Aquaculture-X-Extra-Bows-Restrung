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

    // Annule le Mth.ceil()
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

    // Applique le dégât custom FIXE
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

        Entity shooter = arrow.getOwner();
        if (!(shooter instanceof LivingEntity living)) {
            return target.hurt(source, ignoredVanillaDamage);
        }

        ItemStack bow = living.getMainHandItem();
        if (!(bow.getItem() instanceof BowItem)) {
            return target.hurt(source, ignoredVanillaDamage);
        }

        //----- BASE DAMAGE PAR TYPE D’ARC -----
        double baseDamage = getBaseDamageForBow(bow);

        //----- POWER -----
        int power = EnchantmentHelper.getItemEnchantmentLevel(
                arrow.level().registryAccess()
                        .registryOrThrow(Registries.ENCHANTMENT)
                        .getHolderOrThrow(Enchantments.POWER),
                bow
        );

        double damage = baseDamage + (power * 0.5);

        //----- ARRONDI .0 / .5 -----
        damage = Math.round(damage * 2.0) / 2.0;

        // DEBUG
        System.out.println("==== CUSTOM ARROW DMG ====");
        System.out.println("BOW DMG      = " + baseDamage);
        System.out.println("POWER LVL    = " + power);
        System.out.println("FINAL DMG    = " + damage);
        System.out.println("==========================");

        return target.hurt(source, (float) damage);
    }


    private double getBaseDamageForBow(ItemStack bow) {
        String id = bow.getItem().toString().toLowerCase();

        if(id.contains("golden_bow"))     return 5.0;
        if(id.contains("copper_bow"))     return 6.0;
        if(id.contains("iron_bow"))       return 7.0;
        if(id.contains("diamond_bow"))    return 8.0;
        if(id.contains("neptunium_bow"))  return 8.5;
        if(id.contains("netherite_bow"))  return 9.0;

        return 5.0; // wooden bow
    }
}
