package com.example.battlescribe;

import android.graphics.Bitmap;

public class Skill {
    public int id;
    public String name;
    public String description;
    public Bitmap iconBitmap;
    public int baseValue;
    public int manaCost;
    public int requiredLevel;
    public int cooldown;

    // Scaling factors
    public float strScaling;
    public float vitScaling;
    public float mgcScaling;
    public float agiScaling;
    public float maxHpScaling; 

    public Skill(int id, String name, String description, Bitmap iconBitmap, int baseValue, int manaCost, int requiredLevel, int cooldown,
                 float strScaling, float vitScaling, float mgcScaling, float agiScaling, float maxHpScaling) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconBitmap = iconBitmap;
        this.baseValue = baseValue;
        this.manaCost = manaCost;
        this.requiredLevel = requiredLevel;
        this.cooldown = cooldown;
        this.strScaling = strScaling;
        this.vitScaling = vitScaling;
        this.mgcScaling = mgcScaling;
        this.agiScaling = agiScaling;
        this.maxHpScaling = maxHpScaling;
    }

    /**
     * Calculates total value (damage or healing) based on base value and character stats.
     */
    public int calculateValue(int str, int vit, int mgc, int agi, int maxHp) {
        return baseValue + (int)(str * strScaling + vit * vitScaling + mgc * mgcScaling + agi * agiScaling + maxHp * maxHpScaling);
    }
}
