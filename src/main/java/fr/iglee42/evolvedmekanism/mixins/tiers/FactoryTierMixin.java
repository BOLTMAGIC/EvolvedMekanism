package fr.iglee42.evolvedmekanism.mixins.tiers;

import fr.iglee42.evolvedmekanism.interfaces.InitializableEnum;
import fr.iglee42.evolvedmekanism.tiers.EMBaseTier;
import fr.iglee42.evolvedmekanism.tiers.EMFactoryTier;
import mekanism.api.tier.BaseTier;
import mekanism.common.tier.FactoryTier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Arrays;

// FactoryTier.<init> signature: (String name, int ordinal, BaseTier baseTier, int processes)
@SuppressWarnings("unused")
@Mixin(value = FactoryTier.class, remap = false)
public class FactoryTierMixin implements InitializableEnum {

    @Shadow @Final @Mutable
    private static FactoryTier[] $VALUES;

    @Invoker("<init>")
    public static FactoryTier evolvedmekanism$initInvoker(String internalName, int internalId, BaseTier baseTier, int processes) {
        throw new AssertionError();
    }

    @Unique
    private static FactoryTier evolvedmekanism$addVariant(String name, BaseTier baseTier, int processes) {
        ArrayList<FactoryTier> variants = new ArrayList<>(Arrays.asList($VALUES));
        int ordinal = variants.isEmpty() ? 0 : variants.get(variants.size() - 1).ordinal() + 1;
        FactoryTier tier = evolvedmekanism$initInvoker(name, ordinal, baseTier, processes);
        variants.add(tier);
        FactoryTierMixin.$VALUES = variants.toArray(new FactoryTier[0]);
        return tier;
    }

    @Override
    public void evolvedmekanism$initNewValues() {
        if (EMFactoryTier.OVERCLOCKED != null) return;
        EMFactoryTier.OVERCLOCKED = evolvedmekanism$addVariant("OVERCLOCKED", EMBaseTier.OVERCLOCKED, 11);
        EMFactoryTier.QUANTUM     = evolvedmekanism$addVariant("QUANTUM",     EMBaseTier.QUANTUM,     13);
        EMFactoryTier.DENSE       = evolvedmekanism$addVariant("DENSE",       EMBaseTier.DENSE,       15);
        EMFactoryTier.MULTIVERSAL = evolvedmekanism$addVariant("MULTIVERSAL", EMBaseTier.MULTIVERSAL, 17);
    }

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void evolvedmekanism$init(CallbackInfo ci) {
        if (EMFactoryTier.OVERCLOCKED != null) return;
        if (EMBaseTier.OVERCLOCKED == null) {
            try {
                ((InitializableEnum) (Object) BaseTier.BASIC).evolvedmekanism$initNewValues();
            } catch (Exception e) {
                return;
            }
        }
        EMFactoryTier.OVERCLOCKED = evolvedmekanism$addVariant("OVERCLOCKED", EMBaseTier.OVERCLOCKED, 11);
        EMFactoryTier.QUANTUM     = evolvedmekanism$addVariant("QUANTUM",     EMBaseTier.QUANTUM,     13);
        EMFactoryTier.DENSE       = evolvedmekanism$addVariant("DENSE",       EMBaseTier.DENSE,       15);
        EMFactoryTier.MULTIVERSAL = evolvedmekanism$addVariant("MULTIVERSAL", EMBaseTier.MULTIVERSAL, 17);
    }
}
