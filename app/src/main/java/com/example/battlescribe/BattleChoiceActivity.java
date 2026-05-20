package com.example.battlescribe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BattleChoiceActivity extends AppCompatActivity {

    private int infiniteWave = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_choice);
        hideSystemUI();

        loadWaveProgress();
        setupNavigation();

        findViewById(R.id.btn_continue_story).setOnClickListener(v -> 
            Toast.makeText(this, "Story Mode coming soon!", Toast.LENGTH_SHORT).show()
        );

        updateBattleButton();
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
        updateBattleButton();
    }

    private void loadWaveProgress() {
        SharedPreferences prefs = getSharedPreferences("BattleProgress", MODE_PRIVATE);
        infiniteWave = prefs.getInt("infinite_wave", 1);
    }

    private void updateBattleButton() {
        Button btnInfinite = findViewById(R.id.btn_normal_fight);
        if (btnInfinite != null) {
            btnInfinite.setText("ENTER COMBAT (WAVE " + infiniteWave + ")");
            btnInfinite.setOnClickListener(v -> {
                Intent intent = new Intent(BattleChoiceActivity.this, BattleActivity.class);
                intent.putExtra("WAVE", infiniteWave);
                intent.putExtra("IS_INFINITE", true);
                startActivity(intent);
            });
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
