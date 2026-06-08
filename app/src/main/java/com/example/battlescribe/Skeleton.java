package com.example.battlescribe;

import android.content.Context;
import android.graphics.BitmapFactory;

/**
 * Skeleton: A faster but more fragile undead enemy.
 */
public class Skeleton extends Monster {
    public Skeleton(Context context, int level, double difficultyMult) {
        super(
            "Skeleton (Lv " + level + ")", 
            50 + (level - 1) * 20,       // Max HP: Base 50 + 20 per level
            30,                          // Max Mana
            5,                           // Mana Regen
            15 + (level - 1) * 2,        // Strength: Base 15 + 2 per level
            (level - 1) * 2,             // Vitality: Base 0 + 2 per level
            (level - 1) * 2,             // Magic
            8 + (level - 1) * 2,         // Agility: Base 8 + 2 per level (High speed)
            BitmapFactory.decodeResource(context.getResources(), R.drawable.skeleton),
            (20 * level) / 4,            // Gold Reward
            (40 * level) / 2             // EXP Reward
        );
        this.currentMana = 0;
        
        // Loot Table
        this.materialDrops.put(2, 0.3); // 30% Steel Bar
        this.materialDrops.put(4, 0.5); // 50% Wood
    }
}
