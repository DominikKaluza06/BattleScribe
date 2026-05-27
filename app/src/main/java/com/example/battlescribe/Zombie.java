package com.example.battlescribe;

import android.content.Context;
import android.graphics.BitmapFactory;

public class Zombie extends Monster {
    public Zombie(Context context, int level, double difficultyMult) {
        super(
            "Zombie (Lv " + level + ")", 
            40 + (level - 1) * 20,       // hp scaled
            0,                          // mana
            0,                          // mana regen
            12 + (level - 1) * 2,        // str scaled
            (level - 1) * 2,             // vit scaled
            (level - 1) * 2,             // mgc scaled
            3 + (level - 1) * 2,         // agi scaled
            BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_report_image),
            (15 * level) / 4,           // gold reward reduced 4x
            (25 * level) / 2            // exp reward reduced 2x
        );
        this.currentMana = 0;
        
        // Zombies drop Iron Ore and Leather
        this.materialDrops.put(1, 0.5); // 50% Iron Ore
        this.materialDrops.put(5, 0.3); // 30% Leather
    }
}
