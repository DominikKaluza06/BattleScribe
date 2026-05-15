package com.example.battlescribe;

import android.content.Context;
import android.graphics.BitmapFactory;

public class Goblin extends Monster {
    public Goblin(Context context, int level) {
        // Scaling: 
        // HP: 30 + 15 per level
        // STR: 10 + 2 per level
        // DEF: 1 + 1 per level
        // MaxMana: 20
        // ManaRegen: 3 (Special attack every ~7 turns)
        // Rewards: Scalable Gold (10*L) and EXP (20*L)
        super(
            "Goblin (Lv " + level + ")", 
            30 + (level - 1) * 15, 
            20, 
            3, 
            10 + (level - 1) * 2, 
            1 + (level - 1) * 1, 
            0, 
            5, 
            BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_dialog_map),
            10 * level, 
            20 * level
        );
        this.currentMana = 0; // Start at 0 so it has to regen to use special
    }
}
