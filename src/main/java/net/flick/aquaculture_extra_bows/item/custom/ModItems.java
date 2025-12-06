package net.flick.aquaculture_extra_bows.item.custom;

import net.flick.aquaculture_extra_bows.AquacultureExtraBows;
import net.flick.aquaculture_extra_bows.event.BowTweak;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(AquacultureExtraBows.MOD_ID);

    public static final DeferredItem<Item> GOLDEN_BOW =
            ITEMS.register("golden_bow",
                    () -> new CustomEnchantableBow(
                            new Item.Properties().durability(32),
                            25  // enchantability
                    ));

    public static final DeferredItem<Item> COPPER_BOW =
            ITEMS.register("copper_bow",
                    () -> new CustomEnchantableBow(
                            new Item.Properties().durability(190),
                            10
                    ));

    public static final DeferredItem<Item> IRON_BOW =
            ITEMS.register("iron_bow",
                    () -> new CustomEnchantableBow(
                            new Item.Properties().durability(250),
                            14
                    ));

    public static final DeferredItem<Item> DIAMOND_BOW =
            ITEMS.register("diamond_bow",
                    () -> new CustomEnchantableBow(
                            new Item.Properties().durability(1561),
                            20
                    ));

    public static final DeferredItem<Item> NETHERITE_BOW =
            ITEMS.register("netherite_bow",
                    () -> new CustomEnchantableBow(
                            new Item.Properties().durability(2031),
                            22
                    ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
    public static class CustomEnchantableBow extends BowItem {

        private final int enchantability;

        public CustomEnchantableBow(Properties props, int enchantability) {
            super(props);
            this.enchantability = enchantability;
        }

        @Override
        public int getEnchantmentValue() {
            return enchantability;
        }
    }
}
