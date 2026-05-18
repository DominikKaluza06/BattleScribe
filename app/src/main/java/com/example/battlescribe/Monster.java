package com.example.battlescribe;

import android.graphics.Bitmap;
import java.util.HashMap;
import java.util.Map;

public class Monster extends Entity {
    public int maxMana;
    public int currentMana;
    public int manaRegen;
    public Bitmap icon;
    
    // Rewards
    public int goldReward;
    public int expReward;
    public Map<Integer, Double> materialDrops = new HashMap<>(); // Material ID -> Drop Chance (0.0 to 1.0)

    public Monster(String name, int hp, int mana, int manaRegen, int str, int def, int mgc, int agi, Bitmap icon, int gold, int exp) {
        super(name, hp, str, def, mgc, agi);
        this.maxMana = mana;
        this.currentMana = mana;
        this.manaRegen = manaRegen;
        this.icon = icon;
        this.goldReward = gold;
        this.expReward = exp;
    }

    public boolean isDead() {
        return currentHp <= 0;
    }
}
