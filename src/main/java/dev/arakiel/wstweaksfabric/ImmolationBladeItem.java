package dev.arakiel.wstweaksfabric;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;

/**
 * The immolation blade: sets its victim on fire and finishes any skeleton outright with firework
 * damage, which is what drives the skull drops.
 */
public class ImmolationBladeItem extends SwordItem {

    private static final float BURN_SECONDS = 150.0F;

    private static final float SKELETON_DAMAGE = 150.0F;

    public ImmolationBladeItem(Tier tier, Item.Properties properties) {
        super(tier, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        target.igniteForSeconds(BURN_SECONDS);
        super.hurtEnemy(stack, target, attacker);
        if (target instanceof AbstractSkeleton) {
            target.setHealth(1.0F);
            DamageSource firework = new DamageSource(target.level().registryAccess()
                    .registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.FIREWORKS));
            target.hurt(firework, SKELETON_DAMAGE);
            double i = target.getCommandSenderWorld().random.nextDouble() * 4.0D;
            double d = target.getCommandSenderWorld().random.nextDouble() * 4.0D;
            double k = target.getCommandSenderWorld().random.nextDouble() * 4.0D;
            target.push(2.0D - i, d, 2.0D - k);
        }
        return true;
    }
}
