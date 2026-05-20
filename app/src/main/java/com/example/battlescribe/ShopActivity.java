package com.example.battlescribe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ShopActivity extends AppCompatActivity {

    private Item[] shopItems = new Item[24];
    private Item[] inventoryItems = new Item[24];
    private int currentPage = 0;
    private Item selectedItem = null;
    private boolean isSelling = false;
    private int playerGold = 0;
    
    private TextView tvGold;

    private final int[] shopSlotIds = {
            R.id.shop_slot1, R.id.shop_slot2, R.id.shop_slot3, R.id.shop_slot4,
            R.id.shop_slot5, R.id.shop_slot6, R.id.shop_slot7, R.id.shop_slot8
    };

    private final int[] sellSlotIds = {
            R.id.sell_slot1, R.id.sell_slot2, R.id.sell_slot3, R.id.sell_slot4,
            R.id.sell_slot5, R.id.sell_slot6, R.id.sell_slot7, R.id.sell_slot8
    };

    private View itemInfoPanel;
    private ImageView selectedItemIcon;
    private TextView selectedItemName;
    private TextView selectedItemDesc;
    private Button actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        hideSystemUI();

        tvGold = findViewById(R.id.tv_gold);
        itemInfoPanel = findViewById(R.id.item_info_panel);
        selectedItemIcon = findViewById(R.id.selected_item_icon);
        selectedItemName = findViewById(R.id.selected_item_name);
        selectedItemDesc = findViewById(R.id.selected_item_desc);
        actionButton = findViewById(R.id.action_button);

        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSelling) {
                    sellItem();
                } else {
                    buyItem();
                }
            }
        });

        setupNavigation();

        ItemDB.init(this);
        loadShopItems();
        loadPlayerData();
        loadInventory();
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

    private void setupNavigation() {
        findViewById(R.id.character).setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, Character.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        
        findViewById(R.id.skills).setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, SkillsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        findViewById(R.id.adventure).setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, BattleChoiceActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        findViewById(R.id.crafting).setOnClickListener(v -> {
            Intent intent = new Intent(ShopActivity.this, CraftingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }

    private void loadPlayerData() {
        SharedPreferences statsPrefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);
        playerGold = statsPrefs.getInt("gold", 0);
        updateGoldUI();
    }

    private void updateGoldUI() {
        if (tvGold != null) {
            tvGold.setText("Gold: " + playerGold);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlayerData();
        loadInventory();
        itemInfoPanel.setVisibility(View.INVISIBLE);
        selectedItem = null;
    }

    private void loadShopItems() {
        for (int i = 0; i < 24; i++) shopItems[i] = null;
        shopItems[0] = ItemDB.getItem(101);
        shopItems[1] = ItemDB.getItem(102);
        shopItems[2] = ItemDB.getItem(103);
        updateShopUI();
    }

    private void loadInventory() {
        for (int i = 0; i < 24; i++) inventoryItems[i] = null;
        SharedPreferences prefs = getSharedPreferences("CharacterItems", MODE_PRIVATE);
        int index = 0;
        for (Item item : ItemDB.getAllItems()) {
            if (prefs.getBoolean("owned_" + item.id, false)) {
                if (index < 24) {
                    inventoryItems[index] = item;
                    index++;
                }
            }
        }
        updateInventoryUI();
    }

    private void updateShopUI() {
        int startOffset = currentPage * 8;
        for (int i = 0; i < 8; i++) {
            ImageView slot = findViewById(shopSlotIds[i]);
            int itemIndex = startOffset + i;
            final Item item = (itemIndex < shopItems.length) ? shopItems[itemIndex] : null;

            if (item != null) {
                slot.setImageBitmap(item.iconBitmap);
                slot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showItemDescription(item, false);
                    }
                });
            } else {
                slot.setImageBitmap(null);
                slot.setOnClickListener(null);
            }
        }
        TextView pageText = findViewById(R.id.shop_page_text);
        if (pageText != null) pageText.setText((currentPage + 1) + "/3");
    }

    private void updateInventoryUI() {
        int startOffset = currentPage * 8;
        for (int i = 0; i < 8; i++) {
            ImageView slot = findViewById(sellSlotIds[i]);
            int itemIndex = startOffset + i;
            final Item item = (itemIndex < inventoryItems.length) ? inventoryItems[itemIndex] : null;

            if (item != null) {
                slot.setImageBitmap(item.iconBitmap);
                slot.setAlpha(isEquipped(item) ? 0.5f : 1.0f);
                slot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showItemDescription(item, true);
                    }
                });
            } else {
                slot.setImageBitmap(null);
                slot.setOnClickListener(null);
            }
        }
    }

    private void showItemDescription(Item item, boolean isSellMode) {
        selectedItem = item;
        this.isSelling = isSellMode;
        
        itemInfoPanel.setVisibility(View.VISIBLE);
        selectedItemIcon.setImageBitmap(item.iconBitmap);
        selectedItemName.setText(item.name);
        
        String desc = "Price: " + (isSellMode ? item.price / 2 : item.price) + " Gold\n" +
                     "STR: +" + item.strBonus + " | " + "DEF: +" + item.defBonus + "\n" +
                     "MGC: +" + item.mgcBonus + " | " + "AGI: +" + item.agiBonus;
        selectedItemDesc.setText(desc);
        
        if (isSellMode) {
            if (isEquipped(item)) {
                actionButton.setText("EQUIPPED");
                actionButton.setEnabled(false);
            } else {
                actionButton.setText("SELL (" + (item.price / 2) + ")");
                actionButton.setEnabled(true);
            }
        } else {
            actionButton.setText("BUY (" + item.price + ")");
            actionButton.setEnabled(playerGold >= item.price);
        }
    }

    private boolean isEquipped(Item item) {
        SharedPreferences equipPrefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
        return equipPrefs.getInt(item.slot.name(), -1) == item.id;
    }

    public void buyItem() {
        if (selectedItem == null) return;
        if (playerGold < selectedItem.price) {
            Toast.makeText(this, "Not enough gold!", Toast.LENGTH_SHORT).show();
            return;
        }
        SharedPreferences itemPrefs = getSharedPreferences("CharacterItems", MODE_PRIVATE);
        if (itemPrefs.getBoolean("owned_" + selectedItem.id, false)) {
            Toast.makeText(this, "You already own this!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        playerGold -= selectedItem.price;
        saveGold();
        itemPrefs.edit().putBoolean("owned_" + selectedItem.id, true).commit();
        
        Toast.makeText(this, "Bought " + selectedItem.name + "!", Toast.LENGTH_SHORT).show();
        updateGoldUI();
        loadInventory();
        itemInfoPanel.setVisibility(View.INVISIBLE);
        selectedItem = null;
    }

    public void sellItem() {
        if (selectedItem == null) return;
        if (isEquipped(selectedItem)) {
            Toast.makeText(this, "Cannot sell equipped item!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        playerGold += selectedItem.price / 2;
        saveGold();
        
        SharedPreferences prefs = getSharedPreferences("CharacterItems", MODE_PRIVATE);
        prefs.edit().remove("owned_" + selectedItem.id).commit();
        
        Toast.makeText(this, "Sold " + selectedItem.name + "!", Toast.LENGTH_SHORT).show();
        updateGoldUI();
        loadInventory();
        itemInfoPanel.setVisibility(View.INVISIBLE);
        selectedItem = null;
    }

    private void saveGold() {
        getSharedPreferences("CharacterStats", MODE_PRIVATE).edit().putInt("gold", playerGold).apply();
    }

    public void SHOPnextPage(View view) {
        if (currentPage < 2) {
            currentPage++;
            itemInfoPanel.setVisibility(View.INVISIBLE);
            updateShopUI();
            updateInventoryUI();
        }
    }

    public void SHOPprevPage(View view) {
        if (currentPage > 0) {
            currentPage--;
            itemInfoPanel.setVisibility(View.INVISIBLE);
            updateShopUI();
            updateInventoryUI();
        }
    }
}
