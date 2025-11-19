package net.flick.aquaculture_extra_bows.mixin;

import net.flick.aquaculture_extra_bows.util.BowStats;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.DataComponentMap;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.WeakHashMap;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrow_OnHitMixin {

    @Shadow private ItemStack firedFromWeapon;

    private static final WeakHashMap<AbstractArrow, ItemStack> ORIGINAL_WEAPONS = new WeakHashMap<>();

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void aeb_applyCustomDamage(EntityHitResult hit, CallbackInfo ci) {

        AbstractArrow arrow = (AbstractArrow)(Object)this;

        if (arrow.level().isClientSide()) return;

        Entity shooter = arrow.getOwner();
        if (!(shooter instanceof LivingEntity living)) return;

        ItemStack bow = living.getMainHandItem();
        if (!(bow.getItem() instanceof BowItem)) return;

        double baseDamage = arrow.getBaseDamage();

        // --- POWER LEVEL ---
        int power = 0;
        try {
            Level lvl = arrow.level();
            var registry = lvl.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
            Holder<Enchantment> powerHolder = registry.getHolderOrThrow(Enchantments.POWER);
            power = EnchantmentHelper.getItemEnchantmentLevel(powerHolder, bow);
        } catch (Throwable ignored) {}

        // --- RETIRER BONUS VANILLA ---
        if (power > 0) {
            double vanillaPowerBonus = 0.5 * (power + 1);
            baseDamage -= vanillaPowerBonus;
        }

        // --- BOW MULTIPLIER ---
        double scaledDamage = baseDamage * BowStats.getMultiplier(bow.getItem());

        // --- BONUS CUSTOM POWER ---
        double customBonus = 0;
        if (power > 0) customBonus = 1 + (power - 1) * 0.5;

        // --- APPLY FINAL DAMAGE ---
        arrow.setBaseDamage(scaledDamage + customBonus);
    }

    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void afterHit(EntityHitResult hit, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow)(Object)this;
        ItemStack original = ORIGINAL_WEAPONS.remove(arrow);
        if (original != null) {
            this.firedFromWeapon = original;
        }
    }
}
