package net.flick.aquaculture_extra_bows.mixin;

import com.teammetallurgy.aquaculture.item.neptunium.NeptuniumBow;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mixin(NeptuniumBow.class)
public abstract class NeptuniumBowMixin {


    @Inject(method = "customArrow", at = @At("RETURN"), cancellable = true)
    private void injectCustomArrow(AbstractArrow arrowEntity, ItemStack projectileStack, ItemStack weaponStack, CallbackInfoReturnable<AbstractArrow> cir) {
        AbstractArrow arrow = cir.getReturnValue();

        Level level = arrowEntity.level(); // flèche knows its level
        ItemStack bow = weaponStack; // le weaponStack est le bow

        Holder<Enchantment> power = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getHolderOrThrow(Enchantments.POWER);
        int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(power, bow);


        double damage = arrow.getBaseDamage();

        arrow.setBaseDamage(damage);
        // Log pour vérifier le mixin
        LOGGER.info("[NeptuniumBowMixin] Mixin appliqué : flèche tirée par {} avec baseDamage={}", arrow.getOwner(), damage);
        cir.setReturnValue(arrow);
    }
}
