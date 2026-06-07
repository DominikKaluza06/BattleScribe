package com.example.battlescribe;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;

/**
 * Material Database: Stores all raw resources available in the game.
 * Materials are used in recipes to craft new equipment or processed materials.
 */
public class MaterialDB {
    private static final Map<Integer, Material> allMaterials = new HashMap<>();

    public static void init(Context context) {
        if (!allMaterials.isEmpty()) {
            return;
        }

        SpriteManager.init(context);

        // --- DEFINING MATERIALS ---

        addMaterial(new Material(
            1, "Bronze Ore",
            SpriteManager.getItemSpriteRaw(SpriteManager.BRONZE_ORE_RAW[0], SpriteManager.BRONZE_ORE_RAW[1]),
            "Obtainable in: Forest"
        ));

        addMaterial(new Material(
            2, "Bronze Bar",
            SpriteManager.getItemSpriteRaw(SpriteManager.BRONZE_BAR_RAW[0], SpriteManager.BRONZE_BAR_RAW[1]),
            "Crafted from Bronze Ore"
        ));

        addMaterial(new Material(
            9, "Iron Ore",
            SpriteManager.getItemSpriteRaw(SpriteManager.IRON_ORE_RAW[0], SpriteManager.IRON_ORE_RAW[1]),
            "Obtainable in: Mountain"
        ));

        addMaterial(new Material(
            6, "Iron Bar",
            SpriteManager.getItemSpriteRaw(SpriteManager.IRON_BAR_RAW[0], SpriteManager.IRON_BAR_RAW[1]),
            "Crafted from Iron Ore"
        ));

        addMaterial(new Material(
            3, "Bloodstone Shard",
            SpriteManager.getItemSpriteRaw(SpriteManager.BLOODSTONE_SHARD_RAW[0], SpriteManager.BLOODSTONE_SHARD_RAW[1]),
            "Obtainable in: Graveyard"
        ));

        addMaterial(new Material(
            4, "Brown Wood",
            SpriteManager.getItemSpriteRaw(SpriteManager.WOOD_RAW[0], SpriteManager.WOOD_RAW[1]),
            "Obtainable in: Forest"
        ));

        addMaterial(new Material(
            5, "Leather",
            SpriteManager.getItemSpriteRaw(SpriteManager.LEATHER_RAW[0], SpriteManager.LEATHER_RAW[1]),
            "Obtainable in: Forest"
        ));

        addMaterial(new Material(
            8, "Stone",
            SpriteManager.getItemSpriteRaw(SpriteManager.STONE_RAW[0], SpriteManager.STONE_RAW[1]),
            "Obtainable in: Mountain"
        ));
    }

    private static void addMaterial(Material material) {
        allMaterials.put(material.id, material);
    }

    public static Material getMaterial(int id) {
        return allMaterials.get(id);
    }

    public static java.util.Collection<Material> getAllMaterials() {
        return allMaterials.values();
    }
}
