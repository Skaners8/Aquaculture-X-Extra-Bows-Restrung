package net.flick.aquaculture_extra_bows.mixin;

import com.teammetallurgy.aquaculture.init.AquaItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Mixin(AbstractArrow.class)
public abstract class ArrowDamageMixin {

    private static final Logger LOGGER = LogManager.getLogger("AquacultureExtraBows");

    private static final double BASE_ARROW_DAMAGE = 3.0d;

    private static final Map<Item, Double> BOW_MULTIPLIERS = new ConcurrentHashMap<>();
    private static volatile boolean multipliersInitialized = false;

    private static void initMultipliersIfNeeded() {
        if (multipliersInitialized) return;
        synchronized (BOW_MULTIPLIERS) {
            if (multipliersInitialized) return;

            // Base vanilla bow
            BOW_MULTIPLIERS.put(Items.BOW, 1.0);

            // Try register Neptunium safely
            try {
                BOW_MULTIPLIERS.put(AquaItems.NEPTUNIUM_BOW.get(), 1.30);
            } catch (Exception ignored) {}

            multipliersInitialized = true;
        }
    }

    private static double getMultiplierForItem(Item item) {
        initMultipliersIfNeeded();

        Double m = BOW_MULTIPLIERS.get(item);
        if (m != null) return m;

        String id = item.toString().toLowerCase();

        if (id.contains("wood")) return 0.80d;
        if (id.contains("gold")) return 0.80d;
        if (id.contains("copper")) return 1.10d;
        if (id.contains("iron")) return 1.20d;
        if (id.contains("diamond")) return 1.40d;
        if (id.contains("neptunium")) return 1.50d;
        if (id.contains("netherite")) return 1.60d;

        return 0.80d; // fallback
    }

    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void onHitEntity(EntityHitResult result, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow)(Object)this;
        Entity target = result.getEntity();
        Entity owner = arrow.getOwner();

        if (!(target instanceof LivingEntity living))
            return;

        ItemStack weapon = arrow.getWeaponItem();
        double bowMultiplier = 1.0;

        if (weapon != null) {
            bowMultiplier = getMultiplierForItem(weapon.getItem());
        }

        double velocity = arrow.getDeltaMovement().length();
        double damage = BASE_ARROW_DAMAGE * velocity * bowMultiplier;

        // ---- ENCHANTMENTS ----

        if (weapon != null) {
            var registry = arrow.level()
                    .registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT);

            // POWER – final formula
            Holder<Enchantment> powerHolder = registry.getHolderOrThrow(Enchantments.POWER);
            int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(powerHolder, weapon);
            if (powerLevel > 0) {
                double powerMultiplier = 1.0 + 0.20 * (powerLevel + 1);
                damage *= powerMultiplier;
            }

            // FLAME
            Holder<Enchantment> flameHolder = registry.getHolderOrThrow(Enchantments.FLAME);
            if (EnchantmentHelper.getItemEnchantmentLevel(flameHolder, weapon) > 0) {
                arrow.setRemainingFireTicks(100); // 5s fire
            }

            // PUNCH
            Holder<Enchantment> punchHolder = registry.getHolderOrThrow(Enchantments.PUNCH);
            int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(punchHolder, weapon);
            if (punchLevel > 0 && owner != null) {
                living.knockback(
                        punchLevel * 0.5F,
                        Math.sin(Math.toRadians(owner.getYRot())),
                        -Math.cos(Math.toRadians(owner.getYRot()))
                );
            }

            // QUICK CHARGE laissé tel quel — pas encore bloqué, comme demandé
        }

        DamageSource source = arrow.damageSources().arrow(arrow, owner);
        living.hurt(source, (float)damage);

        ci.cancel();
    }
}
