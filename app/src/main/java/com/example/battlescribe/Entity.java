package com.example.battlescribe;

public abstract class Entity {
    protected String name;
    protected int maxHp;
    protected int currentHp;

    // Stats
    protected int baseStr;
    protected int baseVit;
    protected int baseMgc;
    protected int baseAgi;

    public Entity(String name, int maxHp, int baseStr, int baseVit, int baseMgc, int baseAgi) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.baseStr = baseStr;
        this.baseVit = baseVit;
        this.baseMgc = baseMgc;
        this.baseAgi = baseAgi;
    }

    public int getMaxHp() { return maxHp; }
    public int getTotalStr() { return baseStr; }
    public int getTotalVit() { return baseVit; }
    public int getTotalMgc() { return baseMgc; }
    public int getTotalAgi() { return baseAgi; }

    public boolean isAlive() { return currentHp > 0; }

    public void takeDamage(int amount) {
        // Vitality no longer acts as flat damage reduction for monsters in infinite wave, 
        // but we keep the logic here for general use if needed, or set defense to 0.
        int realDamage = Math.max(1, amount); 
        this.currentHp -= realDamage;
        if (this.currentHp < 0) this.currentHp = 0;
    }

    public void heal(int amount) {
        this.currentHp += amount;
        if (this.currentHp > getMaxHp()) {
            this.currentHp = getMaxHp();
        }
    }
}