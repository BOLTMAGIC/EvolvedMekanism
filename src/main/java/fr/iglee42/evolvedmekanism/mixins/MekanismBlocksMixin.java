package fr.iglee42.evolvedmekanism.mixins;

import com.google.common.collect.Table;
import fr.iglee42.evolvedmekanism.EvolvedMekanism;
import fr.iglee42.evolvedmekanism.registries.EMFactoryType;
import mekanism.common.content.blocktype.Factory;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.common.registries.MekanismBlocks;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.factory.TileEntityFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Prevents Mekanism from adding non-ALLOYING factory blocks (smelting, enriching, etc.)
 * for EM-specific factory tiers (OVERCLOCKED, QUANTUM, DENSE, MULTIVERSAL) to the
 * FACTORIES lookup table. The blocks are still registered in the registry (unavoidable),
 * but they won't appear in MekanismBlocks.getFactory() or the creative tab.
 */
@SuppressWarnings({"unused", "rawtypes"})
@Mixin(value = MekanismBlocks.class, remap = false)
public class MekanismBlocksMixin {

    @Shadow
    private static <TILE extends TileEntityFactory<?>> BlockRegistryObject<?, ?> registerFactory(Factory<TILE> type) {
        throw new AssertionError();
    }

    @Redirect(method = "<clinit>",
              at = @At(value = "INVOKE",
                       target = "Lcom/google/common/collect/Table;put(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    private static Object evolvedmekanism$skipNonAlloyingEMTiers(Table table, Object tier, Object type, Object block) {
        if (EvolvedMekanism.isEvolvedMekanismTier(((FactoryTier) tier).getBaseTier())
                && !type.equals(EMFactoryType.ALLOYING)) {
            return null; // don't add to FACTORIES table — block exists but is hidden
        }
        //noinspection unchecked
        return table.put(tier, type, block);
    }
}
