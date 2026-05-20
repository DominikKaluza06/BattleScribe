package com.example.battlescribe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

public class CraftingActivity extends AppCompatActivity {

    private final int[] recipeSlotIds = {
            R.id.recipe_slot1, R.id.recipe_slot2, R.id.recipe_slot3, R.id.recipe_slot4,
            R.id.recipe_slot5, R.id.recipe_slot6, R.id.recipe_slot7, R.id.recipe_slot8
    };

    private View craftingPanel;
    private TextView selectedRecipeName;
    private LinearLayout requirementsContainer;
    private Button btnCraft;

    private Recipe selectedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crafting);
        hideSystemUI();

        craftingPanel = findViewById(R.id.crafting_panel);
        selectedRecipeName = findViewById(R.id.selected_recipe_name);
        requirementsContainer = findViewById(R.id.requirements_container);
        btnCraft = findViewById(R.id.btn_craft);

        ItemDB.init(this);
        MaterialDB.init(this);

        setupRecipes();
        setupNavigation();

        btnCraft.setOnClickListener(v -> craftItem());
    }

    private void hideSystemUI() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            final WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            // Older versions
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

    private void setupRecipes() {
        List<Recipe> recipes = RecipeDB.getAllRecipes();
        for (int i = 0; i < recipeSlotIds.length; i++) {
            ImageView slot = findViewById(recipeSlotIds[i]);
            if (i < recipes.size()) {
                Recipe recipe = recipes.get(i);
                Item resultItem = ItemDB.getItem(recipe.resultItemId);
                if (resultItem != null) {
                    slot.setImageBitmap(resultItem.iconBitmap);
                    slot.setVisibility(View.VISIBLE);
                    slot.setOnClickListener(v -> showRecipe(recipe));
                }
            } else {
                slot.setVisibility(View.GONE);
            }
        }
    }

    private void showRecipe(Recipe recipe) {
        selectedRecipe = recipe;
        Item item = ItemDB.getItem(recipe.resultItemId);
        selectedRecipeName.setText(item.name);
        
        requirementsContainer.removeAllViews();
        SharedPreferences matPrefs = getSharedPreferences("MaterialInventory", MODE_PRIVATE);
        
        boolean canCraft = true;
        for (Map.Entry<Integer, Integer> entry : recipe.materialsRequired.entrySet()) {
            Material mat = MaterialDB.getMaterial(entry.getKey());
            int owned = matPrefs.getInt("mat_" + entry.getKey(), 0);
            int required = entry.getValue();
            
            // Create a vertical layout for each material (Icon + Text)
            LinearLayout matLayout = new LinearLayout(this);
            matLayout.setOrientation(LinearLayout.VERTICAL);
            matLayout.setGravity(Gravity.CENTER);
            matLayout.setPadding(16, 0, 16, 0);

            ImageView iv = new ImageView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, 100);
            iv.setLayoutParams(lp);
            iv.setImageBitmap(mat.iconBitmap);
            
            TextView tv = new TextView(this);
            tv.setText(owned + "/" + required);
            tv.setTextColor(owned >= required ? Color.GREEN : Color.RED);
            tv.setGravity(Gravity.CENTER);

            matLayout.addView(iv);
            matLayout.addView(tv);
            requirementsContainer.addView(matLayout);

            if (owned < required) canCraft = false;
        }
        
        btnCraft.setEnabled(canCraft);
        craftingPanel.setVisibility(View.VISIBLE);
    }

    private void craftItem() {
        if (selectedRecipe == null) return;
        
        SharedPreferences matPrefs = getSharedPreferences("MaterialInventory", MODE_PRIVATE);
        SharedPreferences itemPrefs = getSharedPreferences("CharacterItems", MODE_PRIVATE);
        
        // Double check requirements
        for (Map.Entry<Integer, Integer> entry : selectedRecipe.materialsRequired.entrySet()) {
            int owned = matPrefs.getInt("mat_" + entry.getKey(), 0);
            if (owned < entry.getValue()) {
                Toast.makeText(this, "Not enough materials!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Consume materials
        SharedPreferences.Editor editor = matPrefs.edit();
        for (Map.Entry<Integer, Integer> entry : selectedRecipe.materialsRequired.entrySet()) {
            int owned = matPrefs.getInt("mat_" + entry.getKey(), 0);
            editor.putInt("mat_" + entry.getKey(), owned - entry.getValue());
        }
        editor.apply();

        // Add item to inventory
        itemPrefs.edit().putBoolean("owned_" + selectedRecipe.resultItemId, true).apply();
        
        Toast.makeText(this, "Crafted " + ItemDB.getItem(selectedRecipe.resultItemId).name + "!", Toast.LENGTH_SHORT).show();
        showRecipe(selectedRecipe); // Refresh UI
    }

    private void setupNavigation() {
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
        findViewById(R.id.adventure).setOnClickListener(v -> {
            Intent intent = new Intent(this, BattleChoiceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        findViewById(R.id.character).setOnClickListener(v -> {
            Intent intent = new Intent(this, Character.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }
}
