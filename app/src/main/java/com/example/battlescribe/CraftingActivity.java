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
import java.util.List;
import java.util.Map;

public class CraftingActivity extends AppCompatActivity {

    private final int[] recipeSlotIds = {
            R.id.recipe_slot1, R.id.recipe_slot2, R.id.recipe_slot3, R.id.recipe_slot4,
            R.id.recipe_slot5, R.id.recipe_slot6, R.id.recipe_slot7, R.id.recipe_slot8
    };

    private View craftingPanel;
    private TextView selectedRecipeName;
    private TextView materialList;
    private Button btnCraft;

    private Recipe selectedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crafting);

        craftingPanel = findViewById(R.id.crafting_panel);
        selectedRecipeName = findViewById(R.id.selected_recipe_name);
        materialList = findViewById(R.id.material_list);
        btnCraft = findViewById(R.id.btn_craft);

        ItemDB.init(this);
        MaterialDB.init(this);

        setupRecipes();
        setupNavigation();

        btnCraft.setOnClickListener(v -> craftItem());
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
        
        StringBuilder sb = new StringBuilder();
        SharedPreferences matPrefs = getSharedPreferences("MaterialInventory", MODE_PRIVATE);
        
        boolean canCraft = true;
        for (Map.Entry<Integer, Integer> entry : recipe.materialsRequired.entrySet()) {
            Material mat = MaterialDB.getMaterial(entry.getKey());
            int owned = matPrefs.getInt("mat_" + entry.getKey(), 0);
            int required = entry.getValue();
            
            sb.append(mat.name).append(": ").append(owned).append("/").append(required).append("\n");
            if (owned < required) canCraft = false;
        }
        
        materialList.setText(sb.toString());
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
