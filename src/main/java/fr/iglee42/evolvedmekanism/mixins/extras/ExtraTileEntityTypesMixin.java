package fr.iglee42.evolvedmekanism.mixins.extras;

import com.google.common.collect.Table;
import com.jerry.mekanism_extras.common.registries.ExtraBlocks;
import com.jerry.mekanism_extras.common.registries.ExtraTileEntityTypes;
import com.jerry.mekanism_extras.common.tier.ExtraFactoryTier;
import com.jerry.mekanism_extras.common.tile.factory.TileEntityExtraFactory;
import com.jerry.mekanism_extras.common.util.ExtraEnumUtils;
import fr.iglee42.evolvedmekanism.compat.extras.TileEntityExtraAlloyingFactory;
import fr.iglee42.evolvedmekanism.registries.EMFactoryType;
import mekanism.common.content.blocktype.FactoryType;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.tile.base.TileEntityMekanism;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ExtraTileEntityTypes.class, remap = false)
public class ExtraTileEntityTypesMixin {

    @Shadow @Final private static Table<ExtraFactoryTier, FactoryType, TileEntityTypeRegistryObject<? extends TileEntityExtraFactory<?>>> FACTORIES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void evolvedmekanism$registerAlloyingFactories(CallbackInfo ci) {
        for (ExtraFactoryTier tier : ExtraEnumUtils.EXTRA_FACTORY_TIERS) {
            FACTORIES.put(tier, EMFactoryType.ALLOYING, ExtraTileEntityTypes.TILE_ENTITY_TYPES.register(ExtraBlocks.getAdvancedFactory(tier, EMFactoryType.ALLOYING),
                    (pos, state) -> new TileEntityExtraAlloyingFactory(ExtraBlocks.getAdvancedFactory(tier, EMFactoryType.ALLOYING), pos, state),
                    TileEntityMekanism::tickServer, TileEntityMekanism::tickClient));
        }
    }
}
