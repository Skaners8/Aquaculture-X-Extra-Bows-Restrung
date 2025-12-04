package net.flick.aquaculture_extra_bows.event;

import net.flick.aquaculture_extra_bows.AquacultureExtraBows;
import net.flick.aquaculture_extra_bows.util.TooltipUtils;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = AquacultureExtraBows.MOD_ID)
public class BowTooltipEvent {

    @SubscribeEvent
    public static void onTooltip(ItemTooltipEvent event) {

        ItemStack stack = event.getItemStack();
        String id = stack.getItem().toString();

        double dmg =
                id.contains("golden_bow") ? 5.0 :
                        id.contains("copper_bow") ? 6.0 :
                                id.contains("iron_bow") ? 7.0 :
                                        id.contains("diamond_bow") ? 8.0 :
                                                id.contains("netherite_bow") ? 9.0 :
                                                        -1;

        if (dmg == -1) return;

        TooltipUtils.insertDamageTooltip(event.getToolTip(), dmg);
    }
}
