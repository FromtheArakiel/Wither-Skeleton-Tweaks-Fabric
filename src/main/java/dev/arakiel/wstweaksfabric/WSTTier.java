package dev.arakiel.wstweaksfabric;

import java.util.function.Supplier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/**
 * The tool tier of the immolation blade.
 *
 * <p>Replaces the NeoForge {@code SimpleTier} helper the original used. The repair ingredient is
 * supplied lazily because the tier is built before the item it repairs exists.
 */
public record WSTTier(TagKey<Block> incorrectBlocks, int uses, float speed, float attackDamageBonus,
                      int enchantmentValue, Supplier<Ingredient> repairIngredient) implements Tier {

    @Override
    public int getUses() {
        return this.uses;
    }

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return this.attackDamageBonus;
    }

    @Override
    public TagKey<Block> getIncorrectBlocksForDrops() {
        return this.incorrectBlocks;
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }
}
