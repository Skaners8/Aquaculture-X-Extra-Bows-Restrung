package net.flick.aquaculture_extra_bows.item.custom;

import net.flick.aquaculture_extra_bows.AquacultureExtraBows;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(AquacultureExtraBows.MOD_ID);

    public static final DeferredItem<Item> GOLDEN_BOW =
            ITEMS.register("golden_bow",
                    () -> new BowItem(new Item.Properties().durability(32)));

    public static final DeferredItem<Item> COPPER_BOW =
            ITEMS.register("copper_bow",
                    () -> new BowItem(new Item.Properties().durability(190)));

    public static final DeferredItem<Item> IRON_BOW =
            ITEMS.register("iron_bow",
                    () -> new BowItem(new Item.Properties().durability(250)));

    public static final DeferredItem<Item> DIAMOND_BOW =
            ITEMS.register("diamond_bow",
                    () -> new BowItem(new Item.Properties().durability(1561)));

    public static final DeferredItem<Item> NETHERITE_BOW =
            ITEMS.register("netherite_bow",
                    () -> new BowItem(new Item.Properties().durability(2031)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
