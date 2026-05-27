package com.example.battlescribe;

/**
 * Base class for all living entities (Players and Monsters).
 * Manages core statistics, health, and combat-related calculations.
 */
public abstract class Entity {
    protected String name;
    protected int maxHp;
    protected int currentHp;

    // Core statistics
    protected int baseStr; // Increases damage and critical hit damage
    protected int baseVit; // Increases HP and physical defense
    protected int baseMgc; // Increases mana, mana regen, and skill effectiveness
    protected int baseAgi; // Increases speed and critical hit chance

    // Charge Time Battle (CTB) system variables
    protected int currentCharge = 0; // Ticks up based on speed; grants turn at 100

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

    /**
     * Calculates speed for the CTB system.
     * Monsters gain speed twice as slowly from Agility to prevent them from becoming too fast.
     */
    public int getSpeed() {
        if (this instanceof Monster) {
            return 10 + (getTotalAgi() / 2);
        }
        return 10 + getTotalAgi();
    }

    /**
     * Calculates physical damage reduction.
     * Monsters have "nerfed" defense: they only get 1/3 of the defense value from VIT compared to players.
     */
    public int getDefense() {
        if (this instanceof Monster) {
            return getTotalVit() / 3; 
        }
        return getTotalVit(); // Player: 1 VIT = 1 Defense
    }

    /**
     * Calculates the probability of a critical hit.
     * Returns decimal value (e.g. 0.05 = 5%).
     */
    public double getCritChance() {
        return getTotalAgi() * 0.005;
    }

    /**
     * Calculates damage multiplier for critical hits.
     * Base is 150%, increases with Strength.
     */
    public double getCritMultiplier() {
        return 1.5 + (getTotalStr() * 0.01);
    }

    public int getCurrentCharge() { return currentCharge; }
    public void addCharge(int amount) { this.currentCharge += amount; }
    public void reduceCharge(int amount) { this.currentCharge -= amount; }
    public void resetCharge() { this.currentCharge = 0; }

    public boolean isAlive() { return currentHp > 0; }

    /**
     * Standard method to handle taking damage, accounting for defense.
     * Guaranteed minimum of 1 damage.
     */
    public void takeDamage(int amount) {
        int damageAfterDefense = Math.max(1, amount - getDefense());
        this.currentHp -= damageAfterDefense;
        if (this.currentHp < 0) this.currentHp = 0;
    }

    public void heal(int amount) {
        this.currentHp += amount;
        if (this.currentHp > getMaxHp()) {
            this.currentHp = getMaxHp();
        }
    }
}
