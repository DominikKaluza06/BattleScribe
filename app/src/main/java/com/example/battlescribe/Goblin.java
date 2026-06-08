package com.example.battlescribe;

import android.content.Context;
import android.graphics.BitmapFactory;

/**
 * Goblin: A weak but common enemy.
 * 
 * SPECIAL TRAITS:
 * - Low HP and damage.
 * - Uses Mana for basic special attacks.
 * - Fast regeneration.
 */
public class Goblin extends Monster {
    public Goblin(Context context, int level, double difficultyMult) {
        super(
            "Goblin (Lv " + level + ")", 
            30 + (level - 1) * 20,       // Max HP: Base 30 + 20 per level
            20,                          // Max Mana
            3,                           // Mana Regen
            10 + (level - 1) * 2,        // Strength: Base 10 + 2 per level
            1 + (level - 1) * 2,         // Vitality: Base 1 + 2 per level
            (level - 1) * 2,             // Magic
            5 + (level - 1) * 2,         // Agility
            BitmapFactory.decodeResource(context.getResources(), R.drawable.goblin),
            (10 * level) / 4,            // Gold Reward: Lowest of all enemies
            (20 * level) / 2             // EXP Reward
        );
        this.currentMana = 0;
        
        // LOOT TABLE:
        this.materialDrops.put(1, 0.4); // 40% chance for Iron Ore
    }
}
