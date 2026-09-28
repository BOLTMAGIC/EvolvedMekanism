package fr.iglee42.evolvedmekanism.mixins;

import mekanism.common.block.attribute.AttributeUpgradeable;
import mekanism.common.registration.impl.BlockRegistryObject;
import mekanism.api.tier.BaseTier;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

/**
 * Handles null upgrade block gracefully. When an EM factory tier's upgrade target
 * does not exist (e.g. non-ALLOYING factories that have no EM-tier variant),
 * upgradeResult() would NPE. This mixin returns the current state instead.
 */
@SuppressWarnings("unused")
@Mixin(value = AttributeUpgradeable.class, remap = false)
public class AttributeUpgradeableMixin {

    @Shadow
    private Supplier<BlockRegistryObject<?, ?>> upgradeBlock;

    @Inject(method = "upgradeResult", at = @At("HEAD"), cancellable = true)
    private void evolvedmekanism$safeUpgradeResult(BlockState current, BaseTier tier,
            CallbackInfoReturnable<BlockState> cir) {
        if (upgradeBlock.get() == null) {
            cir.setReturnValue(current); // no upgrade target exists, stay as-is
        }
    }
}
