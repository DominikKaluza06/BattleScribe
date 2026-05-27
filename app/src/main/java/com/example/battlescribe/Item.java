package com.example.battlescribe;

import android.graphics.Bitmap;

/**
 * Represents an equippable item (Weapon, Armor, etc.).
 * Stores statistical bonuses and metadata required for shop and equipment systems.
 */
public class Item {
    public int id;
    public String name;
    public int iconResId;
    public SlotType slot;

    // Stat bonuses provided when the item is equipped
    public int strBonus, vitBonus, mgcBonus, agiBonus;

    public Bitmap iconBitmap;
    
    public int price;
    public int requiredChapter; // The story chapter required to unlock this item in the shop

    public Item(int id, String name, Bitmap icon, SlotType slot, 
                int strBonus, int vitBonus, int mgcBonus, int agiBonus, 
                int price, int requiredChapter) {
        this.id = id;
        this.name = name;
        this.iconBitmap = icon;
        this.slot = slot;
        
        // Assigning statistical bonuses
        this.strBonus = strBonus;
        this.vitBonus = vitBonus;
        this.mgcBonus = mgcBonus;
        this.agiBonus = agiBonus;
        
        this.price = price;
        this.requiredChapter = requiredChapter;
    }
}
