package net.flick.aquaculture_extra_bows.event;

import com.teammetallurgy.aquaculture.init.AquaItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static net.flick.aquaculture_extra_bows.AquacultureExtraBows.MOD_ID;

/**
 * Handles custom arrow damage for different bows.
 * All custom bows behave like vanilla arrows (randomized damage).
 */
@EventBusSubscriber(modid = MOD_ID)
public class BowDamageEventHandler {

    // ----------------------------
    // Lazy-initialized map of bow multipliers
    // ----------------------------
    private static final Map<Item, Double> BOW_MULTIPLIERS = new ConcurrentHashMap<>();
    private static boolean initialized = false;

    private static void initMultipliers() {
        if (initialized) return;
        initialized = true;

        // Arcs custom
        BOW_MULTIPLIERS.put(net.flick.aquaculture_extra_bows.item.custom.ModItems.GOLDEN_BOW.get(), 1.0);
        BOW_MULTIPLIERS.put(net.flick.aquaculture_extra_bows.item.custom.ModItems.COPPER_BOW.get(), 1.0);
        BOW_MULTIPLIERS.put(net.flick.aquaculture_extra_bows.item.custom.ModItems.IRON_BOW.get(), 1.0);
    }

    // ----------------------------
    // Pending shots
    // ----------------------------
    private static final class PendingShot {
        final long tick;
        final double multiplier;
        final int powerLevel;

        PendingShot(long tick, double multiplier, int powerLevel) {
            this.tick = tick;
            this.multiplier = multiplier;
            this.powerLevel = powerLevel;
        }
    }

    private static final Map<UUID, PendingShot> PENDING_SHOTS = new ConcurrentHashMap<>();
    private static final long VALID_TICKS = 3L;

    // ----------------------------
    // Event: arrow released
    // ----------------------------
    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event) {
        initMultipliers();

        ItemStack bow = event.getBow();
        if (bow == null) return;

        double multiplier = BOW_MULTIPLIERS.getOrDefault(bow.getItem(), 0.0);
        if (multiplier <= 0) return;

        LivingEntity shooter = event.getEntity();
        if (shooter == null) return;

        Level level = shooter.level();
        if (level.isClientSide()) return;

        int powerLevel = EnchantmentHelper.getItemEnchantmentLevel(
                level.holderOrThrow(Enchantments.POWER),
                bow
        );

        PENDING_SHOTS.put(shooter.getUUID(), new PendingShot(level.getGameTime(), multiplier, powerLevel));
    }

    // ----------------------------
    // Event: entity joins level (detect arrow)
    // ----------------------------
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof AbstractArrow arrow)) return;
        if (!(arrow.getOwner() instanceof LivingEntity owner)) return;

        UUID ownerUUID = owner.getUUID();
        PendingShot ps = PENDING_SHOTS.get(ownerUUID);
        if (ps == null) return;

        Level level = (Level) event.getLevel();
        if (level.isClientSide()) {
            PENDING_SHOTS.remove(ownerUUID);
            return;
        }

        long currentTick = level.getGameTime();
        if (currentTick - ps.tick > VALID_TICKS) {
            PENDING_SHOTS.remove(ownerUUID);
            return;
        }

        // ----------------------------
        // Apply multiplier and Power
        // We don't remove random, vanilla behavior remains
        // ----------------------------
        double damage = arrow.getBaseDamage() * ps.multiplier;

        if (ps.powerLevel > 0) {
            double powerMultiplier = 1.0 + 0.25 * (ps.powerLevel + 1);
            damage *= powerMultiplier;
        }

        arrow.setBaseDamage(damage);

        // Remove pending shot
        PENDING_SHOTS.remove(ownerUUID);
    }
}
