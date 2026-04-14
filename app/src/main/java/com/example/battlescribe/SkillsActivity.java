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

        // Initialize Database
        SkillDB.init(this);

        // Load and display skills
        displaySkills();

        // Navigation
        setupNavigation();
    }

    private void displaySkills() {
        LinearLayout container = (LinearLayout) findViewById(R.id.skills_container);
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        List<Skill> skills = SkillDB.getAllSkills();
        final SharedPreferences prefs = getSharedPreferences("CharacterSkills", MODE_PRIVATE);

        for (int i = 0; i < skills.size(); i++) {
            final Skill skill = skills.get(i);
            View skillView = inflater.inflate(R.layout.skill_item, container, false);

            ImageView icon = (ImageView) skillView.findViewById(R.id.skill_icon);
            TextView name = (TextView) skillView.findViewById(R.id.skill_name);
            TextView desc = (TextView) skillView.findViewById(R.id.skill_desc);
            final TextView statusText = (TextView) skillView.findViewById(R.id.skill_status);
            final Button btnEquip = (Button) skillView.findViewById(R.id.btn_equip);
            final Button btnUnequip = (Button) skillView.findViewById(R.id.btn_unequip);

            icon.setImageBitmap(skill.iconBitmap);
            name.setText(skill.name);
            desc.setText(skill.description);

            boolean isEquipped = prefs.getBoolean("equipped_" + skill.id, false);
            updateUI(btnEquip, btnUnequip, statusText, isEquipped);

            btnEquip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getEquippedCount(prefs) < MAX_EQUIPPED_SKILLS) {
                        prefs.edit().putBoolean("equipped_" + skill.id, true).apply();
                        updateUI(btnEquip, btnUnequip, statusText, true);
                        Toast.makeText(SkillsActivity.this, skill.name + " equipped!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(SkillsActivity.this, "Max " + MAX_EQUIPPED_SKILLS + " skills allowed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnUnequip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    prefs.edit().putBoolean("equipped_" + skill.id, false).apply();
                    updateUI(btnEquip, btnUnequip, statusText, false);
                    Toast.makeText(SkillsActivity.this, skill.name + " unequipped!", Toast.LENGTH_SHORT).show();
                }
            });

            container.addView(skillView);
        }
    }

    private int getEquippedCount(SharedPreferences prefs) {
        int count = 0;
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith("equipped_")) {
                Object value = entry.getValue();
                if (value instanceof Boolean) {
                    if (((Boolean) value).booleanValue()) {
                        count++;
                    }
                }
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
            statusText.setText("UNEQUIPPED");
            statusText.setTextColor(Color.GRAY);
        }
    }

    private void setupNavigation() {
        findViewById(R.id.character).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SkillsActivity.this, Character.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        findViewById(R.id.shop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SkillsActivity.this, ShopActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }
}
