package com.example.battlescribe;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player extends Entity {

    private Map<SlotType, Item> equipment = new HashMap<>();
    private List<Skill> equippedSkills = new ArrayList<>();

    public Player(String name, Context context) {
        // Initializing with base 100 HP and base 10 stats
        super(name, 100, 10, 10, 10, 10);
        loadStatsAndEquipment(context);
    }

    private void loadStatsAndEquipment(Context context) {
        // 1. Load Base Stats from SharedPreferences
        SharedPreferences statsPrefs = context.getSharedPreferences("CharacterStats", Context.MODE_PRIVATE);
        this.baseStr = statsPrefs.getInt("str", 10);
        this.baseVit = statsPrefs.getInt("vit", 10); // Renamed from def
        this.baseMgc = statsPrefs.getInt("mgc", 10);
        this.baseAgi = statsPrefs.getInt("agi", 10);

        // 2. Load Equipped Items
        SharedPreferences equipPrefs = context.getSharedPreferences("EquippedItems", Context.MODE_PRIVATE);
        for (SlotType type : SlotType.values()) {
            int itemId = equipPrefs.getInt(type.name(), -1);
            if (itemId != -1) {
                Item item = ItemDB.getItem(itemId);
                if (item != null) {
                    equipment.put(type, item);
                }
            }
        }

        // 3. Load Equipped Skills
        SharedPreferences skillPrefs = context.getSharedPreferences("CharacterSkills", Context.MODE_PRIVATE);
        equippedSkills.clear();
        for (Skill skill : SkillDB.getAllSkills()) {
            if (skillPrefs.getBoolean("equipped_" + skill.id, false)) {
                equippedSkills.add(skill);
            }
        }
        
        // After loading stats and items, reset currentHp to the new MaxHp
        this.currentHp = getMaxHp();
    }

    public List<Skill> getEquippedSkills() {
        return equippedSkills;
    }

    @Override
    public int getTotalStr() {
        int total = baseStr;
        for (Item item : equipment.values()) {
            total += item.strBonus;
        }
        return total;
    }

    public int getTotalVit() {
        int total = baseVit;
        for (Item item : equipment.values()) {
            total += item.vitBonus;
        }
        return total;
    }

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
    
    @Override
    public int getMaxHp() {
        // Vitality increases max HP: Base 100 + 10 per total Vitality
        return 100 + (getTotalVit() * 10);
    }
}
