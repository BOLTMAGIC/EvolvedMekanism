package fr.iglee42.evolvedmekanism.mixins.extras;

import com.google.common.collect.Table;
import com.jerry.mekanism_extras.common.content.blocktype.ExtraFactory;
import com.jerry.mekanism_extras.common.registries.ExtraBlockTypes;
import com.jerry.mekanism_extras.common.tier.ExtraFactoryTier;
import com.jerry.mekanism_extras.common.util.ExtraEnumUtils;
import fr.iglee42.evolvedmekanism.compat.extras.ExtrasAlloyingFactories;
import fr.iglee42.evolvedmekanism.registries.EMFactoryType;
import mekanism.common.content.blocktype.FactoryType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//Extras skips ALLOYING when Evolved Mekanism is loaded, so add the alloying block types it left out
@Mixin(value = ExtraBlockTypes.class, remap = false)
public class ExtraBlockTypesMixin {

    @Shadow @Final private static Table<ExtraFactoryTier, FactoryType, ExtraFactory<?>> FACTORIES;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void evolvedmekanism$addAlloyingFactories(CallbackInfo ci) {
        for (ExtraFactoryTier tier : ExtraEnumUtils.EXTRA_FACTORY_TIERS) {
            FACTORIES.put(tier, EMFactoryType.ALLOYING, ExtrasAlloyingFactories.createFactory(tier));
        }
    }
}
