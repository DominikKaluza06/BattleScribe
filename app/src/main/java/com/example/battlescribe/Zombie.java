package com.example.battlescribe;

import android.content.Context;
import android.graphics.BitmapFactory;

/**
 * Zombie: A standard early-game enemy.
 * Its statistics scale linearly with level to stay competitive with the player.
 */
public class Zombie extends Monster {
    public Zombie(Context context, int level, double difficultyMult) {
        super(
            "Zombie (Lv " + level + ")", 
            40 + (level - 1) * 20,       // Max HP: Starts at 40, gains 20 per level
            0,                          // Max Mana: Zombies use brute force, no mana
            0,                          // Mana Regen
            12 + (level - 1) * 2,        // Strength: Base 12, +2 per level (Increases damage)
            (level - 1) * 2,             // Vitality: Base 0, +2 per level (Defense is nerfed to 1/3 in Entity.java)
            (level - 1) * 2,             // Magic: Base 0, +2 per level
            3 + (level - 1) * 2,         // Agility: Base 3, +2 per level (Increases Speed/Crit)
            BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_report_image),
            (15 * level) / 4,           // Gold Reward: Scaled and balanced
            (25 * level) / 2            // EXP Reward: Scaled and balanced
        );
        this.currentMana = 0;
        
        // Loot Table: Mapping Material ID to Drop Chance (0.0 to 1.0)
        this.materialDrops.put(1, 0.5); // 50% chance for Iron Ore
        this.materialDrops.put(5, 0.3); // 30% chance for Leather
    }
}
