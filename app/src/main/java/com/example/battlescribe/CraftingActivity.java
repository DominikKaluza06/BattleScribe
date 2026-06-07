package com.example.battlescribe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Activity for crafting items using gathered materials.
 * It displays recipes and their requirements with a paginated grid.
 */
public class CraftingActivity extends AppCompatActivity {

    private final int[] recipeSlotIds = {
            R.id.recipe_slot1, R.id.recipe_slot2, R.id.recipe_slot3, R.id.recipe_slot4,
            R.id.recipe_slot5, R.id.recipe_slot6, R.id.recipe_slot7, R.id.recipe_slot8
    };

    private List<Recipe> allRecipes = new ArrayList<>();
    private List<Recipe> filteredRecipes = new ArrayList<>();
    private int recipePage = 0;
    private String currentFilter = "ALL";

    private View craftingPanel;
    private ImageView selectedRecipeIcon;
    private TextView selectedRecipeName;
    private View tvRequirementsLabel;
    private LinearLayout requirementsContainer;
    private Button btnCraft;

    private View matSourcePanel;
    private TextView tvMatSource;

    private Recipe selectedRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crafting);
        hideSystemUI();

        // Initialize UI components
        craftingPanel = findViewById(R.id.crafting_panel);
        selectedRecipeIcon = findViewById(R.id.selected_recipe_icon);
        selectedRecipeName = findViewById(R.id.selected_recipe_name);
        tvRequirementsLabel = findViewById(R.id.tv_requirements);
        requirementsContainer = findViewById(R.id.requirements_container);
        btnCraft = findViewById(R.id.btn_craft);
        matSourcePanel = findViewById(R.id.mat_source_panel);
        tvMatSource = findViewById(R.id.tv_mat_source);

        // Ensure all databases are initialized
        ItemDB.init(this);
        MaterialDB.init(this);
        RecipeDB.init(this);

        allRecipes = RecipeDB.getAllRecipes();
        
        setupFilters();
        applyFilter("ALL");
        setupNavigation();

        btnCraft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                craftItem();
            }
        });

        // Tooltip dismissal
        craftingPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                matSourcePanel.setVisibility(View.GONE);
            }
        });
    }

    private void setupFilters() {
        findViewById(R.id.filter_all).setOnClickListener(v -> applyFilter("ALL"));
        findViewById(R.id.filter_weapon).setOnClickListener(v -> applyFilter("WEAPON"));
        findViewById(R.id.filter_armor).setOnClickListener(v -> applyFilter("ARMOR"));
        findViewById(R.id.filter_boots).setOnClickListener(v -> applyFilter("BOOTS"));
        findViewById(R.id.filter_helmet).setOnClickListener(v -> applyFilter("HELMET"));
        findViewById(R.id.filter_ring).setOnClickListener(v -> applyFilter("RING"));
        findViewById(R.id.filter_materials).setOnClickListener(v -> applyFilter("BARS"));
    }

    private void applyFilter(String filter) {
        currentFilter = filter;
        filteredRecipes.clear();
        recipePage = 0;

        for (Recipe recipe : allRecipes) {
            if (filter.equals("ALL")) {
                filteredRecipes.add(recipe);
            } else if (filter.equals("BARS")) {
                // If it's a material and not an item, it's a "Bar" in this context
                if (MaterialDB.getMaterial(recipe.resultItemId) != null && ItemDB.getItem(recipe.resultItemId) == null) {
                    filteredRecipes.add(recipe);
                }
            } else {
                // Check by SlotType
                Item item = ItemDB.getItem(recipe.resultItemId);
                if (item != null && item.slot.name().equals(filter)) {
                    filteredRecipes.add(recipe);
                }
            }
        }
        
        // Hide info panel when switching filters to avoid showing a recipe not in the current list
        craftingPanel.setVisibility(View.INVISIBLE);
        updateRecipeUI();
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

    private void updateRecipeUI() {
        int totalPages = (int) Math.ceil(filteredRecipes.size() / 8.0);
        if (totalPages == 0) totalPages = 1;
        
        if (recipePage >= totalPages) recipePage = totalPages - 1;
        
        int startOffset = recipePage * 8;
        for (int i = 0; i < 8; i++) {
            ImageView slot = findViewById(recipeSlotIds[i]);
            int recipeIndex = startOffset + i;
            if (recipeIndex < filteredRecipes.size()) {
                final Recipe recipe = filteredRecipes.get(recipeIndex);
                
                android.graphics.Bitmap icon = null;
                Item item = ItemDB.getItem(recipe.resultItemId);
                if (item != null) icon = item.iconBitmap;
                else {
                    Material mat = MaterialDB.getMaterial(recipe.resultItemId);
                    if (mat != null) icon = mat.iconBitmap;
                }

                if (icon != null) {
                    slot.setImageBitmap(icon);
                    slot.setVisibility(View.VISIBLE);
                    slot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showRecipe(recipe);
                        }
                    });
                }
            } else {
                slot.setVisibility(View.INVISIBLE);
                slot.setOnClickListener(null);
            }
        }

        TextView pageText = findViewById(R.id.recipe_page_text);
        if (pageText != null) {
            pageText.setText((recipePage + 1) + "/" + totalPages);
            pageText.setVisibility(totalPages > 1 ? View.VISIBLE : View.INVISIBLE);
        }

        View nextBtn = findViewById(R.id.recipe_next_page);
        if (nextBtn != null) nextBtn.setVisibility(recipePage < totalPages - 1 ? View.VISIBLE : View.INVISIBLE);

        View prevBtn = findViewById(R.id.recipe_prev_page);
        if (prevBtn != null) prevBtn.setVisibility(recipePage > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    private void showRecipe(Recipe recipe) {
        selectedRecipe = recipe;
        String name = "";
        android.graphics.Bitmap icon = null;
        
        Item item = ItemDB.getItem(recipe.resultItemId);
        if (item != null) {
            name = item.name;
            icon = item.iconBitmap;
        } else {
            Material mat = MaterialDB.getMaterial(recipe.resultItemId);
            if (mat != null) {
                name = mat.name;
                icon = mat.iconBitmap;
            }
        }

        if (icon == null) return;
        
        selectedRecipeName.setText(name);
        selectedRecipeIcon.setImageBitmap(icon);
        requirementsContainer.removeAllViews();
        matSourcePanel.setVisibility(View.GONE);

        SharedPreferences matPrefs = getSharedPreferences("MaterialInventory", MODE_PRIVATE);
        float density = getResources().getDisplayMetrics().density;
        int iconSize = (int) (55 * density); 
        
        boolean canCraft = true;
        for (Map.Entry<Integer, Integer> entry : recipe.materialsRequired.entrySet()) {
            final Material reqMat = MaterialDB.getMaterial(entry.getKey());
            if (reqMat == null) continue;
            
            int owned = matPrefs.getInt("mat_" + entry.getKey(), 0);
            int required = entry.getValue();
            
            LinearLayout matLayout = new LinearLayout(this);
            matLayout.setOrientation(LinearLayout.VERTICAL);
            matLayout.setGravity(Gravity.CENTER);
            matLayout.setPadding((int)(16 * density), 0, (int)(16 * density), 0);

            ImageView iv = new ImageView(this);
            iv.setLayoutParams(new LinearLayout.LayoutParams(iconSize, iconSize));
            iv.setImageBitmap(reqMat.iconBitmap);
            iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
            
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tvMatSource.setText(reqMat.name + "\n" + reqMat.obtainableFrom);
                    matSourcePanel.setVisibility(View.VISIBLE);
                }
            });
            
            TextView tv = new TextView(this);
            tv.setText(owned + "/" + required);
            tv.setTextColor(owned >= required ? Color.parseColor("#3E2723") : Color.RED); 
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(16);
            tv.setShadowLayer(1, 0, 0, Color.WHITE);

            matLayout.addView(iv);
            matLayout.addView(tv);
            requirementsContainer.addView(matLayout);

            if (owned < required) {
                canCraft = false;
            }
        }
        
        btnCraft.setEnabled(canCraft);
        tvRequirementsLabel.setVisibility(View.VISIBLE);
        btnCraft.setVisibility(View.VISIBLE);
        craftingPanel.setVisibility(View.VISIBLE);
    }

    private void craftItem() {
        if (selectedRecipe == null) return;
        
        SharedPreferences matPrefs = getSharedPreferences("MaterialInventory", MODE_PRIVATE);
        
        for (Map.Entry<Integer, Integer> entry : selectedRecipe.materialsRequired.entrySet()) {
            int owned = matPrefs.getInt("mat_" + entry.getKey(), 0);
            if (owned < entry.getValue()) {
                Toast.makeText(this, "Not enough materials!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        SharedPreferences.Editor editor = matPrefs.edit();
        for (Map.Entry<Integer, Integer> entry : selectedRecipe.materialsRequired.entrySet()) {
            int owned = matPrefs.getInt("mat_" + entry.getKey(), 0);
            editor.putInt("mat_" + entry.getKey(), owned - entry.getValue());
        }
        editor.apply();

        if (ItemDB.getItem(selectedRecipe.resultItemId) != null) {
            SharedPreferences itemPrefs = getSharedPreferences("CharacterItems", MODE_PRIVATE);
            itemPrefs.edit().putBoolean("owned_" + selectedRecipe.resultItemId, true).apply();
            Toast.makeText(this, "Crafted " + ItemDB.getItem(selectedRecipe.resultItemId).name + "!", Toast.LENGTH_SHORT).show();
        } else {
            int currentResultOwned = matPrefs.getInt("mat_" + selectedRecipe.resultItemId, 0);
            matPrefs.edit().putInt("mat_" + selectedRecipe.resultItemId, currentResultOwned + 1).apply();
            Toast.makeText(this, "Crafted " + MaterialDB.getMaterial(selectedRecipe.resultItemId).name + "!", Toast.LENGTH_SHORT).show();
        }
        
        showRecipe(selectedRecipe); 
    }

    private void setupNavigation() {
        findViewById(R.id.shop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CraftingActivity.this, ShopActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        
        findViewById(R.id.skills).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CraftingActivity.this, SkillsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        
        findViewById(R.id.adventure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CraftingActivity.this, BattleChoiceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        
        findViewById(R.id.character).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CraftingActivity.this, Character.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    public void RECIPEnextPage(View view) {
        int totalPages = (int) Math.ceil(filteredRecipes.size() / 8.0);
        if (recipePage < totalPages - 1) {
            recipePage++;
            matSourcePanel.setVisibility(View.GONE);
            updateRecipeUI();
        }
    }

    public void RECIPEprevPage(View view) {
        if (recipePage > 0) {
            recipePage--;
            matSourcePanel.setVisibility(View.GONE);
            updateRecipeUI();
        }
    }
}
