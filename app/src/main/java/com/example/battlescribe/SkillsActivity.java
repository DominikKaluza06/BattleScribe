package com.example.battlescribe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Map;

public class SkillsActivity extends AppCompatActivity {

    private static final int MAX_EQUIPPED_SKILLS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills);

        SkillDB.init(this);
        displaySkills();
        setupNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh skills list on resume to catch level-ups from battle
        displaySkills();
    }

    private void displaySkills() {
        LinearLayout container = findViewById(R.id.skills_container);
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        List<Skill> skills = SkillDB.getAllSkills();
        SharedPreferences skillPrefs = getSharedPreferences("CharacterSkills", MODE_PRIVATE);
        SharedPreferences statsPrefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);
        int playerLevel = statsPrefs.getInt("level", 1);

        for (Skill skill : skills) {
            View skillView = inflater.inflate(R.layout.skill_item, container, false);

            ImageView icon = skillView.findViewById(R.id.skill_icon);
            TextView name = skillView.findViewById(R.id.skill_name);
            TextView desc = skillView.findViewById(R.id.skill_desc);
            TextView statusText = skillView.findViewById(R.id.skill_status);
            Button btnEquip = skillView.findViewById(R.id.btn_equip);
            Button btnUnequip = skillView.findViewById(R.id.btn_unequip);

            icon.setImageBitmap(skill.iconBitmap);
            name.setText(skill.name + " (Lv " + skill.requiredLevel + ")");
            desc.setText(skill.description);

            boolean isEquipped = skillPrefs.getBoolean("equipped_" + skill.id, false);
            boolean isLevelMet = playerLevel >= skill.requiredLevel;

            if (!isLevelMet) {
                btnEquip.setEnabled(false);
                btnUnequip.setEnabled(false);
                statusText.setText("LOCKED (Requires Lv " + skill.requiredLevel + ")");
                statusText.setTextColor(Color.RED);
            } else {
                updateUI(btnEquip, btnUnequip, statusText, isEquipped);
            }

            btnEquip.setOnClickListener(v -> {
                if (getEquippedCount(skillPrefs) < MAX_EQUIPPED_SKILLS) {
                    skillPrefs.edit().putBoolean("equipped_" + skill.id, true).apply();
                    updateUI(btnEquip, btnUnequip, statusText, true);
                } else {
                    Toast.makeText(this, "Max 4 skills equipped!", Toast.LENGTH_SHORT).show();
                }
            });

            btnUnequip.setOnClickListener(v -> {
                skillPrefs.edit().putBoolean("equipped_" + skill.id, false).apply();
                updateUI(btnEquip, btnUnequip, statusText, false);
            });

            container.addView(skillView);
        }
    }

    private int getEquippedCount(SharedPreferences prefs) {
        int count = 0;
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith("equipped_") && entry.getValue() instanceof Boolean) {
                if ((Boolean) entry.getValue()) count++;
            }
        }
        return count;
    }

    private void updateUI(Button equip, Button unequip, TextView statusText, boolean isEquipped) {
        equip.setEnabled(!isEquipped);
        unequip.setEnabled(isEquipped);
        if (isEquipped) {
            statusText.setText("EQUIPPED");
            statusText.setTextColor(Color.GREEN);
        } else {
            statusText.setText("READY");
            statusText.setTextColor(Color.GRAY);
        }
    }

    private void setupNavigation() {
        findViewById(R.id.character).setOnClickListener(v -> {
            Intent intent = new Intent(this, Character.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        findViewById(R.id.shop).setOnClickListener(v -> {
            Intent intent = new Intent(this, ShopActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        findViewById(R.id.adventure).setOnClickListener(v -> {
            Intent intent = new Intent(this, BattleChoiceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        findViewById(R.id.crafting).setOnClickListener(v -> {
            Intent intent = new Intent(this, CraftingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }
}
