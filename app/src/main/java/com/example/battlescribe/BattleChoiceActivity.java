package com.example.battlescribe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Random;

/**
 * Activity for selecting the battle mode (Story or Adventure) and specific biomes.
 * Manages the transitions between different selection steps using visibility toggles.
 */
public class BattleChoiceActivity extends AppCompatActivity {

    private int infiniteWave = 1;
    private LinearLayout layoutMainChoice;
    private LinearLayout layoutBiomeSelection;
    private LinearLayout layoutBiomeActions;
    private TextView tvBiomeName;
    private Button btnBiomeFight;
    private Button btnBiomeLeave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_choice);
        hideSystemUI();

        // Bind layout containers for the three-step selection process
        layoutMainChoice = findViewById(R.id.layout_main_choice);
        layoutBiomeSelection = findViewById(R.id.layout_biome_selection);
        layoutBiomeActions = findViewById(R.id.layout_biome_actions);
        
        tvBiomeName = findViewById(R.id.tv_biome_name);
        btnBiomeFight = findViewById(R.id.btn_biome_fight);
        btnBiomeLeave = findViewById(R.id.btn_biome_leave);

        loadWaveProgress();
        setupNavigation();

        // Story Mode button
        findViewById(R.id.btn_continue_story).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BattleChoiceActivity.this, StoryActivity.class);
                startActivity(intent);
            }
        });

        // Step 1: Main Menu to Biome Selection
        findViewById(R.id.btn_to_biomes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBiomeSelection();
            }
        });

        // Step 2: Back from Biomes to Main Menu
        findViewById(R.id.btn_back_to_main).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMainChoice();
            }
        });

        // Step 2: Selecting the Forest biome
        findViewById(R.id.btn_enter_forest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBiomeActions("DARK FOREST");
            }
        });

        // Step 3: Action - Start the actual battle
        btnBiomeFight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRandomBattle();
            }
        });

        // Step 3: Action - Leave the biome back to selection
        btnBiomeLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBiomeSelection();
            }
        });
    }

    /**
     * Standard utility to hide status and navigation bars for fullscreen immersion.
     */
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
        loadWaveProgress();
    }

    private void loadWaveProgress() {
        SharedPreferences prefs = getSharedPreferences("BattleProgress", MODE_PRIVATE);
        infiniteWave = prefs.getInt("infinite_wave", 1);
    }

    // --- Layout Visibility Management ---

    private void showBiomeSelection() {
        layoutMainChoice.setVisibility(View.GONE);
        layoutBiomeSelection.setVisibility(View.VISIBLE);
        layoutBiomeActions.setVisibility(View.GONE);
    }

    private void showBiomeActions(String name) {
        tvBiomeName.setText(name);
        layoutMainChoice.setVisibility(View.GONE);
        layoutBiomeSelection.setVisibility(View.GONE);
        layoutBiomeActions.setVisibility(View.VISIBLE);
    }

    private void showMainChoice() {
        layoutMainChoice.setVisibility(View.VISIBLE);
        layoutBiomeSelection.setVisibility(View.GONE);
        layoutBiomeActions.setVisibility(View.GONE);
    }

    /**
     * Logic for entering combat. Picks a monster type randomly and starts the BattleActivity.
     */
    private void startRandomBattle() {
        Random rnd = new Random();
        boolean isSkeleton = rnd.nextBoolean();
        
        Intent intent = new Intent(this, BattleActivity.class);
        intent.putExtra("IS_ADVENTURE", true); // Flag for level-scaling logic
        intent.putExtra("MONSTER_TYPE", isSkeleton ? "SKELETON" : "ZOMBIE");
        startActivity(intent);
    }

    /**
     * Links bottom navigation icons to their respective activities.
     */
    private void setupNavigation() {
        findViewById(R.id.character).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BattleChoiceActivity.this, Character.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        findViewById(R.id.shop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BattleChoiceActivity.this, ShopActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        findViewById(R.id.skills).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BattleChoiceActivity.this, SkillsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        findViewById(R.id.crafting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BattleChoiceActivity.this, CraftingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }
}
