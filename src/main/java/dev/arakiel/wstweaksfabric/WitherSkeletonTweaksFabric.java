package dev.arakiel.wstweaksfabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Fabric port of Wither Skeleton Tweaks.
 *
 * <p>The NeoForge original leaned on the Placebo library for three things: its config file, its
 * deferred registrar and a reload listener wrapper. All three are hand rolled here (see
 * {@link WSTConfig}, {@link WSTObjects} and {@link WSTEvents}) so the port needs nothing beyond
 * Fabric API.
 */
public class WitherSkeletonTweaksFabric implements ModInitializer {

    public static final String MODID = "wstweaksfabric";

    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        WSTConfig.load();
        WSTObjects.bootstrap();
        WSTKillCondition.register();
        WSTEvents.register();
        WSTLootTables.register();

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COMBAT)
                .register(entries -> entries.accept(WSTObjects.IMMOLATION_BLADE));
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.INGREDIENTS)
                .register(entries -> entries.accept(WSTObjects.FRAGMENT));
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
