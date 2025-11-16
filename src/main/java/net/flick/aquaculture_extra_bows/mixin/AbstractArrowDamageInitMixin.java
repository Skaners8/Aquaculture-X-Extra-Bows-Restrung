package net.flick.aquaculture_extra_bows.mixin;

import net.flick.aquaculture_extra_bows.util.BowStats;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrowDamageInitMixin {

    private static final Logger LOGGER = LogManager.getLogger("AquacultureExtraBows");
    private static final double BASE_ARROW_DAMAGE = 3.0d;

    @Shadow private ItemStack firedFromWeapon;

    @Inject(method = "setBaseDamageFromMob", at = @At("HEAD"), cancellable = true)
    private void replaceBaseDamageFromMob(float velocity, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow)(Object)this;

        // --- 1) determine weapon reliably ---
        ItemStack weapon = this.firedFromWeapon;

        if (weapon == null || weapon.isEmpty()) {
            Entity owner = arrow.getOwner();
            if (owner instanceof LivingEntity living) {
                // prefer the currently used item (getUseItem)
                ItemStack use = living.getUseItem();
                if (use != null && !use.isEmpty() && use.getItem() instanceof BowItem) {
                    weapon = use;
                } else {
                    // fallback: scan hands for a BowItem
                    ItemStack main = living.getMainHandItem();
                    ItemStack off = living.getOffhandItem();
                    if (main != null && !main.isEmpty() && main.getItem() instanceof BowItem) {
                        weapon = main;
                    } else if (off != null && !off.isEmpty() && off.getItem() instanceof BowItem) {
                        weapon = off;
                    }
                }
            }
        }

        // --- 2) compute multiplier via BowStats (your canonical source) ---
        double bowMultiplier = 1.0d;
        String weaponId = "null";
        Item weaponItem = null;
        if (weapon != null && !weapon.isEmpty()) {
            weaponItem = weapon.getItem();
            weaponId = weaponItem.toString();
            try {
                bowMultiplier = BowStats.getMultiplier(weaponItem);
            } catch (Throwable t) {
                LOGGER.warn("[BowDebug] BowStats.getMultiplier threw: {}", t.toString());
                bowMultiplier = 1.0d;
            }
        } else {
            weaponId = "no-weapon-found";
        }

        // --- 3) compute deterministic damage (power included) ---
        double dmg = BASE_ARROW_DAMAGE * (double)velocity * bowMultiplier;

        try {
            Level lvl = arrow.level();
            if (lvl != null) {
                var registry = lvl.registryAccess().registryOrThrow(Registries.ENCHANTMENT);

                if (weapon != null && !weapon.isEmpty()) {
                    Holder<Enchantment> powerHolder = registry.getHolderOrThrow(Enchantments.POWER);
                    int power = EnchantmentHelper.getItemEnchantmentLevel(powerHolder, weapon);
                    if (power > 0) {
                        dmg *= (1.0 + 0.25 * power);
                    }

                    Holder<Enchantment> flameHolder = registry.getHolderOrThrow(Enchantments.FLAME);
                    if (EnchantmentHelper.getItemEnchantmentLevel(flameHolder, weapon) > 0) {
                        arrow.setRemainingFireTicks(100);
                    }
                }
            }
        } catch (Throwable ignored) {}

        // --- 4) write final damage into arrow (public setter) ---
        try {
            arrow.setBaseDamage(dmg);
        } catch (Throwable t) {
            LOGGER.error("[BowDebug] Failed to setBaseDamage: {}", t.toString());
        }

        // --- 5) debug log (important to debug why multipliers don't apply) ---
        LOGGER.info("[BowDebug] setBaseDamageFromMob: vel={} weapon={} multiplier={} dmg={}",
                velocity, weaponId, bowMultiplier, dmg);

        ci.cancel(); // prevent vanilla RNG version
    }
}
