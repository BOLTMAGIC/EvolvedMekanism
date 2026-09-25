package fr.iglee42.evolvedmekanism.compat.extras;

import com.jerry.mekanism_extras.common.content.blocktype.ExtraFactory;
import com.jerry.mekanism_extras.common.content.blocktype.ExtraMachine.ExtraFactoryMachine;
import com.jerry.mekanism_extras.common.content.blocktype.ExtraMachine.ExtraMachineBuilder;
import com.jerry.mekanism_extras.common.registries.ExtraContainerTypes;
import com.jerry.mekanism_extras.common.registries.ExtraTileEntityTypes;
import com.jerry.mekanism_extras.common.tier.ExtraFactoryTier;
import fr.iglee42.evolvedmekanism.EvolvedMekanismLang;
import fr.iglee42.evolvedmekanism.registries.EMFactoryType;
import fr.iglee42.evolvedmekanism.registries.EMTileEntityTypes;
import fr.iglee42.evolvedmekanism.tiles.machine.TileEntityAlloyer;
import mekanism.common.block.attribute.AttributeParticleFX;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.blocktype.BlockShapes;
import mekanism.common.lib.math.Pos3D;
import mekanism.common.registration.impl.TileEntityTypeRegistryObject;
import mekanism.common.registries.MekanismSounds;
import net.minecraft.core.particles.ParticleTypes;

/**
 * Builds the Absolute -> Infinite alloying factories the same way Mekanism Extras builds its own factories.
 * Extras skips ALLOYING on purpose: its factory code switches over FactoryType, which can't handle a value added at runtime.
 * Only touched from the Extras mixins, so none of these classes load when Mekanism Extras is missing.
 */
public class ExtrasAlloyingFactories {

    //Template machine the factories copy sound, energy and upgrade support from (Extras' counterpart of EMBlockTypes.ALLOYER)
    public static final ExtraFactoryMachine<TileEntityAlloyer> ALLOYER = ExtraMachineBuilder
            .createExtraFactoryMachine(() -> EMTileEntityTypes.ALLOYER, EvolvedMekanismLang.DESCRIPTION_ALLOYER, EMFactoryType.ALLOYING)
            .withSound(MekanismSounds.COMBINER)
            .withEnergyConfig(MekanismConfig.usage.combiner, MekanismConfig.storage.combiner)
            .build();

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ExtraFactory<TileEntityExtraAlloyingFactory> createFactory(ExtraFactoryTier tier) {
        ExtraFactory<TileEntityExtraAlloyingFactory> factory = new ExtraFactory<>(
                () -> (TileEntityTypeRegistryObject<TileEntityExtraAlloyingFactory>) ExtraTileEntityTypes.getExtraFactoryTile(tier, EMFactoryType.ALLOYING),
                () -> ExtraContainerTypes.FACTORY, ALLOYER, tier);
        return (ExtraFactory<TileEntityExtraAlloyingFactory>) new Builder(factory)
                .withCustomShape(BlockShapes.getShape(null, EMFactoryType.ALLOYING))
                .replace(new AttributeParticleFX().addDense(ParticleTypes.SMOKE, 5, rand -> new Pos3D(
                        rand.nextFloat() * 0.7F - 0.3F,
                        rand.nextFloat() * 0.1F + 0.7F,
                        rand.nextFloat() * 0.7F - 0.3F)))
                .withComputerSupport(tier.getAdvanceTier().getLowerName() + EMFactoryType.ALLOYING.getRegistryNameComponentCapitalized() + "Factory")
                .build();
    }

    //Only exists to reach the protected constructor. Raw because the builder's self type must be an ExtraMachineBuilder,
    // which no subclass of AdvancedFactoryBuilder can satisfy (Extras itself only ever uses it through a wildcard)
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static class Builder extends ExtraFactory.AdvancedFactoryBuilder {

        private Builder(ExtraFactory<TileEntityExtraAlloyingFactory> holder) {
            super(holder);
        }
    }
}
