package net.flick.aquaculture_extra_bows.item.custom;

import net.flick.aquaculture_extra_bows.AquacultureExtraBows;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(AquacultureExtraBows.MOD_ID);

    public static final DeferredItem<Item> GOLDEN_BOW =
            ITEMS.register("golden_bow",
                    () -> new BowItem(new Item.Properties().durability(32)){
                        @Override
                        public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                List<Component> tooltip,
                                TooltipFlag flag
                        ) {
                            double baseDamage = 5.0; // ton damage standard
                            double total = baseDamage;
                            tooltip.add(
                                    Component.literal(total + " ")
                                            .append(Component.translatable("attribute.name.generic.attack_damage"))
                                            .withStyle(ChatFormatting.DARK_GREEN)
                            );
                            super.appendHoverText(stack, context, tooltip, flag);
                        }
                    });

    public static final DeferredItem<Item> COPPER_BOW =
            ITEMS.register("copper_bow",
                    () -> new BowItem(new Item.Properties().durability(190)) {
                        @Override
                        public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                List<Component> tooltip,
                                TooltipFlag flag
                        ) {
                            double baseDamage = 6.0; // ton damage standard
                            double total = baseDamage;
                            tooltip.add(
                                    Component.literal(total + " ")
                                            .append(Component.translatable("attribute.name.generic.attack_damage"))
                                            .withStyle(ChatFormatting.DARK_GREEN)
                            );
                            super.appendHoverText(stack, context, tooltip, flag);
                        }
                    });

    public static final DeferredItem<Item> IRON_BOW =
            ITEMS.register("iron_bow",
                    () -> new BowItem(new Item.Properties().durability(250)){
                        @Override
                        public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                List<Component> tooltip,
                                TooltipFlag flag
                        ) {
                            double baseDamage = 7.0; // ton damage standard
                            double total = baseDamage;
                            tooltip.add(
                                    Component.literal(total + " ")
                                            .append(Component.translatable("attribute.name.generic.attack_damage"))
                                            .withStyle(ChatFormatting.DARK_GREEN)
                            );
                            super.appendHoverText(stack, context, tooltip, flag);
                        }
                    });

    public static final DeferredItem<Item> DIAMOND_BOW =
            ITEMS.register("diamond_bow",
                    () -> new BowItem(new Item.Properties().durability(1561)){
                        @Override
                        public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                List<Component> tooltip,
                                TooltipFlag flag
                        ) {
                            double baseDamage = 8.0; // ton damage standard
                            double total = baseDamage;
                            tooltip.add(
                                    Component.literal(total + " ")
                                            .append(Component.translatable("attribute.name.generic.attack_damage"))
                                            .withStyle(ChatFormatting.DARK_GREEN)
                            );
                            super.appendHoverText(stack, context, tooltip, flag);
                        }
                    });

    public static final DeferredItem<Item> NETHERITE_BOW =
            ITEMS.register("netherite_bow",
                    () -> new BowItem(new Item.Properties().durability(2031)){
                        @Override
                        public void appendHoverText(
                                ItemStack stack,
                                TooltipContext context,
                                List<Component> tooltip,
                                TooltipFlag flag
                        ) {
                            double baseDamage = 9.0; // ton damage standard
                            double total = baseDamage;
                            tooltip.add(
                                    Component.literal(total + " ")
                                            .append(Component.translatable("attribute.name.generic.attack_damage"))
                                            .withStyle(ChatFormatting.DARK_GREEN)
                            );
                            super.appendHoverText(stack, context, tooltip, flag);
                        }

                    });

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
