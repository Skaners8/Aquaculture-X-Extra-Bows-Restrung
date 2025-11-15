package net.flick.aquaculture_extra_bows.mixin;

import com.teammetallurgy.aquaculture.item.neptunium.NeptuniumBow;
import net.flick.aquaculture_extra_bows.util.BowStats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
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
public abstract class ArrowDamageMixin {

    @Shadow private double baseDamage;

    @Inject(method = "hurtTarget", at = @At("HEAD"), cancellable = true)
    private void replaceDamage(EntityHitResult hit, CallbackInfo ci) {
        AbstractArrow arrow = (AbstractArrow)(Object)this;
        Entity target = hit.getEntity();

        if (!(target instanceof LivingEntity living)) return;

        Entity owner = arrow.getOwner();
        if (!(owner instanceof LivingEntity shooter)) return;

        ItemStack bow = shooter.getUseItem();
        if (bow.isEmpty()) return;

        // SPEED
        double speed = arrow.getDeltaMovement().length();

        // MULTIPLIER (ton système)
        double multiplier = BowStats.getMultiplier(bow.getItem());

        // DAMAGE
        double damage = speed * multiplier;

        // POWER ENCHANT
        int power = EnchantmentHelper.getItemEnchantmentLevel(
                shooter.level().registryAccess().holderOrThrow(Enchantments.POWER),
                bow
        );
        if (power > 0) {
            damage += (0.5 * power + 0.5);
        }

        // APPLY DAMAGE
        DamageSource src = arrow.damageSources().arrow(arrow, owner);
        living.hurt(src, (float) damage);

        // ✔ DO NOT CANCEL LOGIC AFTER
        // we let vanilla handle knockback, stopping, stuck arrow, discard
        // only damage is overridden
    }
}
