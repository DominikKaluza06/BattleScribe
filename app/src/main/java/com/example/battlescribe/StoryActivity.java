package com.example.battlescribe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class StoryActivity extends AppCompatActivity {

    private TextView tvStoryText;
    private ImageView ivRewardIcon;
    private Button btnNext;
    private int currentChapter = 1;
    private int storyStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        tvStoryText = findViewById(R.id.tv_story_text);
        ivRewardIcon = findViewById(R.id.iv_reward_icon);
        btnNext = findViewById(R.id.btn_next);

        // Load progress
        SharedPreferences prefs = getSharedPreferences("StoryProgress", MODE_PRIVATE);
        currentChapter = prefs.getInt("chapter", 1);
        storyStep = prefs.getInt("step", 0);

        btnNext.setOnClickListener(v -> advanceStory());
        findViewById(R.id.btn_leave_story).setOnClickListener(v -> finish());

        updateStoryUI();
    }

    private void advanceStory() {
        storyStep++;
        
        // Save step immediately
        getSharedPreferences("StoryProgress", MODE_PRIVATE).edit()
                .putInt("chapter", currentChapter)
                .putInt("step", storyStep)
                .apply();

        if (currentChapter == 1 && storyStep > 4) {
            // End of Chapter 1 intro -> Battle with Goblin
            startBattle(1, "Goblin");
        } else if (currentChapter == 2 && storyStep > 2) {
             // End of Chapter 2 intro -> Battle with Skeleton
             startBattle(2, "Skeleton");
        } else {
            updateStoryUI();
        }
    }

    private void startBattle(int wave, String type) {
        Intent intent = new Intent(this, BattleActivity.class);
        intent.putExtra("WAVE", wave);
        intent.putExtra("STORY_MODE", true);
        startActivity(intent);
        finish();
    }

    private void updateStoryUI() {
        ivRewardIcon.setVisibility(View.GONE);
        btnNext.setVisibility(View.VISIBLE);
        btnNext.setText("NEXT");

        if (currentChapter == 1) {
            updateChapter1UI();
        } else if (currentChapter == 2) {
            updateChapter2UI();
        } else {
            tvStoryText.setText("Chapter " + currentChapter + ": More content coming soon...");
            btnNext.setVisibility(View.GONE);
        }
    }

    private void updateChapter1UI() {
        switch (storyStep) {
            case 0:
                tvStoryText.setText("Chapter 1.1: You wake up in a small village surrounded by mist. An old man approaches you.");
                break;
            case 1:
                tvStoryText.setText("Chapter 1.2: Old Man: 'Young scribe, the world is in danger. Take this, it was my father\'s.'");
                break;
            case 2:
                tvStoryText.setText("Chapter 1.3: You received an Iron Sword! Check your inventory to equip it.");
                giveReward(101); // Iron Sword ID
                ivRewardIcon.setVisibility(View.VISIBLE);
                Item ironSword = ItemDB.getItem(101);
                if (ironSword != null) ivRewardIcon.setImageBitmap(ironSword.iconBitmap);
                break;
            case 3:
                tvStoryText.setText("Chapter 1.4: Old Man: 'Goblins have been spotted near the gates. You must protect the village!'");
                break;
            case 4:
                tvStoryText.setText("Chapter 1.5: You head towards the gate. Suddenly, a Goblin jumps out of the shadows!");
                btnNext.setText("BATTLE!");
                break;
        }
    }

    private void updateChapter2UI() {
        switch (storyStep) {
            case 0:
                tvStoryText.setText("Chapter 2.1: With the Goblin defeated, the mist starts to clear. But a cold chill remains.");
                break;
            case 1:
                tvStoryText.setText("Chapter 2.2: You find an old graveyard at the edge of the woods. The ground begins to shake.");
                break;
            case 2:
                tvStoryText.setText("Chapter 2.3: Skeletons are rising! Prepare yourself for a new threat.");
                btnNext.setText("BATTLE!");
                break;
        }
    }

    private void giveReward(int itemId) {
        SharedPreferences prefs = getSharedPreferences("CharacterItems", MODE_PRIVATE);
        if (!prefs.getBoolean("owned_" + itemId, false)) {
            prefs.edit().putBoolean("owned_" + itemId, true).apply();
            Toast.makeText(this, "Iron Sword added to inventory!", Toast.LENGTH_SHORT).show();
        }
    }
}
