package com.example.battlescribe;

import android.content.Context;
import android.graphics.BitmapFactory;

public class Skeleton extends Monster {
    public Skeleton(Context context, int wave, double difficultyMult) {
        // Base stats for Skeleton: 50 HP, 15 STR
        super(
            "Skeleton (Lv " + wave + ")", 
            (int)(50 * difficultyMult), // hp scaled
            30,                          // mana
            5,                           // mana regen
            (int)(15 * difficultyMult), // str scaled
            0,                           // vit
            0,                           // mgc
            8,                           // agi
            BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_help), // Placeholder icon
            20 * wave,                   // gold reward
            40 * wave                    // exp reward
        );
        this.currentMana = 0;
        
        // Skeletons drop Steel Bars and Wood
        this.materialDrops.put(2, 0.3); // 30% Steel Bar
        this.materialDrops.put(4, 0.5); // 50% Wood
    }
}
