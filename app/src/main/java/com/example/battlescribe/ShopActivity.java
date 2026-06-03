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
import java.util.HashSet;
import java.util.Set;

/**
 * Activity for the in-game Shop.
 * Players can purchase equipment and sell items they own.
 * Paging logic is split between Shop inventory and Player inventory.
 */
public class ShopActivity extends AppCompatActivity {

    private Item[] shopItems = new Item[24];
    private Item[] inventoryItems = new Item[24];
    
    private int shopPage = 0;
    private int invPage = 0;
    
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
        loadPlayerData();
        loadShopItems();
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
        findViewById(R.id.character).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopActivity.this, Character.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        
        findViewById(R.id.skills).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopActivity.this, SkillsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        findViewById(R.id.adventure).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopActivity.this, BattleChoiceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        findViewById(R.id.crafting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopActivity.this, CraftingActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
    }

    private void loadPlayerData() {
        SharedPreferences statsPrefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);
        playerGold = statsPrefs.getInt("gold", 0);
        updateGoldUI();
    }

    private void updateGoldUI() {
        if (tvGold != null) {
            tvGold.setText(getString(R.string.label_gold, playerGold));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPlayerData();
        loadShopItems();
        loadInventory();
        itemInfoPanel.setVisibility(View.INVISIBLE);
        selectedItem = null;
    }

    private void loadShopItems() {
        for (int i = 0; i < 24; i++) shopItems[i] = null;
        SharedPreferences storyPrefs = getSharedPreferences("StoryProgress", MODE_PRIVATE);
        int currentChapter = storyPrefs.getInt("chapter", 1);
        int index = 0;
        for (Item item : ItemDB.getAllItems()) {
            if (item.requiredChapter <= currentChapter) {
                if (index < 24) {
                    shopItems[index] = item;
                    index++;
                }
            }
        }
        updateShopUI();
    }

    private void loadInventory() {
        for (int i = 0; i < 24; i++) inventoryItems[i] = null;
        SharedPreferences itemsPrefs = getSharedPreferences("CharacterItems", MODE_PRIVATE);
        SharedPreferences equipPrefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
        
        Set<Integer> equippedIds = new HashSet<>();
        for (SlotType type : SlotType.values()) {
            int id = equipPrefs.getInt(type.name(), -1);
            if (id != -1) equippedIds.add(id);
        }

        int index = 0;
        for (Item item : ItemDB.getAllItems()) {
            if (itemsPrefs.getBoolean("owned_" + item.id, false)) {
                if (!equippedIds.contains(item.id)) {
                    if (index < 24) {
                        inventoryItems[index] = item;
                        index++;
                    }
                }
            }
        }
        updateInventoryUI();
    }

    private void updateShopUI() {
        int startOffset = shopPage * 8;
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
        if (pageText != null) pageText.setText((shopPage + 1) + "/3");
    }

    private void updateInventoryUI() {
        int startOffset = invPage * 8;
        for (int i = 0; i < 8; i++) {
            ImageView slot = findViewById(sellSlotIds[i]);
            int itemIndex = startOffset + i;
            final Item item = (itemIndex < inventoryItems.length) ? inventoryItems[itemIndex] : null;
            if (item != null) {
                slot.setImageBitmap(item.iconBitmap);
                slot.setAlpha(1.0f);
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
        TextView pageText = findViewById(R.id.inv_page_text);
        if (pageText != null) pageText.setText((invPage + 1) + "/3");
    }

    private void showItemDescription(final Item item, final boolean isSellMode) {
        selectedItem = item;
        this.isSelling = isSellMode;
        itemInfoPanel.setVisibility(View.VISIBLE);
        selectedItemIcon.setImageBitmap(item.iconBitmap);
        selectedItemName.setText(item.name);
        
        int displayPrice = isSellMode ? item.price / 2 : item.price;
        
        StringBuilder desc = new StringBuilder();
        desc.append("Price: ").append(displayPrice).append(" Gold\n");
        desc.append(formatStat("STR", item.strBonus)).append(" | ");
        desc.append(formatStat("VIT", item.vitBonus)).append("\n");
        desc.append(formatStat("MGC", item.mgcBonus)).append(" | ");
        desc.append(formatStat("AGI", item.agiBonus));
        
        selectedItemDesc.setText(desc.toString());
        
        if (isSellMode) {
            actionButton.setText(getString(R.string.btn_sell) + " (" + displayPrice + ")");
            actionButton.setEnabled(true);
        } else {
            actionButton.setText(getString(R.string.btn_buy) + " (" + displayPrice + ")");
            actionButton.setEnabled(playerGold >= item.price);
        }
        actionButton.setVisibility(View.VISIBLE);
        findViewById(R.id.close_button).setVisibility(View.VISIBLE);
    }

    private String formatStat(String label, int value) {
        if (value > 0) return label + ": +" + value;
        if (value < 0) return label + ": " + value;
        return label + ": 0";
    }

    public void buyItem() {
        if (selectedItem == null) return;
        
        if (playerGold < selectedItem.price) {
            Toast.makeText(this, "Not enough gold!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        SharedPreferences itemPrefs = getSharedPreferences("CharacterItems", MODE_PRIVATE);
        if (itemPrefs.getBoolean("owned_" + selectedItem.id, false)) {
            Toast.makeText(this, getString(R.string.toast_already_owned), Toast.LENGTH_SHORT).show();
            return;
        }
        
        playerGold -= selectedItem.price;
        saveGold();
        itemPrefs.edit().putBoolean("owned_" + selectedItem.id, true).commit();
        
        Toast.makeText(this, getString(R.string.toast_bought, selectedItem.name), Toast.LENGTH_SHORT).show();
        updateGoldUI();
        loadInventory();
        closeInfoPanel(null);
    }

    public void sellItem() {
        if (selectedItem == null) return;
        
        playerGold += selectedItem.price / 2;
        saveGold();
        
        SharedPreferences prefs = getSharedPreferences("CharacterItems", MODE_PRIVATE);
        prefs.edit().remove("owned_" + selectedItem.id).commit();
        
        Toast.makeText(this, getString(R.string.toast_sold, selectedItem.name), Toast.LENGTH_SHORT).show();
        updateGoldUI();
        loadInventory();
        closeInfoPanel(null);
    }

    private void saveGold() {
        getSharedPreferences("CharacterStats", MODE_PRIVATE).edit().putInt("gold", playerGold).apply();
    }

    public void closeInfoPanel(View view) {
        if (itemInfoPanel != null) {
            itemInfoPanel.setVisibility(View.INVISIBLE);
            selectedItem = null;
        }
    }

    public void SHOPnextPage(View view) {
        if (shopPage < 2) {
            shopPage++;
            closeInfoPanel(null);
            updateShopUI();
        }
    }

    public void SHOPprevPage(View view) {
        if (shopPage > 0) {
            shopPage--;
            closeInfoPanel(null);
            updateShopUI();
        }
    }

    public void INVnextPage(View view) {
        if (invPage < 2) {
            invPage++;
            closeInfoPanel(null);
            updateInventoryUI();
        }
    }

    public void INVprevPage(View view) {
        if (invPage > 0) {
            invPage--;
            closeInfoPanel(null);
            updateInventoryUI();
        }
    }
}
