package net.flick.aquaculture_extra_bows.item.custom;

import net.flick.aquaculture_extra_bows.AquacultureExtraBows;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {


    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AquacultureExtraBows.MOD_ID);

    public static final DeferredItem<Item> COPPER_BOW = ITEMS.register("copper_bow",
            () -> new BowItem(new Item.Properties().durability(191)));

    public static final DeferredItem<Item> IRON_BOW = ITEMS.register("iron_bow",
            () -> new BowItem(new Item.Properties().durability(250)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
