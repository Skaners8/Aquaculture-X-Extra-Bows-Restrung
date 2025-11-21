package net.flick.aquaculture_extra_bows.mixin;

import net.flick.aquaculture_extra_bows.util.BowStats;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class AbstractArrow_OnHitMixin {

    @Shadow private ItemStack firedFromWeapon;

    @Inject(method = "onHitEntity", at = @At("HEAD"))
    private void aeb_applyCustomDamage(EntityHitResult hit, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow)(Object)this;

        if (arrow.level().isClientSide()) return;

        Entity shooter = arrow.getOwner();
        if (!(shooter instanceof LivingEntity living)) return;

        ItemStack bow = living.getMainHandItem();
        if (!(bow.getItem() instanceof BowItem)) return;

        double base = arrow.getBaseDamage();

        // power déjà modifié par MEV
        int power = EnchantmentHelper.getItemEnchantmentLevel(
                living.level().registryAccess().registryOrThrow(Registries.ENCHANTMENT)
                        .getHolderOrThrow(Enchantments.POWER),
                bow
        );

        // ---- BOW MULTIPLIER ----
        double scaled = base * BowStats.getMultiplier(bow.getItem());

        // ---- TON BONUS CUSTOM FINAL ----
        // P1 → +1
        // P2 → +1.5
        // etc
        double custom = (power > 0) ? 1 + (power - 1) * 0.5 : 0;

        arrow.setBaseDamage(scaled + custom);
    }
}
