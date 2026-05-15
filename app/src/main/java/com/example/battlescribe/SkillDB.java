package com.example.battlescribe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SkillDB {
    private static final Map<Integer, Skill> allSkills = new HashMap<>();

    public static void init(Context context) {
        if (!allSkills.isEmpty()) return;
        
        Bitmap placeholderIcon = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_compass);

        // id, name, desc, icon, baseVal, mana, reqLevel, cooldown, scaling...
        // Power Strike: 1.3x STR damage, 2 turn cooldown
        addSkill(new Skill(1, "Power Strike", "A powerful blow that deals 1.3x STR damage.", 
                placeholderIcon, 0, 5, 1, 2, 1.3f, 0, 0, 0, 0f));

        // Heal: 15% Max HP + MGC bonus, 6 turn cooldown
        addSkill(new Skill(2, "Heal", "Restores health (15% Max HP + MGC bonus).", 
                placeholderIcon, 0, 10, 3, 6, 0, 0, 1.0f, 0, 0.15f));

        // Fireball: 1.3x MGC damage, 2 turn cooldown
        addSkill(new Skill(3, "Fireball", "Launches a fireball dealing 1.3x MGC damage.", 
                placeholderIcon, 0, 15, 5, 2, 0, 0, 1.3f, 0, 0f));
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
