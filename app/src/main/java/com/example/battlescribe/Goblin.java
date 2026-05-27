package com.example.battlescribe;

import android.content.Context;
import android.graphics.BitmapFactory;

public class Goblin extends Monster {
    public Goblin(Context context, int level, double difficultyMult) {
        // Base stats for Goblin (Lv 1): 30 HP, 10 STR, 1 VIT, 0 MGC, 5 AGI
        super(
            "Goblin (Lv " + level + ")", 
            30 + (level - 1) * 20,       // hp scaled
            20,                          // mana
            3,                           // mana regen
            10 + (level - 1) * 2,        // str scaled
            1 + (level - 1) * 2,         // vit scaled
            (level - 1) * 2,             // mgc scaled
            5 + (level - 1) * 2,         // agi scaled
            BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_dialog_map),
            (10 * level) / 4,            // gold reward reduced 4x
            (20 * level) / 2             // exp reward reduced 2x
        );
        this.currentMana = 0;
        
        // Goblins drop Iron Ore
        this.materialDrops.put(1, 0.4); // 40% Iron Ore
    }
}
