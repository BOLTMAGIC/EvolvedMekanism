package fr.iglee42.evolvedmekanism.mixins.extras;

import com.google.common.collect.Table;
import com.jerry.mekanism_extras.common.block.prefab.BlockExtraFactoryMachine.BlockExtraFactory;
import com.jerry.mekanism_extras.common.content.blocktype.ExtraFactory;
import com.jerry.mekanism_extras.common.item.block.machine.ItemBlockExtraFactory;
import com.jerry.mekanism_extras.common.registries.ExtraBlockTypes;
import com.jerry.mekanism_extras.common.registries.ExtraBlocks;
import com.jerry.mekanism_extras.common.tier.ExtraFactoryTier;
import com.jerry.mekanism_extras.common.tile.factory.TileEntityExtraFactory;
import com.jerry.mekanism_extras.common.util.ExtraEnumUtils;
import fr.iglee42.evolvedmekanism.registries.EMFactoryType;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.registration.impl.BlockRegistryObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Registers mekanism_extras:<tier>_alloying_factory blocks, exactly like Extras registers its other factories
@Mixin(value = ExtraBlocks.class, remap = false)
public class ExtraBlocksMixin {

    @Shadow @Final private static Table<ExtraFactoryTier, FactoryType, BlockRegistryObject<BlockExtraFactory<?>, ItemBlockExtraFactory>> FACTORIES;

    @Shadow
    private static <TILE extends TileEntityExtraFactory<?>> BlockRegistryObject<BlockExtraFactory<?>, ItemBlockExtraFactory> registerFactory(ExtraFactory<TILE> type) {
        throw new AssertionError();
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void evolvedmekanism$registerAlloyingFactories(CallbackInfo ci) {
        for (ExtraFactoryTier tier : ExtraEnumUtils.EXTRA_FACTORY_TIERS) {
            FACTORIES.put(tier, EMFactoryType.ALLOYING, registerFactory(ExtraBlockTypes.getAdvancedFactory(tier, EMFactoryType.ALLOYING)));
        }
    }
}
