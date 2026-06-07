package com.example.battlescribe;

import android.content.Context;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database for crafting recipes.
 * Defines which materials and how many are needed to craft specific items or refined materials.
 */
public class RecipeDB {
    private static final List<Recipe> allRecipes = new ArrayList<>();

    public static void init(Context context) {
        if (!allRecipes.isEmpty()) {
            return;
        }

        // --- MATERIAL REFINING (New: Recipes for Bars) ---

        // Bronze Bar (ID 2): 3 Bronze Ore (ID 1)
        Map<Integer, Integer> bronzeBarReq = new HashMap<>();
        bronzeBarReq.put(1, 3);
        allRecipes.add(new Recipe(2, bronzeBarReq));

        // Iron Bar (ID 6): 3 Iron Ore (ID 9)
        Map<Integer, Integer> ironBarReq = new HashMap<>();
        ironBarReq.put(9, 3);
        allRecipes.add(new Recipe(6, ironBarReq));

        // --- WEAPON RECIPES ---

        // Iron Sword (101): 5 Iron Bar, 2 Brown Wood
        Map<Integer, Integer> ironSwordReq = new HashMap<>();
        ironSwordReq.put(6, 5); // Iron Bar
        ironSwordReq.put(4, 2); // Brown Wood
        allRecipes.add(new Recipe(101, ironSwordReq));

        // Bronze Sword (102): 3 Bronze Bar, 1 Brown Wood
        Map<Integer, Integer> bronzeSwordReq = new HashMap<>();
        bronzeSwordReq.put(2, 3); // Bronze Bar
        bronzeSwordReq.put(4, 1); // Brown Wood
        allRecipes.add(new Recipe(102, bronzeSwordReq));

        // Bloodstone Sword (103): 5 Bloodstone Shards, 2 Iron Bars
        Map<Integer, Integer> bloodstoneReq = new HashMap<>();
        bloodstoneReq.put(3, 5); // Bloodstone Shard
        bloodstoneReq.put(6, 2); // Iron Bar
        allRecipes.add(new Recipe(103, bloodstoneReq));

        // --- ARMOR (CHEST PLATE) RECIPES ---

        // Leather Plate (201): 5 Leather
        Map<Integer, Integer> leatherPlateReq = new HashMap<>();
        leatherPlateReq.put(5, 5); // Leather
        allRecipes.add(new Recipe(201, leatherPlateReq));

        // Stone Plate (202): 10 Stone
        Map<Integer, Integer> stonePlateReq = new HashMap<>();
        stonePlateReq.put(8, 10); // Stone
        allRecipes.add(new Recipe(202, stonePlateReq));

        // Bronze Plate (203): 5 Bronze Bars
        Map<Integer, Integer> bronzePlateReq = new HashMap<>();
        bronzePlateReq.put(2, 5); // Bronze Bar
        allRecipes.add(new Recipe(203, bronzePlateReq));

        // Iron Plate (204): 5 Iron Bars
        Map<Integer, Integer> ironPlateReq = new HashMap<>();
        ironPlateReq.put(6, 5); // Iron Bar
        allRecipes.add(new Recipe(204, ironPlateReq));
        
        // --- BOOTS RECIPES ---
        
        // Leather Boots (301): 5 Leather
        Map<Integer, Integer> leatherBootsReq = new HashMap<>();
        leatherBootsReq.put(5, 5); // Leather
        allRecipes.add(new Recipe(301, leatherBootsReq));
    }

    public static List<Recipe> getAllRecipes() {
        return allRecipes;
    }

    public static void init(CraftingActivity craftingActivity) {
        init(craftingActivity.getApplicationContext());
    }
}
