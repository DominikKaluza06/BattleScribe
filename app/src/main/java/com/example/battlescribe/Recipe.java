package com.example.battlescribe;

import java.util.Map;

/**
 * Represents a crafting recipe.
 * Contains the ID of the resulting item and a map of required material IDs and quantities.
 */
public class Recipe {
    public int resultItemId;
    public Map<Integer, Integer> materialsRequired; // Map<MaterialID, Quantity>

    public Recipe(int resultItemId, Map<Integer, Integer> materialsRequired) {
        this.resultItemId = resultItemId;
        this.materialsRequired = materialsRequired;
    }
}
