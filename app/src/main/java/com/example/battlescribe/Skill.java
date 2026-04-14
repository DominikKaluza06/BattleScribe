package com.example.battlescribe;

import android.graphics.Bitmap;

public class Skill {
    public int id;
    public String name;
    public String description;
    public Bitmap iconBitmap;
    public int baseValue;
    public int manaCost;

    // Scaling factors
    public float strScaling;
    public float defScaling;
    public float mgcScaling;
    public float agiScaling;
    public float maxHpScaling; // NEW: Scaling based on % of Max HP

    public Skill(int id, String name, String description, Bitmap iconBitmap, int baseValue, int manaCost,
                 float strScaling, float defScaling, float mgcScaling, float agiScaling, float maxHpScaling) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconBitmap = iconBitmap;
        this.baseValue = baseValue;
        this.manaCost = manaCost;
        this.strScaling = strScaling;
        this.defScaling = defScaling;
        this.mgcScaling = mgcScaling;
        this.agiScaling = agiScaling;
        this.maxHpScaling = maxHpScaling;
    }

    /**
     * Calculates total value (damage or healing) based on base value and character stats.
     */
    public int calculateValue(int str, int def, int mgc, int agi, int maxHp) {
        return baseValue + (int)(str * strScaling + def * defScaling + mgc * mgcScaling + agi * agiScaling + maxHp * maxHpScaling);
    }
}
