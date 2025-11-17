package net.flick.aquaculture_extra_bows.util;

import com.teammetallurgy.aquaculture.init.AquaItems;
import net.flick.aquaculture_extra_bows.item.custom.ModItems;
import net.minecraft.world.item.Item;

public class BowStats {

    public static double getMultiplier(Item bow) {
        String id = bow.toString().toLowerCase();

        if (id.contains("golden_bow")) return 0.78;
        if (id.contains("copper_bow")) return 1.11;
        if (id.contains("iron_bow")) return 1.22;
        if (id.contains("diamond_bow")) return 1.44;
        if (id.contains("neptunium_bow")) return 1.56;
        if (id.contains("netherite_bow")) return 1.67;

        return 0.80;
    }
}
