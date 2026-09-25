package fr.iglee42.evolvedmekanism.utils;

import mekanism.api.Upgrade;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraftforge.fml.loading.LoadingModList;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class EMUpgrades {

    /**
     * Mekanism Extras' creative upgrade (no energy use, one tick per operation), or null when Extras isn't installed.
     * Extras adds it to the Upgrade enum at runtime, so look it up by name instead of referencing Extras classes.
     */
    @Nullable
    public static final Upgrade CREATIVE = findExtrasUpgrade("CREATIVE");

    @Nullable
    private static Upgrade findExtrasUpgrade(String name) {
        if (LoadingModList.get().getModFileById("mekanism_extras") == null) {
            return null;
        }
        for (Upgrade upgrade : Upgrade.values()) {
            if (upgrade.name().equals(name)) {
                return upgrade;
            }
        }
        return null;
    }

    public static EnumSet<Upgrade> withCreative(EnumSet<Upgrade> upgrades) {
        if (CREATIVE != null) {
            upgrades.add(CREATIVE);
        }
        return upgrades;
    }

    public static boolean hasCreative(TileEntityMekanism tile) {
        return CREATIVE != null && tile.getComponent().isUpgradeInstalled(CREATIVE);
    }
}
