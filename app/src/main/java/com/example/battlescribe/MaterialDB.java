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
        if (!allMaterials.isEmpty()) {
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        Bitmap mainSheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.sprite_rpg_items_icons_48x48px, options);
        
        int size = 48;

        // --- DEFINING MATERIALS ---
        // Using estimated coordinates from the 48x48 grid

        // Iron Ore: Left section, Row 4, Column 6? (x=240, y=144)
        addMaterial(new Material(
            1,            
            "Iron Ore",   
            Bitmap.createBitmap(mainSheet, 240, 144, size, size)
        ));

        // Steel Bar: Left section, Row 5, Column 3 (x=96, y=192)
        addMaterial(new Material(
            2, 
            "Steel Bar", 
            Bitmap.createBitmap(mainSheet, 96, 192, size, size)
        ));

        // Bloodstone Shard: Left section, Row 6, Column 3 (x=96, y=240)
        addMaterial(new Material(
            3, 
            "Bloodstone Shard", 
            Bitmap.createBitmap(mainSheet, 96, 240, size, size)
        ));

        // Wood: Left section, Row 4, Column 2 (x=48, y=144)
        addMaterial(new Material(
            4, 
            "Wood", 
            Bitmap.createBitmap(mainSheet, 48, 144, size, size)
        ));

        // Leather: Left section, Row 7, Column 2 (x=48, y=288)
        addMaterial(new Material(
            5, 
            "Leather", 
            Bitmap.createBitmap(mainSheet, 48, 288, size, size)
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
