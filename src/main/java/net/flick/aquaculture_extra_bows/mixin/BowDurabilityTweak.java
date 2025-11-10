package net.flick.aquaculture_extra_bows.mixin;


import com.teammetallurgy.aquaculture.init.AquaItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;

import static net.flick.aquaculture_extra_bows.AquacultureExtraBows.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class BowDurabilityTweak {
    @SubscribeEvent
    public static void onModifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        event.modify(Items.BOW, builder -> {
            builder.set(net.minecraft.core.component.DataComponents.MAX_DAMAGE, 59);
        });
        event.modify(AquaItems.NEPTUNIUM_BOW.get(), builder -> {
            builder.set(DataComponents.MAX_DAMAGE, 1796);
        });
    }
}

