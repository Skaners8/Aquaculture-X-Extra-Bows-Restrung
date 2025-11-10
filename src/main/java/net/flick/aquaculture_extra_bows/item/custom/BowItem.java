package net.flick.aquaculture_extra_bows.item.custom;

import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class IronBowItem extends BowItem {

    public IronBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity shooter, int timeLeft) {
        if (!level.isClientSide) {
            int charge = this.getUseDuration(stack) - timeLeft;
            float power = getPowerForTime(charge);

            // Crée la flèche
            AbstractArrow arrow = new Arrow(level, shooter);
            arrow.setBaseDamage(arrow.getBaseDamage() * 1.5); // Augmente de 50%

            arrow.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, power * 3.0F, 1.0F);
            level.addFreshEntity(arrow);
        }
        stack.hurtAndBreak(1, shooter, e -> e.broadcastBreakEvent(shooter.getUsedItemHand()));
    }

    private static float getPowerForTime(int charge) {
        float f = (float) charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) f = 1.0F;
        return f;
    }
}
