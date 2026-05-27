package com.example.battlescribe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Database class for all player skills.
 * Defines skill costs, effects, and how they scale with character attributes.
 */
public class SkillDB {
    private static final Map<Integer, Skill> allSkills = new HashMap<>();

    public static void init(Context context) {
        if (!allSkills.isEmpty()) return;
        
        Bitmap placeholderIcon = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_compass);

        // Power Strike: A physical skill that scales primarily with Strength.
        addSkill(new Skill(
            1,                // ID
            "Power Strike",   // Name
            "A powerful blow that deals 1.3x STR damage.", // Description
            placeholderIcon,  // Icon
            0,                // Base Value
            5,                // Mana Cost
            1,                // Required Level
            2,                // Cooldown (Turns)
            1.3f,             // STR Scaling (130% of Strength)
            0,                // VIT Scaling
            0,                // MGC Scaling
            0,                // AGI Scaling
            0f                // Max HP Scaling
        ));

        // Heal: A recovery skill. Scaling with Max HP ensures it stays relevant as the player grows.
        addSkill(new Skill(
            2, 
            "Heal", 
            "Restores health (15% Max HP + MGC bonus).", 
            placeholderIcon, 
            0, 
            10, 
            3, 
            6, 
            0, 
            0, 
            1.0f,             // MGC Scaling (100% of Magic)
            0, 
            0.15f             // Max HP Scaling (15% of Max HP)
        ));

        // Fireball: A magical attack. Damage is determined by the Magic stat.
        addSkill(new Skill(
            3, 
            "Fireball", 
            "Launches a fireball dealing 1.3x MGC damage.", 
            placeholderIcon, 
            0, 
            15, 
            5, 
            2, 
            0, 
            0, 
            1.3f,             // MGC Scaling (130% of Magic)
            0, 
            0f
        ));
    }

    private static void addSkill(Skill skill) {
        allSkills.put(skill.id, skill);
    }

    public static List<Skill> getAllSkills() {
        return new ArrayList<>(allSkills.values());
    }

    public static Skill getSkill(int id) {
        return allSkills.get(id);
    }
}
