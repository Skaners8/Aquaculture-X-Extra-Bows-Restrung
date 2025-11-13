package net.flick.aquaculture_extra_bows.mixin;

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
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public abstract class ArrowDamageMixin {

    @Inject(method = "onHitEntity", at = @At("HEAD"), cancellable = true)
    private void onHitEntity(EntityHitResult result, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow) (Object) this;
        Entity target = result.getEntity();
        Entity owner = arrow.getOwner();

        if (!(target instanceof LivingEntity living)) return;

        ItemStack weapon = arrow.getWeaponItem();
        double bowMultiplier = 1.0;

        if (weapon != null) {
            Item bowItem = weapon.getItem();
            String name = bowItem.toString().toLowerCase();

            if (name.contains("copper")) bowMultiplier = 1.10;
            else if (name.contains("iron")) bowMultiplier = 1.15;
            else if (name.contains("gold")) bowMultiplier = 1.20;
            else if (name.contains("diamond")) bowMultiplier = 1.30;
            else if (name.contains("neptunium")) bowMultiplier = 1.40;
        }

        double velocity = arrow.getDeltaMovement().length();
        double baseDamage = arrow.getBaseDamage();
        double damage = baseDamage * velocity * bowMultiplier;

        if (weapon != null) {
            var registry = arrow.level()
                    .registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT);

            // Power
            Holder<Enchantment> powerHolder = registry.getHolderOrThrow(Enchantments.POWER);
            int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(powerHolder, weapon);
            if (powerLevel > 0) {
                double powerMultiplier = 1.0 + 0.20 * (powerLevel + 1); // 20% par niveau, au lieu de 25%
                damage *= powerMultiplier;
            }

            // Flame
            Holder<Enchantment> flame = registry.getHolderOrThrow(Enchantments.FLAME);
            if (EnchantmentHelper.getItemEnchantmentLevel(flame, weapon) > 0) {
                arrow.setRemainingFireTicks(100); // 5 s
            }

            // Punch
            Holder<Enchantment> punch = registry.getHolderOrThrow(Enchantments.PUNCH);
            int punchLevel = EnchantmentHelper.getItemEnchantmentLevel(punch, weapon);
            if (punchLevel > 0 && owner != null) {
                living.knockback(punchLevel * 0.5,
                        Math.sin(Math.toRadians(owner.getYRot())),
                        -Math.cos(Math.toRadians(owner.getYRot())));
            }
        }

        DamageSource source = arrow.damageSources().arrow(arrow, owner);
        living.hurt(source, (float) damage);

        ci.cancel(); // empêche la logique vanilla
    }
}
