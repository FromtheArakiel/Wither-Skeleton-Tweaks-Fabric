package dev.arakiel.wstweaksfabric;

import java.util.Set;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableSource;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemKilledByPlayerCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceWithEnchantedBonusCondition;

/**
 * Turns the original global loot modifier into plain loot table additions.
 *
 * <p>A skeleton killed by fireworks or by an immolation blade always yields a skull, and wither
 * skeletons hand out a skull fragment when no skull dropped. The fragment pool repeats the vanilla
 * skull roll so it can tell whether a skull is coming.
 */
public final class WSTLootTables {

    /** The vanilla wither skeleton skull roll: 2.5%, plus 1% per looting level above the first. */
    private static final float SKULL_CHANCE = 0.025F;

    private static final float SKULL_LOOTING_BONUS = 0.01F;

    private static final Set<ResourceKey<LootTable>> PLAIN_SKELETONS = Set.of(
            EntityType.SKELETON.getDefaultLootTable(),
            EntityType.STRAY.getDefaultLootTable(),
            EntityType.BOGGED.getDefaultLootTable());

    private WSTLootTables() {
    }

    public static void register() {
        LootTableEvents.MODIFY.register(WSTLootTables::modify);
    }

    private static void modify(ResourceKey<LootTable> key, LootTable.Builder table, LootTableSource source,
                               HolderLookup.Provider registries) {
        if (key.equals(EntityType.WITHER_SKELETON.getDefaultLootTable())) {
            modifyWitherSkeleton(table, registries);
        } else if (PLAIN_SKELETONS.contains(key)) {
            table.withPool(LootPool.lootPool()
                    .add(LootItem.lootTableItem(Items.SKELETON_SKULL))
                    .when(WSTKillCondition.builder()));
        }
    }

    private static void modifyWitherSkeleton(LootTable.Builder table, HolderLookup.Provider registries) {
        LootItemCondition.Builder blade = WSTKillCondition.builder();
        LootItemCondition.Builder vanillaSkull = LootItemKilledByPlayerCondition.killedByPlayer()
                .and(LootItemRandomChanceWithEnchantedBonusCondition
                        .randomChanceAndLootingBoost(registries, SKULL_CHANCE, SKULL_LOOTING_BONUS));

        // The blade guarantees a skull, but never a second one on top of the roll the vanilla pool
        // already made for the same death.
        table.withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(Items.WITHER_SKELETON_SKULL))
                .when(blade.and(vanillaSkull.invert())));

        // A fragment is handed out when the skull did not drop, which is the guarantee the original
        // modifier provided.
        table.withPool(LootPool.lootPool()
                .add(LootItem.lootTableItem(WSTObjects.FRAGMENT))
                .when(LootItemRandomChanceCondition.randomChance(WSTConfig.shardDropChance)
                        .and(blade.or(vanillaSkull).invert())));
    }
}
