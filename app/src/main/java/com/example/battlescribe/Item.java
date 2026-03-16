package com.example.battlescribe;

import android.graphics.Bitmap;

/**
 * Represents an equippable item within the game, containing its display information,
 * equipment slot type, and statistical bonuses.
 */
public class Item {
    public int id;
    public String name;
    public int iconResId;
    public SlotType slot;

    // Stats
    public int strBonus, defBonus, mgcBonus, agiBonus;

    public Bitmap iconBitmap;

    public Item(int id, String name, Bitmap icon, SlotType slot, int strBonus, int defBonus, int mgcBonus, int agiBonus) {
        this.id = id;
        this.name = name;
        this.iconBitmap = icon;
        this.slot = slot;
        this.strBonus = strBonus;
        this.defBonus = defBonus;
        this.mgcBonus = mgcBonus;
        this.agiBonus = agiBonus;
    }
}