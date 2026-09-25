package fr.iglee42.evolvedmekanism.mixins.extras;

import com.jerry.mekanism_extras.client.gui.machine.GuiExtraFactory;
import com.jerry.mekanism_extras.common.tile.factory.TileEntityExtraFactory;
import fr.iglee42.evolvedmekanism.jei.EMJEI;
import fr.iglee42.evolvedmekanism.registries.EMFactoryType;
import mekanism.client.gui.GuiConfigurableTile;
import mekanism.client.gui.element.progress.GuiProgress;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//GuiExtraFactory#addProgress switches over FactoryType for the JEI category, which can't handle ALLOYING
@Mixin(value = GuiExtraFactory.class, remap = false)
public abstract class GuiExtraFactoryMixin extends GuiConfigurableTile<TileEntityExtraFactory<?>, MekanismTileContainer<TileEntityExtraFactory<?>>> {

    private GuiExtraFactoryMixin(MekanismTileContainer<TileEntityExtraFactory<?>> container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Inject(method = "addProgress", at = @At("HEAD"), cancellable = true)
    private void evolvedmekanism$alloyingProgressBar(GuiProgress progressBar, CallbackInfoReturnable<GuiProgress> cir) {
        if (tile.getFactoryType() == EMFactoryType.ALLOYING) {
            cir.setReturnValue(addRenderableWidget(progressBar.jeiCategories(EMJEI.ALLOYING)));
        }
    }
}
