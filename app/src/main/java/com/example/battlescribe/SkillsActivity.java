package com.example.battlescribe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import java.util.Map;

/**
 * Activity for managing and equipping player skills.
 * Allows equipping up to 4 skills that have been unlocked by level.
 */
public class SkillsActivity extends AppCompatActivity {

    private static final int MAX_EQUIPPED_SKILLS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skills);
        hideSystemUI();

        SkillDB.init(this);
        displaySkills();
        setupNavigation();
    }

    private void hideSystemUI() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            final WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        displaySkills();
    }

    /**
     * Dynamically populates the skill list from the database.
     * Checks requirements and equipment status.
     */
    private void displaySkills() {
        LinearLayout container = findViewById(R.id.skills_container);
        container.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        List<Skill> skills = SkillDB.getAllSkills();
        final SharedPreferences skillPrefs = getSharedPreferences("CharacterSkills", MODE_PRIVATE);
        SharedPreferences statsPrefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);
        int playerLevel = statsPrefs.getInt("level", 1);

        for (final Skill skill : skills) {
            View skillView = inflater.inflate(R.layout.skill_item, container, false);

            ImageView icon = skillView.findViewById(R.id.skill_icon);
            TextView name = skillView.findViewById(R.id.skill_name);
            TextView desc = skillView.findViewById(R.id.skill_desc);
            final Button btnAction = skillView.findViewById(R.id.btn_skill_action);
            TextView tvRequirement = skillView.findViewById(R.id.tv_skill_requirement);

            icon.setImageBitmap(skill.iconBitmap);
            name.setText(skill.name);
            desc.setText(skill.description);

            boolean isEquipped = skillPrefs.getBoolean("equipped_" + skill.id, false);
            boolean isLevelMet = playerLevel >= skill.requiredLevel;

            // Handle skill locking based on player level
            if (!isLevelMet) {
                btnAction.setVisibility(View.GONE);
                tvRequirement.setVisibility(View.VISIBLE);
                tvRequirement.setText(getString(R.string.skill_status_locked, skill.requiredLevel));
            } else {
                tvRequirement.setVisibility(View.GONE);
                btnAction.setVisibility(View.VISIBLE);
                updateButtonState(btnAction, isEquipped);
            }

            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean currentlyEquipped = skillPrefs.getBoolean("equipped_" + skill.id, false);
                    if (currentlyEquipped) {
                        // Unequip logic
                        skillPrefs.edit().putBoolean("equipped_" + skill.id, false).apply();
                        updateButtonState(btnAction, false);
                    } else {
                        // Equip logic with limit check
                        if (getEquippedCount(skillPrefs) < MAX_EQUIPPED_SKILLS) {
                            skillPrefs.edit().putBoolean("equipped_" + skill.id, true).apply();
                            updateButtonState(btnAction, true);
                        } else {
                            Toast.makeText(SkillsActivity.this, getString(R.string.toast_max_skills), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            container.addView(skillView);
        }
    }

    /**
     * Iterates through preferences to find how many skills are currently toggled on.
     */
    private int getEquippedCount(SharedPreferences prefs) {
        int count = 0;
        Map<String, ?> allEntries = prefs.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            if (entry.getKey().startsWith("equipped_") && entry.getValue() instanceof Boolean) {
                if ((Boolean) entry.getValue()) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Changes button appearance based on whether a skill is equipped.
     */
    private void updateButtonState(Button btn, boolean isEquipped) {
        if (isEquipped) {
            btn.setText(R.string.btn_unequip);
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.RED));
        } else {
            btn.setText(R.string.btn_equip);
            btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50")));
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
        findViewById(R.id.adventure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SkillsActivity.this, BattleChoiceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        findViewById(R.id.crafting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SkillsActivity.this, CraftingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }
}
