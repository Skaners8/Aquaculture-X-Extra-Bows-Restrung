package net.flick.aquaculture_extra_bows.item.custom;

import net.flick.aquaculture_extra_bows.AquacultureExtraBows;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AquacultureExtraBows.MOD_ID);

    public static final Supplier<CreativeModeTab> Extra_Bows_Tab = CREATIVE_MODE_TAB.register("extra_bows_tabs",
            () -> CreativeModeTab.builder().icon(()-> new ItemStack(ModItems.IRON_BOW.get()))
                    .title(Component.translatable("creativetab.aquaculture_extra_bows.extra_bows"))
                    .displayItems((itemDisplayParameters, output)-> {

                        output.accept(ModItems.GOLDEN_BOW);
                        output.accept(ModItems.COPPER_BOW);
                        output.accept(ModItems.IRON_BOW);

                    }).build());

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }

}
