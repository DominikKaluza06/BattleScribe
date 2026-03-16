package com.example.battlescribe;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.TextView;

public class Character extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        // Load stats
        SharedPreferences prefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);

        // Get saved values (default to 10 if nothing is saved yet)
        ((TextView)findViewById(R.id.STR)).setText(String.valueOf(prefs.getInt("str", 10)));
        ((TextView)findViewById(R.id.DEF)).setText(String.valueOf(prefs.getInt("def", 10)));
        ((TextView)findViewById(R.id.MGC)).setText(String.valueOf(prefs.getInt("mgc", 10)));
        ((TextView)findViewById(R.id.AGI)).setText(String.valueOf(prefs.getInt("agi", 10)));
    }

    private void incrementStat(int textViewId, String statKey) {
        TextView statView = findViewById(textViewId);int currentScore = Integer.parseInt(statView.getText().toString());
        int newScore = currentScore + 1;

        // Update the UI
        statView.setText(String.valueOf(newScore));

        // SAVE to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(statKey, newScore);
        editor.apply(); // This saves the data to the phone's storage
    }

    public void increaseStr(View view) {
        incrementStat(R.id.STR, "str");
    }


    public void increaseDef(View view) {
        incrementStat(R.id.DEF, "def");
    }

    public void increaseMgc(View view) {
        incrementStat(R.id.MGC, "mgc");
    }

    public void increaseAgi(View view) {
        incrementStat(R.id.AGI, "mgc");
    }
}