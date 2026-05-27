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

    // CTB System (Charge Time Battle)
    protected int currentCharge = 0;

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

    // Speed: Base 10 + 1 per AGI point
    public int getSpeed() {
        return 10 + getTotalAgi();
    }

    // Crit Chance: 0.5% per AGI point (e.g., 20 AGI = 10% chance = 0.10)
    public double getCritChance() {
        return getTotalAgi() * 0.005;
    }

    // Crit Multiplier: 1.5x base + 1% per STR point (e.g., 50 STR = 2.0x crit)
    public double getCritMultiplier() {
        return 1.5 + (getTotalStr() * 0.01);
    }

    public int getCurrentCharge() { return currentCharge; }
    public void addCharge(int amount) { this.currentCharge += amount; }
    public void reduceCharge(int amount) { this.currentCharge -= amount; }
    public void resetCharge() { this.currentCharge = 0; }

    public boolean isAlive() { return currentHp > 0; }

    public void takeDamage(int amount) {
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
