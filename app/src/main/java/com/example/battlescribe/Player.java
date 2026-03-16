package com.example.battlescribe;

import java.util.HashMap;
import java.util.Map;

public class Player extends Entity {

    private Map<SlotType, Item> equipment = new HashMap<>();

    public Player(String name) {
        // Name, HP, Str, Def, Mgc
        // Let's give the player 20 Base Magic to start
        super(name, 100, 10, 10, 10, 10);
    }

    public void equipItem(Item item) {
        equipment.put(item.slot, item);
        System.out.println("Equipped: " + item.name);
    }

    @Override
    public int getTotalStr() {
        int total = baseStr;
        for (Item item : equipment.values()) {
            total += item.strBonus;
        }
        return total;
    }

    @Override
    public int getTotalDef() {
        int total = baseDef;
        for (Item item : equipment.values()) {
            total += item.defBonus;
        }
        return total;
    }

    // <--- NEW MAGIC CALCULATION
    @Override
    public int getTotalMgc() {
        int total = baseMgc;
        for (Item item : equipment.values()) {
            total += item.mgcBonus;
        }
        return total;
    }

    @Override
    public int getTotalAgi() {
        int total = baseAgi;
        for (Item item : equipment.values()) {
            total += item.agiBonus;
        }
        return total;
    }
}