"""Source-level regression guard for APT input acceptance.

This checks slot wiring, not a live Minecraft/Forge interaction.
"""
import pathlib
import re
import unittest

SOURCE = (
    pathlib.Path(__file__).resolve().parents[1]
    / "src/main/java/fr/iglee42/evolvedmekanism/multiblock/apt/APTMultiblockData.java"
)


class APTInputFilterTest(unittest.TestCase):
    def test_item_slot_accepts_recipe_items_without_chemical_in_tank(self):
        source = SOURCE.read_text(encoding="utf-8")
        self.assertRegex(
            source,
            r"InputInventorySlot\.at\(\s*this::hasRecipeWith\s*,\s*this::hasRecipeWith\s*,",
        )
        self.assertRegex(
            source,
            r"private boolean hasRecipeWith\(ItemStack item\)\s*\{[^}]*"
            r"getItemInput\(\)\.testType\(item\)",
            "A valid item must be restricted to APT recipe item inputs",
        )
        self.assertRegex(
            source,
            r"filter\(r\s*->\s*r\.test\(inputSlot\.getStack\(\),\s*inputTank\.getStack\(\)\)\)",
            "Processing must still require both recipe inputs",
        )


if __name__ == "__main__":
    unittest.main()
