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

/**
 * Activity for displaying story cutscenes and dialog.
 * Players progress through "steps" within "chapters" to receive rewards and trigger boss battles.
 */
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

        // Initialize UI components
        tvStoryText = findViewById(R.id.tv_story_text);
        ivRewardIcon = findViewById(R.id.iv_reward_icon);
        btnNext = findViewById(R.id.btn_next);

        // Load saved progress from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("StoryProgress", MODE_PRIVATE);
        currentChapter = prefs.getInt("chapter", 1);
        storyStep = prefs.getInt("step", 0);

        // Standard OnClickListener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                advanceStory();
            }
        });

        findViewById(R.id.btn_leave_story).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        updateStoryUI();
    }

    /**
     * Increments the story step and checks for battle triggers or chapter ends.
     */
    private void advanceStory() {
        storyStep++;
        
        // Save step immediately to ensure progress isn't lost if activity closes
        getSharedPreferences("StoryProgress", MODE_PRIVATE).edit()
                .putInt("chapter", currentChapter)
                .putInt("step", storyStep)
                .apply();

        // Check if we reached a point where a battle should start
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

    /**
     * Transitions from story mode into the Battle Activity.
     */
    private void startBattle(int wave, String type) {
        Intent intent = new Intent(this, BattleActivity.class);
        intent.putExtra("WAVE", wave);
        intent.putExtra("STORY_MODE", true);
        intent.putExtra("MONSTER_TYPE", type.toUpperCase());
        startActivity(intent);
        finish();
    }

    /**
     * Orchestrates the UI updates based on current chapter and step.
     */
    private void updateStoryUI() {
        // Reset dynamic components
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

    /**
     * Dialog definitions for Chapter 1.
     */
    private void updateChapter1UI() {
        switch (storyStep) {
            case 0:
                tvStoryText.setText("Chapter 1.1: You wake up in a small village surrounded by mist. An old man approaches you.");
                break;
            case 1:
                tvStoryText.setText("Chapter 1.2: Old Man: 'Young scribe, the world is in danger. Take this, it was my father\\'s.'");
                break;
            case 2:
                tvStoryText.setText("Chapter 1.3: You received an Iron Sword! Check your inventory to equip it.");
                giveReward(101); // 101: Iron Sword ID
                ivRewardIcon.setVisibility(View.VISIBLE);
                Item ironSword = ItemDB.getItem(101);
                if (ironSword != null) {
                    ivRewardIcon.setImageBitmap(ironSword.iconBitmap);
                }
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

    /**
     * Dialog definitions for Chapter 2.
     */
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

    /**
     * Safely grants an item to the player only if they don't already own it.
     */
    private void giveReward(int itemId) {
        SharedPreferences prefs = getSharedPreferences("CharacterItems", MODE_PRIVATE);
        if (!prefs.getBoolean("owned_" + itemId, false)) {
            prefs.edit().putBoolean("owned_" + itemId, true).apply();
            Item item = ItemDB.getItem(itemId);
            String itemName = (item != null) ? item.name : "New Item";
            Toast.makeText(this, itemName + " added to inventory!", Toast.LENGTH_SHORT).show();
        }
    }
}
