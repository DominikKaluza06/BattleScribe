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
        Bitmap placeholderIcon = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_compass);

        // Power Strike: 1.5x STR damage
        addSkill(new Skill(1, "Power Strike", "A powerful blow that deals 1.5x STR damage.", 
                placeholderIcon, 0, 5, 1.5f, 0, 0, 0, 0f));

        // Heal: 15% Max HP + 1.0x MGC scaling
        addSkill(new Skill(2, "Heal", "Restores health (15% Max HP + MGC bonus).", 
                placeholderIcon, 0, 10, 0, 0, 1.0f, 0, 0.15f));

        // Fireball: 10 base + 1.0x MGC damage
        addSkill(new Skill(3, "Fireball", "Launches a fireball dealing (10 + MGC) damage.", 
                placeholderIcon, 10, 15, 0, 0, 1.0f, 0, 0f));
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
