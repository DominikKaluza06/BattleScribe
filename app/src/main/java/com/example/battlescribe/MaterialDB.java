package com.example.battlescribe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.HashMap;
import java.util.Map;

public class MaterialDB {
    private static final Map<Integer, Material> allMaterials = new HashMap<>();

    public static void init(Context context) {
        if (!allMaterials.isEmpty()) return;

        // For now, using placeholders or existing drawables if available
        // In a real scenario, we might have a materials sprite sheet
        Bitmap placeholder = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_help);

        addMaterial(new Material(1, "Iron Ore", placeholder));
        addMaterial(new Material(2, "Steel Bar", placeholder));
        addMaterial(new Material(3, "Bloodstone Shard", placeholder));
        addMaterial(new Material(4, "Wood", placeholder));
        addMaterial(new Material(5, "Leather", placeholder));
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
