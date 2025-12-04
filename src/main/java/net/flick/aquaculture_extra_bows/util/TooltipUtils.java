package net.flick.aquaculture_extra_bows.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

public class TooltipUtils {

    public static void insertDamageTooltip(List<Component> tooltip, double damage) {

        Component line = Component.literal(damage + " ")
                .append(Component.translatable("attribute.name.generic.attack_damage"))
                .withStyle(ChatFormatting.DARK_GREEN);

        int durabilityIndex = -1;

        for (int i = 0; i < tooltip.size(); i++) {
            String s = tooltip.get(i).getString().toLowerCase();
            if (s.contains("durab") || s.contains("uses") || s.contains("utilisations")) {
                durabilityIndex = i;
                break;
            }
        }

        if (durabilityIndex == -1) {
            tooltip.add(line);
        } else {
            tooltip.add(durabilityIndex, line);
        }
    }
}
