package com.example.battlescribe;

public abstract class Entity {
    protected String name;
    protected int maxHp;
    protected int currentHp;

    // Stats
    protected int baseStr;
    protected int baseDef;
    protected int baseMgc;
    protected int baseAgi;

    public Entity(String name, int maxHp, int baseStr, int baseDef, int baseMgc, int baseAgi) {
        this.name = name;
        this.maxHp = maxHp;
        this.currentHp = maxHp;
        this.baseStr = baseStr;
        this.baseDef = baseDef;
        this.baseMgc = baseMgc;
        this.baseAgi = baseAgi;
    }

    public int getTotalStr() { return baseStr; }
    public int getTotalDef() { return baseDef; }
    public int getTotalMgc() { return baseMgc; }
    public int getTotalAgi() { return baseAgi; }

    public boolean isAlive() { return currentHp > 0; }

    public void takeDamage(int amount) {
        int realDamage = Math.max(0, amount - getTotalDef());
        this.currentHp -= realDamage;
        if (this.currentHp < 0) this.currentHp = 0;
        System.out.println(name + " took " + realDamage + " damage.");
    }
}