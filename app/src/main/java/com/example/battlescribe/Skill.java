package com.example.battlescribe;

import android.graphics.Bitmap;

/**
 * Represents a skill or spell that the player can cast during battle.
 * Contains data about costs, cooldowns, and how damage/healing is calculated.
 */
public class Skill {
    public int id;
    public String name;
    public String description;
    public Bitmap iconBitmap;
    public int baseValue;   // Base damage or healing amount
    public int manaCost;    // Amount of mana required to cast
    public int requiredLevel; // Level player must reach to unlock
    public int cooldown;    // Number of turns to wait before reuse

    // Scaling factors (percentages expressed as decimals)
    public float strScaling;   // Multiplied by total Strength
    public float vitScaling;   // Multiplied by total Vitality
    public float mgcScaling;   // Multiplied by total Magic
    public float agiScaling;   // Multiplied by total Agility
    public float maxHpScaling; // Multiplied by player's Max HP

    public Skill(int id, String name, String description, Bitmap iconBitmap, 
                 int baseValue, int manaCost, int requiredLevel, int cooldown,
                 float strScaling, float vitScaling, float mgcScaling, float agiScaling, float maxHpScaling) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.iconBitmap = iconBitmap;
        this.baseValue = baseValue;
        this.manaCost = manaCost;
        this.requiredLevel = requiredLevel;
        this.cooldown = cooldown;
        
        // Setting up how this skill scales with player stats
        this.strScaling = strScaling;
        this.vitScaling = vitScaling;
        this.mgcScaling = mgcScaling;
        this.agiScaling = agiScaling;
        this.maxHpScaling = maxHpScaling;
    }

    /**
     * Calculates the final value (damage or healing) of the skill based on current stats.
     */
    public int calculateValue(int str, int vit, int mgc, int agi, int maxHp) {
        return baseValue + (int)(
                str * strScaling + 
                vit * vitScaling + 
                mgc * mgcScaling + 
                agi * agiScaling + 
                maxHp * maxHpScaling
        );
    }
}
