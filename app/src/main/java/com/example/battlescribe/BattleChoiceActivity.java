package com.example.battlescribe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BattleChoiceActivity extends AppCompatActivity {

    private int infiniteWave = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_choice);

        loadWaveProgress();
        setupNavigation();

        findViewById(R.id.btn_continue_story).setOnClickListener(v -> 
            Toast.makeText(this, "Story Mode coming soon!", Toast.LENGTH_SHORT).show()
        );

        Button btnInfinite = findViewById(R.id.btn_normal_fight);
        btnInfinite.setText("ENTER COMBAT (WAVE " + infiniteWave + ")");
        btnInfinite.setOnClickListener(v -> {
            Intent intent = new Intent(BattleChoiceActivity.this, BattleActivity.class);
            intent.putExtra("WAVE", infiniteWave);
            intent.putExtra("IS_INFINITE", true);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWaveProgress();
        Button btnInfinite = findViewById(R.id.btn_normal_fight);
        btnInfinite.setText("ENTER COMBAT (WAVE " + infiniteWave + ")");
    }

    private void loadWaveProgress() {
        SharedPreferences prefs = getSharedPreferences("BattleProgress", MODE_PRIVATE);
        infiniteWave = prefs.getInt("infinite_wave", 1);
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
