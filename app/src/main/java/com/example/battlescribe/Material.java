package com.example.battlescribe;

import android.graphics.Bitmap;

/**
 * Simple data container for crafting materials.
 * Stores the identity and visual icon of a raw resource (e.g., Iron Ore).
 */
public class Material {
    public int id;
    public String name;
    public Bitmap iconBitmap;

    public Material(int id, String name, Bitmap iconBitmap) {
        this.id = id;
        this.name = name;
        this.iconBitmap = iconBitmap;
    }
}
