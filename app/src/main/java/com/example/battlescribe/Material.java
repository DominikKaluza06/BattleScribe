package com.example.battlescribe;

import android.graphics.Bitmap;

/**
 * Simple data container for crafting materials.
 * Stores the identity, visual icon, and location info of a raw resource.
 */
public class Material {
    public int id;
    public String name;
    public Bitmap iconBitmap;
    public String obtainableFrom; // Where the player can find this material

    public Material(int id, String name, Bitmap iconBitmap, String obtainableFrom) {
        this.id = id;
        this.name = name;
        this.iconBitmap = iconBitmap;
        this.obtainableFrom = obtainableFrom;
    }
}
