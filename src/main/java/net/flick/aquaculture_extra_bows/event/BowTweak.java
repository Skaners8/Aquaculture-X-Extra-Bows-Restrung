package net.flick.aquaculture_extra_bows.event;

import com.teammetallurgy.aquaculture.init.AquaItems;
import net.flick.aquaculture_extra_bows.item.custom.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

import java.util.List;

import static net.flick.aquaculture_extra_bows.AquacultureExtraBows.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class BowTweak {

    private static final double NEPTUNIUM_BASE_DAMAGE = 8.5;

    @SubscribeEvent
    public static void onModifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        event.modify(net.minecraft.world.item.Items.BOW, builder ->
                builder.set(DataComponents.MAX_DAMAGE, 59));

        event.modify(AquaItems.NEPTUNIUM_BOW.get(), builder ->
                builder.set(DataComponents.MAX_DAMAGE, 1796));

    }

    // ---------------------------------------------
    //          TOOLTIP DU NEPTUNIUM BOW
    // ---------------------------------------------
    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {

        ItemStack stack = event.getItemStack();
        List<Component> tooltip = event.getToolTip();
        if (tooltip == null || tooltip.isEmpty()) return;

        // ----------------------------------------
        //  TOOLTIP NEPTUNIUM BOW
        // ----------------------------------------
        if (stack.is(AquaItems.NEPTUNIUM_BOW.get())) {

            double baseDamage = 8.5;
            Component dmgLine = Component.literal(baseDamage + " ")
                    .append(Component.translatable("attribute.name.generic.attack_damage"))
                    .withStyle(ChatFormatting.DARK_GREEN);

            insertBeforeDurability(tooltip, dmgLine);
            return;
        }

        // ----------------------------------------
        //  TOOLTIP BOW VANILLA
        // ----------------------------------------
        if (stack.is(net.minecraft.world.item.Items.BOW)) {

            double baseDamage = 5.0; // dégâts du bow normal
            Component dmgLine = Component.literal(baseDamage + " ")
                    .append(Component.translatable("attribute.name.generic.attack_damage"))
                    .withStyle(ChatFormatting.DARK_GREEN);

            insertBeforeDurability(tooltip, dmgLine);
        }
    }

    private static void insertBeforeDurability(List<Component> tooltip, Component line) {
        int durabilityIndex = -1;

        for (int i = 0; i < tooltip.size(); i++) {
            String lower = tooltip.get(i).getString().toLowerCase();
            if (lower.contains("durab") || lower.contains("utilisations") || lower.contains("uses")) {
                durabilityIndex = i;
                break;
            }
        }

        if (durabilityIndex == -1) {
            tooltip.add(line); // fallback
        } else {
            tooltip.add(durabilityIndex, line); // le bon emplacement
        }
    }
}
