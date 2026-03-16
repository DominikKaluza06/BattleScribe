package com.example.battlescribe;

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
    public int strBonus;
    public int defBonus;
    public int mgcBonus;
    public int agiBonus;

    public Item(int id, String name, int iconResId, SlotType slot, int strBonus, int defBonus, int mgcBonus, int agiBonus) {
        this.id = id;
        this.name = name;
        this.iconResId = iconResId;
        this.slot = slot;
        this.strBonus = strBonus;
        this.defBonus = defBonus;
        this.mgcBonus = mgcBonus;
        this.agiBonus = agiBonus;
    }
}