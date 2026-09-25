package fr.iglee42.evolvedmekanism.mixins.accessors;

import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.tile.factory.TileEntityFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//TileEntityFactory#energySlot is package-private, so subclasses outside Mekanism can't read it directly
@Mixin(value = TileEntityFactory.class, remap = false)
public interface TileEntityFactoryAccessor {

    @Accessor("energySlot")
    EnergyInventorySlot evolvedmekanism$getEnergySlot();
}
