package net.flick.aquaculture_extra_bows.event;

import com.teammetallurgy.aquaculture.init.AquaItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
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
        if (!stack.is(AquaItems.NEPTUNIUM_BOW.get())) return;

        List<Component> tooltip = event.getToolTip();
        if (tooltip == null || tooltip.isEmpty()) return;

        // ----------------------------------------
        //   Ligne de dégâts à afficher
        // ----------------------------------------
        double baseDamage = 8.5;
        Component dmgLine = Component.literal(baseDamage + " ")
                .append(Component.translatable("attribute.name.generic.attack_damage"))
                .withStyle(ChatFormatting.DARK_GREEN);

        // ----------------------------------------
        //   Trouver la position de la durabilité
        // ----------------------------------------
        int durabilityIndex = -1;

        for (int i = 0; i < tooltip.size(); i++) {
            String text = tooltip.get(i).getString().toLowerCase();

            // "Durability", "Utilisations", "Uses" selon langue
            if (text.contains("durab") || text.contains("utilisations") || text.contains("uses")) {
                durabilityIndex = i;
                break;
            }
        }

        // Si pas trouvé, tout en bas
        if (durabilityIndex == -1) durabilityIndex = tooltip.size();

        // ----------------------------------------
        //   INSÉRER juste avant durabilité
        // ----------------------------------------
        tooltip.add(durabilityIndex, dmgLine);
    }
}
