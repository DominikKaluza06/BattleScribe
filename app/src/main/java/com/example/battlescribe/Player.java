package com.example.battlescribe;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents the player character.
 * This class handles loading stats and equipment from persistent storage
 * and calculates total attributes including bonuses from gear.
 */
public class Player extends Entity {

    // Storage for items currently equipped in each slot
    private Map<SlotType, Item> equipment = new HashMap<>();
    
    // List of skills the player has selected for use in battle
    private List<Skill> equippedSkills = new ArrayList<>();

    public Player(String name, Context context) {
        // Initializing with base 50 HP (at level 1) and base 10 stats
        super(name, 50, 10, 10, 10, 10);
        loadStatsAndEquipment(context);
    }

    /**
     * Reads all character data from SharedPreferences and updates the instance.
     */
    private void loadStatsAndEquipment(Context context) {
        // 1. Load Base Attributes
        SharedPreferences statsPrefs = context.getSharedPreferences("CharacterStats", Context.MODE_PRIVATE);
        this.baseStr = statsPrefs.getInt("str", 10);
        this.baseVit = statsPrefs.getInt("vit", 10); 
        this.baseMgc = statsPrefs.getInt("mgc", 10);
        this.baseAgi = statsPrefs.getInt("agi", 10);

        // 2. Load Equipped Items and map them to SlotTypes
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

        // 3. Filter skills to find which ones the player has chosen to equip
        SharedPreferences skillPrefs = context.getSharedPreferences("CharacterSkills", Context.MODE_PRIVATE);
        equippedSkills.clear();
        for (Skill skill : SkillDB.getAllSkills()) {
            if (skillPrefs.getBoolean("equipped_" + skill.id, false)) {
                equippedSkills.add(skill);
            }
        }
        
        // Ensure HP is full after loading state
        this.currentHp = getMaxHp();
    }

    public List<Skill> getEquippedSkills() {
        return equippedSkills;
    }

    // --- Dynamic Stat Calculations ---
    // These methods sum up the base stats with all active equipment bonuses.

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
        // Player HP Formula: Base 50 at 10 VIT, +10 HP for every point above 10.
        return 50 + (getTotalVit() - 10) * 10;
    }
}
