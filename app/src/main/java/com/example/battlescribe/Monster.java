package com.example.battlescribe;

import android.graphics.Bitmap;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all enemies in the game.
 * Extends Entity to include mana, rewards, and material drops.
 */
public class Monster extends Entity {
    // Combat resources
    public int maxMana;
    public int currentMana;
    public int manaRegen;
    
    // Visual representation
    public Bitmap icon;
    
    // Rewards given to the player upon defeating this monster
    public int goldReward;
    public int expReward;
    
    /**
     * Map of potential material drops.
     * Key: Material ID
     * Value: Drop chance (0.0 to 1.0)
     */
    public Map<Integer, Double> materialDrops = new HashMap<>();

    public Monster(String name, int hp, int mana, int manaRegen, int str, int vit, int mgc, int agi, Bitmap icon, int gold, int exp) {
        super(name, hp, str, vit, mgc, agi);
        this.maxMana = mana;
        this.currentMana = mana;
        this.manaRegen = manaRegen;
        this.icon = icon;
        this.goldReward = gold;
        this.expReward = exp;
    }

    /**
     * Checks if the monster's health has dropped to or below zero.
     */
    public boolean isDead() {
        return currentHp <= 0;
    }
}
