package dev.arakiel.wstweaksfabric;

import com.mojang.serialization.MapCodec;
import java.util.Set;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;

/**
 * "Killed by fireworks or by an immolation blade".
 *
 * <p>This is the condition the original global loot modifier checked by hand; here it is a real loot
 * condition, so the drop rules live in the loot table like every other drop does.
 */
public final class WSTKillCondition implements LootItemCondition, LootItemCondition.Builder {

    public static final MapCodec<WSTKillCondition> CODEC = MapCodec.unit(WSTKillCondition::new);

    private static final WSTKillCondition INSTANCE = new WSTKillCondition();

    private static LootItemConditionType type;

    private WSTKillCondition() {
    }

    /** Has to run while the registries are still open, which is during mod initialisation. */
    public static void register() {
        type = Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE,
                WitherSkeletonTweaksFabric.id("killed_by_blade"), new LootItemConditionType(CODEC));
    }

    public static LootItemCondition.Builder builder() {
        return INSTANCE;
    }

    @Override
    public boolean test(LootContext context) {
        DamageSource source = context.getParamOrNull(LootContextParams.DAMAGE_SOURCE);
        if (source == null) {
            return false;
        }
        if (source.is(DamageTypes.FIREWORKS)) {
            return true;
        }
        Entity attacker = source.getEntity();
        return attacker instanceof LivingEntity living && living.getWeaponItem().is(WSTObjects.IMMOLATION_BLADE);
    }

    @Override
    public Set<LootContextParam<?>> getReferencedContextParams() {
        return Set.of(LootContextParams.DAMAGE_SOURCE);
    }

    @Override
    public LootItemConditionType getType() {
        return type;
    }

    @Override
    public LootItemCondition build() {
        return this;
    }
}
