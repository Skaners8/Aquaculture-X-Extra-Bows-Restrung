package net.flick.aquaculture_extra_bows.event;

import net.flick.aquaculture_extra_bows.AquacultureExtraBows;
import net.flick.aquaculture_extra_bows.item.custom.ModItems;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;

import java.util.Set;

@EventBusSubscriber(modid = AquacultureExtraBows.MOD_ID, value = Dist.CLIENT)
public class ModClientEvents {
    @SubscribeEvent
    public static void onComputeFovModifierEvent(ComputeFovModifierEvent event) {
        if (event.getPlayer().isUsingItem()) {
            Item current = event.getPlayer().getUseItem().getItem();

            // Vérifie à ce moment seulement, quand tout est enregistré
            if (current == ModItems.COPPER_BOW.get()
                    || current == ModItems.IRON_BOW.get()
                    ) {

                float fovModifier = 1f;
                int ticksUsingItem = event.getPlayer().getTicksUsingItem();
                float deltaTicks = (float)ticksUsingItem / 20f;
                if (deltaTicks > 1f) {
                    deltaTicks = 1f;
                } else {
                    deltaTicks *= deltaTicks;
                }
                fovModifier *= 1f - deltaTicks * 0.15f;
                event.setNewFovModifier(fovModifier);
            }
        }
    }

}