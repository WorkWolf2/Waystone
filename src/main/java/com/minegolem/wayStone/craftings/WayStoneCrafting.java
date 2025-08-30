package com.minegolem.wayStone.craftings;

import com.minegolem.wayStone.WayStone;
import com.minegolem.wayStone.data.WaystoneData;
import com.minegolem.wayStone.settings.Settings;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WayStoneCrafting {

    public static ShapedRecipe getRecipe() {

        ItemStack waystone = WaystoneData.getWaystoneItem();

        ShapedRecipe recipe = new ShapedRecipe(WayStone.INSTANCE.getWaystoneCraftingKey(), waystone);

        List<String> shapeConfig = Settings.shape;

        Map<Character, String> craftingMaterial = Settings.getCraftingMaterial();
        List<String> finalShape = new ArrayList<>();

        for (String line : shapeConfig) {
            StringBuilder newLine = new StringBuilder();
            for (char c : line.toCharArray()) {
                String mat = craftingMaterial.get(c);
                if (mat != null && !mat.equalsIgnoreCase("NOTHING")) {
                    newLine.append(c);
                } else {
                    newLine.append(" "); // oppure omettilo, ma la shape deve avere 3 char per riga!
                }
            }
            finalShape.add(newLine.toString());
        }

        recipe.shape(
                finalShape.get(0),
                finalShape.get(1),
                finalShape.get(2)
        );

        // Ingredienti:
        for (Map.Entry<Character, String> entry : craftingMaterial.entrySet()) {
            if (!entry.getValue().equalsIgnoreCase("NOTHING")) {
                recipe.setIngredient(entry.getKey(), Material.valueOf(entry.getValue()));
            }
        }

        return recipe;
    }
}
