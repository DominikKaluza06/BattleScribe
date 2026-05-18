package com.example.battlescribe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeDB {
    private static final List<Recipe> allRecipes = new ArrayList<>();

    static {
        // Iron Sword Recipe: 5 Iron Ore, 2 Wood
        Map<Integer, Integer> ironSwordReq = new HashMap<>();
        ironSwordReq.put(1, 5); // Iron Ore
        ironSwordReq.put(4, 2); // Wood
        allRecipes.add(new Recipe(101, ironSwordReq));

        // Steel Sword Recipe: 3 Steel Bar, 1 Wood
        Map<Integer, Integer> steelSwordReq = new HashMap<>();
        steelSwordReq.put(2, 3); // Steel Bar
        steelSwordReq.put(4, 1); // Wood
        allRecipes.add(new Recipe(102, steelSwordReq));

        // Bloodstone Sword Recipe: 5 Bloodstone Shard, 2 Steel Bar
        Map<Integer, Integer> bloodstoneReq = new HashMap<>();
        bloodstoneReq.put(3, 5); // Bloodstone Shard
        bloodstoneReq.put(2, 2); // Steel Bar
        allRecipes.add(new Recipe(103, bloodstoneReq));

        // Iron Plate Recipe: 10 Iron Ore, 5 Leather
        Map<Integer, Integer> ironPlateReq = new HashMap<>();
        ironPlateReq.put(1, 10); // Iron Ore
        ironPlateReq.put(5, 5);  // Leather
        allRecipes.add(new Recipe(201, ironPlateReq));
    }

    public static List<Recipe> getAllRecipes() {
        return allRecipes;
    }
}
