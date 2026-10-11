package dev.arakiel.wstweaksfabric;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * World behaviour of the mod: skeletons in the nether turn into wither skeletons and skeletons stop
 * dropping their weapons.
 *
 * <p>The NeoForge original used {@code FinalizeSpawnEvent} plus {@code EntityJoinLevelEvent} and a
 * {@code LivingDropsEvent} filter. Fabric has no equivalent of the latter two, so the conversion
 * runs on {@code ENTITY_LOAD} - deferred by one server task so the entity list is not modified while
 * the entity is still being added - and the trash drops are removed at their source, by zeroing the
 * main hand drop chance of a skeleton.
 */
public final class WSTEvents {

    /** Skeletons only convert in the dark when the "all biomes" option is on. */
    private static final int ALL_BIOMES_MAX_LIGHT = 9;

    private WSTEvents() {
    }

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register(WSTEvents::onEntityLoad);
        // The config is re-read before data packs load, so loot tables see fresh values, and again
        // when a server boots.
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, manager) -> WSTConfig.load());
        ServerLifecycleEvents.SERVER_STARTED.register(server -> WSTConfig.load());
    }

    private static void onEntityLoad(Entity entity, ServerLevel world) {
        if (!(entity instanceof AbstractSkeleton skeleton)) {
            return;
        }

        if (WSTConfig.delSwords) {
            // Stone swords and bows are equipment rather than loot table entries, so the drop is
            // removed where it is decided instead of filtering a list of spawned items.
            skeleton.setDropChance(EquipmentSlot.MAINHAND, 0.0F);
        }

        if (entity instanceof Skeleton plain && shouldConvert(world, plain)) {
            world.getServer().execute(() -> convert(plain));
        }
    }

    private static boolean shouldConvert(ServerLevel world, Skeleton skeleton) {
        if (world.dimension() == Level.NETHER) {
            return true;
        }
        return WSTConfig.allBiomes
                && world.getRawBrightness(skeleton.blockPosition(), 0) < ALL_BIOMES_MAX_LIGHT
                && world.getRandom().nextFloat() < WSTConfig.allBiomesChance;
    }

    private static void convert(Skeleton skeleton) {
        if (skeleton.isRemoved() || !skeleton.isAlive()) {
            return;
        }

        WitherSkeleton wither = skeleton.convertTo(EntityType.WITHER_SKELETON, true);
        if (wither == null) {
            return;
        }

        // Wither skeletons always shoot flaming arrows, so a converted skeleton is given a bow when
        // the config allows it and falls back to the regular stone sword otherwise.
        wither.setItemSlot(EquipmentSlot.MAINHAND,
                new ItemStack(WSTConfig.giveBows ? Items.BOW : Items.STONE_SWORD));
    }
}
