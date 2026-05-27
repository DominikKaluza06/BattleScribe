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

        layoutMainChoice = findViewById(R.id.layout_main_choice);
        layoutBiomeSelection = findViewById(R.id.layout_biome_selection);
        layoutBiomeActions = findViewById(R.id.layout_biome_actions);
        tvBiomeName = findViewById(R.id.tv_biome_name);
        btnBiomeFight = findViewById(R.id.btn_biome_fight);
        btnBiomeLeave = findViewById(R.id.btn_biome_leave);

        loadWaveProgress();
        setupNavigation();

        findViewById(R.id.btn_continue_story).setOnClickListener(v -> {
            Intent intent = new Intent(BattleChoiceActivity.this, StoryActivity.class);
            startActivity(intent);
        });

        // Main to Biomes
        findViewById(R.id.btn_to_biomes).setOnClickListener(v -> showBiomeSelection());

        // Back from Biomes to Main
        findViewById(R.id.btn_back_to_main).setOnClickListener(v -> showMainChoice());

        // Forest Biome Click
        findViewById(R.id.btn_enter_forest).setOnClickListener(v -> showBiomeActions("DARK FOREST"));

        // Biome Actions
        btnBiomeFight.setOnClickListener(v -> startRandomBattle());
        btnBiomeLeave.setOnClickListener(v -> showBiomeSelection());
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
        loadWaveProgress();
    }

    private void loadWaveProgress() {
        SharedPreferences prefs = getSharedPreferences("BattleProgress", MODE_PRIVATE);
        infiniteWave = prefs.getInt("infinite_wave", 1);
    }

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

    private void startRandomBattle() {
        Random rnd = new Random();
        boolean isSkeleton = rnd.nextBoolean();
        
        Intent intent = new Intent(this, BattleActivity.class);
        intent.putExtra("IS_ADVENTURE", true);
        intent.putExtra("MONSTER_TYPE", isSkeleton ? "SKELETON" : "ZOMBIE");
        startActivity(intent);
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
        findViewById(R.id.skills).setOnClickListener(v -> {
            Intent intent = new Intent(this, SkillsActivity.class);
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
