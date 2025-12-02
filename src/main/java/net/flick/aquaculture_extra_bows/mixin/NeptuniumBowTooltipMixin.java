package net.flick.aquaculture_extra_bows.mixin;

import com.teammetallurgy.aquaculture.item.neptunium.NeptuniumBow;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(NeptuniumBow.class)
public abstract class NeptuniumBowTooltipMixin {

    private static final double NEPTUNIUM_BASE_DAMAGE = 8.5; // <<< TA VALEUR FIXE

    // ----------- SIGNATURE 1 -----------
    @Inject(method = "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/Level;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V",
            at = @At("TAIL"), remap = true)
    private void injectTooltipLevel(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        addFixedDamageLine(tooltip);
    }

    // ----------- SIGNATURE 2 -----------
    @Inject(method = "appendHoverText(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/TooltipContext;Ljava/util/List;Lnet/minecraft/world/item/TooltipFlag;)V",
            at = @At("TAIL"), remap = true)
    private void injectTooltipContext(ItemStack stack, Object ctx, List<Component> tooltip, TooltipFlag flag, CallbackInfo ci) {
        addFixedDamageLine(tooltip);
    }


    private void addFixedDamageLine(List<Component> tooltip) {

        Component dmgLine = Component.literal(NEPTUNIUM_BASE_DAMAGE + " ")
                .append(Component.translatable("attribute.name.generic.attack_damage"))
                .withStyle(ChatFormatting.DARK_GREEN);

        // chercher la ligne de durabilité
        int durabilityIndex = -1;
        for (int i = 0; i < tooltip.size(); i++) {
            String s = tooltip.get(i).getString().toLowerCase();
            if (s.contains("durab") || s.contains("durabilité") || s.contains("uses")) {
                durabilityIndex = i;
                break;
            }
        }

        if (durabilityIndex != -1) {
            tooltip.add(durabilityIndex, Component.empty());
            tooltip.add(durabilityIndex, dmgLine);
        } else {
            // fallback : avant dernière ligne (durabilité est souvent la dernière)
            int idx = Math.max(0, tooltip.size() - 1);
            tooltip.add(idx, Component.empty());
            tooltip.add(idx, dmgLine);
        }
    }
}
