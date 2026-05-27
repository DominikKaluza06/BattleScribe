package com.example.battlescribe;

import android.content.Context;
import android.graphics.BitmapFactory;

public class Skeleton extends Monster {
    public Skeleton(Context context, int level, double difficultyMult) {
        // Base stats for Skeleton (Lv 1): 50 HP, 15 STR, 0 VIT, 0 MGC, 8 AGI
        super(
            "Skeleton (Lv " + level + ")", 
            50 + (level - 1) * 20,       // hp scaled
            30,                          // mana
            5,                           // mana regen
            15 + (level - 1) * 2,        // str scaled
            (level - 1) * 2,             // vit scaled
            (level - 1) * 2,             // mgc scaled
            8 + (level - 1) * 2,         // agi scaled
            BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_help),
            (20 * level) / 4,            // gold reward reduced 4x
            (40 * level) / 2             // exp reward reduced 2x
        );
        this.currentMana = 0;
        
        // Skeletons drop Steel Bars and Wood
        this.materialDrops.put(2, 0.3); // 30% Steel Bar
        this.materialDrops.put(4, 0.5); // 50% Wood
    }
}
