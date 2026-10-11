package dev.arakiel.wstweaksfabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Items of the mod.
 *
 * <p>This replaces the Placebo {@code DeferredHelper} the NeoForge original used; the values are
 * registered eagerly from {@link #bootstrap()} while the game is still starting up.
 */
public final class WSTObjects {

    public static Tier IMMOLATION;

    public static Item FRAGMENT;

    public static Item IMMOLATION_BLADE;

    private WSTObjects() {
    }

    public static void bootstrap() {
        IMMOLATION = new WSTTier(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, WSTConfig.swordDurability, 0.0F,
                WSTConfig.swordDamage, 30, () -> Ingredient.of(Items.NETHER_STAR));

        FRAGMENT = Registry.register(BuiltInRegistries.ITEM, WitherSkeletonTweaksFabric.id("fragment"),
                new Item(new Item.Properties()));

        IMMOLATION_BLADE = Registry.register(BuiltInRegistries.ITEM,
                WitherSkeletonTweaksFabric.id("immolation_blade"),
                new ImmolationBladeItem(IMMOLATION, new Item.Properties().attributes(
                        SwordItem.createAttributes(IMMOLATION, 0, WSTConfig.swordAtkSpeed))));
    }
}
