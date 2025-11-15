package net.flick.aquaculture_extra_bows.util;

import com.teammetallurgy.aquaculture.init.AquaItems;
import net.flick.aquaculture_extra_bows.item.custom.ModItems;
import net.minecraft.world.item.Item;

public class BowStats {

    public static double getMultiplier(Item bow) {

        // WOOD + GOLD = identical power
        //if (bow == ModItems.WOODEN_BOW.get()) return 0.80;
        if (bow == ModItems.GOLDEN_BOW.get()) return 0.80;

        // COPPER (between stone and iron)
        if (bow == ModItems.COPPER_BOW.get()) return 1.10;

        // STANDARD TIERS
        if (bow == ModItems.IRON_BOW.get()) return 1.20;
        //if (bow == ModItems.DIAMOND_BOW.get()) return 1.40;

        // AQUALCULTURE
        if (bow == AquaItems.NEPTUNIUM_BOW.get()) return 1.50;

        //if (bow == ModItems.NETHERRITE_BOW.get()) return 1.60;

        return 0.8; // default
    }
}
