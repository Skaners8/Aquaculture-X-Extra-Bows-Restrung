package net.flick.aquaculture_extra_bows.mixin;

import net.flick.aquaculture_extra_bows.util.BowStats;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrow_OnHitMixin {

    /**
     * Apply BowStats and Power enchant BEFORE vanilla final damage.
     */
    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void aeb_applyCustomDamage(EntityHitResult hit, CallbackInfo ci) {

        AbstractArrow arrow = (AbstractArrow)(Object)this;

        // server only
        if (arrow.level().isClientSide()) return;

        Entity shooter = arrow.getOwner();
        if (!(shooter instanceof LivingEntity living)) return;

        // Ensure the shooter used a bow
        ItemStack bow = living.getMainHandItem();
        if (!(bow.getItem() instanceof BowItem)) return;

        // =============== 1. BowStats multiplier ===============
        double mult = BowStats.getMultiplier(bow.getItem());

        // Vanilla baseDamage was already calculated earlier (velocity * 2)
        double base = arrow.getBaseDamage();

        double finalDamage = base * mult;

        // =============== 2. Power enchantment (Sharpness-style) ===============
        Level lvl = arrow.level();
        var registry = lvl.registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        Holder<Enchantment> powerHolder = registry.getHolderOrThrow(Enchantments.POWER);
        int power = EnchantmentHelper.getItemEnchantmentLevel(powerHolder, bow);

        if (power > 0) {
            // Sharpness-style additive bonus:
            double powerBonus = 1.0 + (power - 1) * 0.5;
            finalDamage += powerBonus;
        }

        // =============== 3. Apply deterministic final damage ===============
        arrow.setBaseDamage(finalDamage);
    }
}
