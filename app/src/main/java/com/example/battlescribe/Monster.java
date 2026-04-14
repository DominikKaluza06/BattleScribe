package com.example.battlescribe;

import java.util.Random;

public class Monster extends Entity {
    
    private int experienceReward;
    private Item dropItem;
    private float dropChance; // 0.0 to 1.0 (e.g., 0.5 for 50%)

    public Monster(String name, int maxHp, int baseStr, int baseDef, int baseMgc, int baseAgi, int experienceReward, Item dropItem, float dropChance) {
        super(name, maxHp, baseStr, baseDef, baseMgc, baseAgi);
        this.experienceReward = experienceReward;
        this.dropItem = dropItem;
        this.dropChance = dropChance;
    }

    public int getExperienceReward() {
        return experienceReward;
    }

    public Item getDropItem() {
        return dropItem;
    }

    public float getDropChance() {
        return dropChance;
    }

    /**
     * Rolls to see if the monster drops its item upon death.
     * @return The Item dropped, or null if no drop occurred.
     */
    public Item rollDrop() {
        if (dropItem == null) return null;
        Random rand = new Random();
        if (rand.nextFloat() <= dropChance) {
            return dropItem;
        }
        return null;
    }
}
