package com.example.battlescribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database for crafting recipes.
 * Defines which materials and how many are needed to craft specific items.
 */
public class RecipeDB {
    private static final List<Recipe> allRecipes = new ArrayList<>();

    // Static initializer to populate the recipe list once when the class is loaded
    static {
        // --- WEAPON RECIPES ---

        // Iron Sword Recipe: requires 5 Iron Ore and 2 Wood
        Map<Integer, Integer> ironSwordReq = new HashMap<>();
        ironSwordReq.put(1, 5); // ID 1: Iron Ore
        ironSwordReq.put(4, 2); // ID 4: Wood
        allRecipes.add(new Recipe(101, ironSwordReq));

        // Steel Sword Recipe: requires 3 Steel Bars and 1 Wood
        Map<Integer, Integer> steelSwordReq = new HashMap<>();
        steelSwordReq.put(2, 3); // ID 2: Steel Bar
        steelSwordReq.put(4, 1); // ID 4: Wood
        allRecipes.add(new Recipe(102, steelSwordReq));

        // Bloodstone Sword Recipe: requires 5 Bloodstone Shards and 2 Steel Bars
        Map<Integer, Integer> bloodstoneReq = new HashMap<>();
        bloodstoneReq.put(3, 5); // ID 3: Bloodstone Shard
        bloodstoneReq.put(2, 2); // ID 2: Steel Bar
        allRecipes.add(new Recipe(103, bloodstoneReq));

        // --- ARMOR RECIPES ---

        // Iron Plate Recipe: requires 10 Iron Ore and 5 Leather
        Map<Integer, Integer> ironPlateReq = new HashMap<>();
        ironPlateReq.put(1, 10); // ID 1: Iron Ore
        ironPlateReq.put(5, 5);  // ID 5: Leather
        allRecipes.add(new Recipe(201, ironPlateReq));
    }

    /**
     * @return A list of all available crafting recipes.
     */
    public static List<Recipe> getAllRecipes() {
        return allRecipes;
    }

    /**
     * Initialization method called by activities if setup is needed.
     */
    public static void init(CraftingActivity craftingActivity) {
        // Current implementation uses static block, so this is just a placeholder
    }
}
