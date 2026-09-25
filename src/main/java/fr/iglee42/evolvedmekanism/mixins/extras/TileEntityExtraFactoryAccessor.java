package fr.iglee42.evolvedmekanism.mixins.extras;

import com.jerry.mekanism_extras.common.tile.factory.TileEntityExtraFactory;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//TileEntityExtraFactory#energySlot is package-private, so subclasses outside Mekanism Extras can't read it directly
@Mixin(value = TileEntityExtraFactory.class, remap = false)
public interface TileEntityExtraFactoryAccessor {

    @Accessor("energySlot")
    EnergyInventorySlot evolvedmekanism$getEnergySlot();
}
