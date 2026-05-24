package com.example.battlescribe;

import android.content.Context;
import android.graphics.BitmapFactory;

public class Goblin extends Monster {
    public Goblin(Context context, int wave, double difficultyMult) {
        // Base stats for Goblin: 30 HP, 10 STR
        super(
            "Goblin (Lv " + wave + ")", 
            (int)(30 * difficultyMult), // hp scaled
            20,                          // mana
            3,                           // mana regen
            (int)(10 * difficultyMult), // str scaled
            (int)(1 * difficultyMult),  // vit (scaling vit for HP/Defense mix)
            0,                           // mgc
            5,                           // agi
            BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_dialog_map),
            10 * wave,                   // gold reward
            20 * wave                    // exp reward
        );
        this.currentMana = 0;
        
        // Goblins drop Iron Ore
        this.materialDrops.put(1, 0.4); // 40% Iron Ore
    }
}
