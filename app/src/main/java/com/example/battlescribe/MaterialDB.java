package com.example.battlescribe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Material Database: Stores all raw resources available in the game.
 * Materials are used in recipes to craft new equipment.
 */
public class MaterialDB {
    private static final Map<Integer, Material> allMaterials = new HashMap<>();

    /**
     * Initializes the material list with IDs, names, and icons.
     * @param context Required to load image resources.
     */
    public static void init(Context context) {
        // Prevent duplicate initialization
        if (!allMaterials.isEmpty()) {
            return;
        }

        // Standard placeholder icon for materials
        Bitmap placeholder = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_help);

        // --- DEFINING MATERIALS ---

        addMaterial(new Material(
            1,            // ID: Unique identifier
            "Iron Ore",   // Name
            placeholder   // Icon
        ));

        addMaterial(new Material(
            2, 
            "Steel Bar", 
            placeholder
        ));

        addMaterial(new Material(
            3, 
            "Bloodstone Shard", 
            placeholder
        ));

        addMaterial(new Material(
            4, 
            "Wood", 
            placeholder
        ));

        addMaterial(new Material(
            5, 
            "Leather", 
            placeholder
        ));
    }

    /**
     * Internal helper to register a material in the map.
     */
    private static void addMaterial(Material material) {
        allMaterials.put(material.id, material);
    }

    /**
     * Retrieves a specific material by its ID.
     */
    public static Material getMaterial(int id) {
        return allMaterials.get(id);
    }

    /**
     * Returns the full collection of registered materials.
     */
    public static java.util.Collection<Material> getAllMaterials() {
        return allMaterials.values();
    }
}
