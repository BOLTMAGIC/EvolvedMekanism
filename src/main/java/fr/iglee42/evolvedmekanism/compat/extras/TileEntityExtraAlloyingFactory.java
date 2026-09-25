package fr.iglee42.evolvedmekanism.compat.extras;

import com.jerry.mekanism_extras.common.registries.ExtraTileEntityTypes;
import com.jerry.mekanism_extras.common.tier.ExtraFactoryTier;
import com.jerry.mekanism_extras.common.tile.factory.TileEntityExtraItemToItemFactory;
import com.jerry.mekanism_extras.common.util.ExtraEnumUtils;
import fr.iglee42.evolvedmekanism.interfaces.EMInputRecipeCache;
import fr.iglee42.evolvedmekanism.interfaces.ThreeInputCachedRecipe;
import fr.iglee42.evolvedmekanism.interfaces.TripleItemRecipeLookupHandler;
import fr.iglee42.evolvedmekanism.mixins.extras.TileEntityExtraFactoryAccessor;
import fr.iglee42.evolvedmekanism.recipes.AlloyerRecipe;
import fr.iglee42.evolvedmekanism.registries.EMFactoryType;
import fr.iglee42.evolvedmekanism.registries.EMRecipeType;
import fr.iglee42.evolvedmekanism.registries.EMTileEntityTypes;
import fr.iglee42.evolvedmekanism.tiles.LimitedInputInventorySlot;
import fr.iglee42.evolvedmekanism.tiles.upgrade.AlloyerUpgradeData;
import mekanism.api.IContentsListener;
import mekanism.api.inventory.IInventorySlot;
import mekanism.api.math.MathUtils;
import mekanism.api.providers.IBlockProvider;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.api.recipes.cache.CachedRecipe.OperationTracker.RecipeError;
import mekanism.api.recipes.inputs.IInputHandler;
import mekanism.api.recipes.inputs.InputHelper;
import mekanism.common.Mekanism;
import mekanism.common.capabilities.holder.slot.InventorySlotHelper;
import mekanism.common.integration.computer.SpecialComputerMethodWrapper.ComputerIInventorySlotWrapper;
import mekanism.common.integration.computer.annotation.WrappingComputerMethod;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.slot.EnergyInventorySlot;
import mekanism.common.lib.transmitter.TransmissionType;
import mekanism.common.recipe.IMekanismRecipeTypeProvider;
import mekanism.common.tier.FactoryTier;
import mekanism.common.tile.component.config.ConfigInfo;
import mekanism.common.tile.component.config.DataType;
import mekanism.common.tile.component.config.slot.InventorySlotInfo;
import mekanism.common.upgrade.IUpgradeData;
import mekanism.common.util.InventoryUtils;
import mekanism.common.util.MekanismUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

//Mekanism Extras (Absolute -> Infinite) version of TileEntityAlloyingFactory, mirrors TileEntityExtraCombiningFactory
public class TileEntityExtraAlloyingFactory extends TileEntityExtraItemToItemFactory<AlloyerRecipe> implements TripleItemRecipeLookupHandler<AlloyerRecipe> {

    private static final List<RecipeError> TRACKED_ERROR_TYPES = List.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_INPUT,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT,
            RecipeError.NOT_ENOUGH_OUTPUT_SPACE,
            RecipeError.INPUT_DOESNT_PRODUCE_OUTPUT
    );
    private static final Set<RecipeError> GLOBAL_ERROR_TYPES = Set.of(
            RecipeError.NOT_ENOUGH_ENERGY,
            RecipeError.NOT_ENOUGH_SECONDARY_INPUT
    );

    private final IInputHandler<@NotNull ItemStack> extraInputHandler;
    private final IInputHandler<@NotNull ItemStack> secondExtraInputHandler;

    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getSecondaryInput", docPlaceholder = "secondary input slot")
    LimitedInputInventorySlot extraSlot;
    @WrappingComputerMethod(wrapper = ComputerIInventorySlotWrapper.class, methodNames = "getTertiaryInput", docPlaceholder = "tertiary input slot")
    LimitedInputInventorySlot secondExtraSlot;

    public TileEntityExtraAlloyingFactory(IBlockProvider blockProvider, BlockPos pos, BlockState state) {
        super(blockProvider, pos, state, TRACKED_ERROR_TYPES, GLOBAL_ERROR_TYPES);
        extraInputHandler = InputHelper.getInputHandler(extraSlot, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
        secondExtraInputHandler = InputHelper.getInputHandler(secondExtraSlot, RecipeError.NOT_ENOUGH_SECONDARY_INPUT);
        ConfigInfo itemConfig = configComponent.getConfig(TransmissionType.ITEM);
        if (itemConfig != null) {
            itemConfig.addSlotInfo(DataType.EXTRA, new InventorySlotInfo(true, true, extraSlot, secondExtraSlot));
        }
    }

    @Override
    protected void addSlots(InventorySlotHelper builder, IContentsListener listener, IContentsListener updateSortingListener) {
        super.addSlots(builder, listener, updateSortingListener);
        //Continue the Mekanism alloying factory capacity curve (64 per tier) past Ultimate
        int limit = (FactoryTier.ULTIMATE.ordinal() + tier.ordinal() + 2) * 64;
        builder.addSlot(extraSlot = LimitedInputInventorySlot.at(limit, this::containsRecipeB, markAllMonitorsChanged(listener), 7, 57));
        builder.addSlot(secondExtraSlot = LimitedInputInventorySlot.at(limit, this::containsRecipeC, markAllMonitorsChanged(listener), 7, 35));
        extraSlot.setSlotType(ContainerSlotType.EXTRA);
        secondExtraSlot.setSlotType(ContainerSlotType.EXTRA);
    }

    @Nullable
    @Override
    protected LimitedInputInventorySlot getExtraSlot() {
        return extraSlot;
    }

    @SuppressWarnings("unused")
    public LimitedInputInventorySlot getSecondExtraSlot() {
        return secondExtraSlot;
    }

    @Override
    public boolean isValidInputItem(@NotNull ItemStack stack) {
        return containsRecipeA(stack);
    }

    @Override
    protected int getNeededInput(AlloyerRecipe recipe, ItemStack inputStack) {
        return MathUtils.clampToInt(recipe.getMainInput().getNeededAmount(inputStack));
    }

    @Override
    protected boolean isCachedRecipeValid(@Nullable CachedRecipe<AlloyerRecipe> cached, @NotNull ItemStack stack) {
        if (cached != null) {
            AlloyerRecipe cachedRecipe = cached.getRecipe();
            return cachedRecipe.getMainInput().testType(stack) && (extraSlot.isEmpty() || cachedRecipe.getExtraInput().testType(extraSlot.getStack()));
        }
        return false;
    }

    @Override
    protected AlloyerRecipe findRecipe(int process, @NotNull ItemStack fallbackInput, @NotNull IInventorySlot outputSlot, @Nullable IInventorySlot secondaryOutputSlot) {
        ItemStack extra = extraSlot.getStack();
        ItemStack secondExtra = secondExtraSlot.getStack();
        ItemStack output = outputSlot.getStack();
        return getRecipeType().getInputCache().findTypeBasedRecipe(level, fallbackInput, extra, secondExtra,
                recipe -> InventoryUtils.areItemsStackable(recipe.getOutput(fallbackInput, extra, secondExtra), output));
    }

    @NotNull
    @Override
    public IMekanismRecipeTypeProvider<AlloyerRecipe, EMInputRecipeCache.TripleItem<AlloyerRecipe>> getRecipeType() {
        return EMRecipeType.ALLOYING;
    }

    @Nullable
    @Override
    public AlloyerRecipe getRecipe(int cacheIndex) {
        return findFirstRecipe(inputHandlers[cacheIndex], extraInputHandler, secondExtraInputHandler);
    }

    @NotNull
    @Override
    public CachedRecipe<AlloyerRecipe> createNewCachedRecipe(@NotNull AlloyerRecipe recipe, int cacheIndex) {
        return ThreeInputCachedRecipe.alloyer(recipe, recheckAllRecipeErrors[cacheIndex], inputHandlers[cacheIndex], extraInputHandler, secondExtraInputHandler, outputHandlers[cacheIndex])
                .setErrorsChanged(errors -> errorTracker.onErrorsChanged(errors, cacheIndex))
                .setCanHolderFunction(() -> MekanismUtils.canFunction(this))
                .setActive(active -> setActiveState(active, cacheIndex))
                .setEnergyRequirements(energyContainer::getEnergyPerTick, energyContainer)
                .setRequiredTicks(this::getTicksRequired)
                .setOnFinish(this::markForSave)
                .setBaselineMaxOperations(this::getBaselineMaxOperations)
                .setOperatingTicksChanged(operatingTicks -> progress[cacheIndex] = operatingTicks);
    }

    @Override
    public boolean isConfigurationDataCompatible(BlockEntityType<?> tileType) {
        //Replaces TileEntityExtraFactory's check, whose switch over FactoryType can't handle the ALLOYING value added at runtime
        if (getType() == tileType || EMTileEntityTypes.ALLOYER.get() == tileType) {
            return true;
        }
        for (ExtraFactoryTier factoryTier : ExtraEnumUtils.EXTRA_FACTORY_TIERS) {
            if (ExtraTileEntityTypes.getExtraFactoryTile(factoryTier, EMFactoryType.ALLOYING).get() == tileType) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void parseUpgradeData(@NotNull IUpgradeData upgradeData) {
        if (upgradeData instanceof AlloyerUpgradeData data) {
            //Generic factory upgrade data handling (energy, progress, slots, then components such as upgrades)
            super.parseUpgradeData(upgradeData);
            //Copy the stack using NBT so that if it is not actually valid due to a reload we don't crash
            extraSlot.deserializeNBT(data.extraSlot.serializeNBT());
            secondExtraSlot.deserializeNBT(data.secondaryExtraSlot.serializeNBT());
        } else {
            Mekanism.logger.warn("Unhandled upgrade data.", new Throwable());
        }
    }

    @NotNull
    @Override
    public AlloyerUpgradeData getUpgradeData() {
        EnergyInventorySlot energySlot = ((TileEntityExtraFactoryAccessor) this).evolvedmekanism$getEnergySlot();
        return new AlloyerUpgradeData(redstone, getControlType(), getEnergyContainer(), progress, energySlot, extraSlot, secondExtraSlot, inputSlots, outputSlots, isSorting(), getComponents());
    }
}
