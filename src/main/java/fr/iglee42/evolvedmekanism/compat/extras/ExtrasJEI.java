package fr.iglee42.evolvedmekanism.compat.extras;

import com.jerry.mekanism_extras.common.registries.ExtraBlocks;
import com.jerry.mekanism_extras.common.tier.ExtraFactoryTier;
import com.jerry.mekanism_extras.common.util.ExtraEnumUtils;
import fr.iglee42.evolvedmekanism.jei.EMJEI;
import fr.iglee42.evolvedmekanism.registries.EMFactoryType;
import mekanism.api.providers.IItemProvider;
import mekanism.client.jei.CatalystRegistryHelper;
import mezz.jei.api.registration.IRecipeCatalystRegistration;

//Kept separate from EMJEI so Mekanism Extras classes are only loaded when it is installed
public class ExtrasJEI {

    public static void registerCatalysts(IRecipeCatalystRegistration registry) {
        IItemProvider[] factories = new IItemProvider[ExtraEnumUtils.EXTRA_FACTORY_TIERS.length];
        for (ExtraFactoryTier tier : ExtraEnumUtils.EXTRA_FACTORY_TIERS) {
            factories[tier.ordinal()] = ExtraBlocks.getAdvancedFactory(tier, EMFactoryType.ALLOYING);
        }
        CatalystRegistryHelper.register(registry, EMJEI.ALLOYING, factories);
    }
}
