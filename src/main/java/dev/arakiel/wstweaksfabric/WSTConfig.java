package dev.arakiel.wstweaksfabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;

/**
 * The config of the mod, stored as JSON through Gson.
 *
 * <p>This replaces the Placebo {@code Configuration} the NeoForge original used. The file lives in
 * {@code config/wstweaksfabric.json}, is written on first launch with the defaults below and is read
 * again whenever data packs reload, which is what the original did with its reload listener.
 */
public final class WSTConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final String FILE_NAME = WitherSkeletonTweaksFabric.MODID + ".json";

    /** The chance for a skull shard to drop. 1 = 100%, 0.5 = 50%, etc. */
    public static float shardDropChance = 1.0F;

    /** If skeletons in ALL biomes are converted, instead of just the nether. */
    public static boolean allBiomes = false;

    /** The chance for skeletons to be converted in all biomes, when enabled. */
    public static float allBiomesChance = 0.15F;

    /** If stone swords and other trash are removed from wither skeleton drop tables. */
    public static boolean delSwords = true;

    /** If converted skeletons receive bows (Wither Skeletons always shoot flaming arrows). */
    public static boolean giveBows = true;

    /** Requires a restart: the durability of immolation blades. */
    public static int swordDurability = 4096;

    /** Requires a restart: the attack damage of immolation blades. */
    public static float swordDamage = 11.0F;

    /** Requires a restart: the attack speed of immolation blades. */
    public static float swordAtkSpeed = -2.0F;

    private WSTConfig() {
    }

    /** Reads the config, creating the file with the defaults when it does not exist yet. */
    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        Values values = new Values();

        if (Files.exists(path)) {
            try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                Values stored = GSON.fromJson(reader, Values.class);
                if (stored != null) {
                    values = stored;
                }
            } catch (IOException | JsonParseException ex) {
                WitherSkeletonTweaksFabric.LOGGER.warn("Could not read {}, falling back to the defaults", path, ex);
            }
        }

        shardDropChance = clamp(values.shardDropChance, 0.0F, 1.0F, 1.0F);
        allBiomes = values.allBiomes;
        allBiomesChance = clamp(values.allBiomesChance, 0.0F, 1.0F, 0.15F);
        delSwords = values.delSwords;
        giveBows = values.giveBows;
        swordDurability = Math.max(1, values.swordDurability);
        swordDamage = Math.max(1.0F, values.swordDamage);
        swordAtkSpeed = values.swordAtkSpeed;

        if (!Files.exists(path)) {
            save(path);
        }
    }

    private static void save(Path path) {
        Values values = new Values();
        values.shardDropChance = shardDropChance;
        values.allBiomes = allBiomes;
        values.allBiomesChance = allBiomesChance;
        values.delSwords = delSwords;
        values.giveBows = giveBows;
        values.swordDurability = swordDurability;
        values.swordDamage = swordDamage;
        values.swordAtkSpeed = swordAtkSpeed;

        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
                GSON.toJson(values, writer);
            }
        } catch (IOException ex) {
            WitherSkeletonTweaksFabric.LOGGER.warn("Could not write {}", path, ex);
        }
    }

    private static float clamp(float value, float min, float max, float fallback) {
        if (!Float.isFinite(value)) {
            return fallback;
        }
        return Math.min(max, Math.max(min, value));
    }

    /** Plain holder for the serialised values; the field names are the JSON keys. */
    private static final class Values {
        private float shardDropChance = 1.0F;
        private boolean allBiomes = false;
        private float allBiomesChance = 0.15F;
        private boolean delSwords = true;
        private boolean giveBows = true;
        private int swordDurability = 4096;
        private float swordDamage = 11.0F;
        private float swordAtkSpeed = -2.0F;
    }
}
