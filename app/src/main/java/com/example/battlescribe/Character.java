package com.example.battlescribe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Map;

public class Character extends AppCompatActivity {

    private Map<SlotType, Item> equippedItems = new HashMap<>();
    private Map<SlotType, Integer> slotViewIds = new HashMap<>();
    
    private View itemInfoPanel;
    private ImageView selectedItemIcon;
    private TextView selectedItemName;
    private TextView selectedItemDesc;
    private Button actionButton;
    private Item selectedItem = null;
    private boolean selectedFromEquipSlot = false;

    private int statPoints = 0;
    private int playerLevel = 1;
    private int playerGold = 0;
    private TextView tvStatPoints;
    private TextView tvLevel;
    private TextView tvGold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        hideSystemUI();

        tvStatPoints = findViewById(R.id.tv_stat_points);
        tvLevel = findViewById(R.id.tv_level);
        tvGold = findViewById(R.id.tv_char_gold);
        itemInfoPanel = findViewById(R.id.char_item_info_panel);
        selectedItemIcon = findViewById(R.id.selected_char_item_icon);
        selectedItemName = findViewById(R.id.selected_char_item_name);
        selectedItemDesc = findViewById(R.id.selected_char_item_desc);
        actionButton = findViewById(R.id.char_item_action_button);
        
        findViewById(R.id.char_item_close_button).setOnClickListener(v -> hideItemInfo());
        
        actionButton.setOnClickListener(v -> {
            if (selectedItem != null) {
                if (selectedFromEquipSlot) {
                    unequipItem(selectedItem);
                } else {
                    equipItem(selectedItem);
                }
                hideItemInfo();
            }
        });

        setupNavigation();

        slotViewIds.put(SlotType.WEAPON, R.id.weapon_slot);
        slotViewIds.put(SlotType.HELMET, R.id.helmet_slot);
        slotViewIds.put(SlotType.ARMOR, R.id.armor_slot);
        slotViewIds.put(SlotType.BOOTS, R.id.boots_slot);
        slotViewIds.put(SlotType.RING, R.id.ring_slot);

        ItemDB.init(this);
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
        findViewById(R.id.shop).setOnClickListener(v -> {
            Intent intent = new Intent(Character.this, ShopActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
        
        findViewById(R.id.skills).setOnClickListener(v -> {
            Intent intent = new Intent(Character.this, SkillsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });

        View adventureBtn = findViewById(R.id.adventure);
        if (adventureBtn != null) {
            adventureBtn.setOnClickListener(v -> {
                Intent intent = new Intent(Character.this, BattleChoiceActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            });
        }

        findViewById(R.id.crafting).setOnClickListener(v -> {
            Intent intent = new Intent(Character.this, CraftingActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEquippedItems();
        loadInventory();
        refreshStatsUI();
        hideItemInfo();
    }

    public void resetGame(View view) {
        getSharedPreferences("CharacterStats", MODE_PRIVATE).edit().clear().commit();
        getSharedPreferences("CharacterItems", MODE_PRIVATE).edit().clear().commit();
        getSharedPreferences("EquippedItems", MODE_PRIVATE).edit().clear().commit();
        getSharedPreferences("CharacterSkills", MODE_PRIVATE).edit().clear().commit();
        getSharedPreferences("BattleProgress", MODE_PRIVATE).edit().clear().commit();
        getSharedPreferences("MaterialInventory", MODE_PRIVATE).edit().clear().commit();
        
        Toast.makeText(this, "Save Deleted. Starting Fresh!", Toast.LENGTH_SHORT).show();
        
        loadEquippedItems();
        loadInventory();
        refreshStatsUI();
        hideItemInfo();
    }

    private void showItemInfo(Item item, boolean fromEquipSlot) {
        selectedItem = item;
        selectedFromEquipSlot = fromEquipSlot;
        itemInfoPanel.setVisibility(View.VISIBLE);
        selectedItemIcon.setImageBitmap(item.iconBitmap);
        selectedItemName.setText(item.name);
        
        String desc = "Slot: " + item.slot.name() + "\n" +
                     "STR: +" + item.strBonus + " | " + "DEF: +" + item.defBonus + "\n" +
                     "MGC: +" + item.mgcBonus + " | " + "AGI: +" + item.agiBonus;
        selectedItemDesc.setText(desc);

        if (fromEquipSlot) {
            actionButton.setText("UNEQUIP");
            actionButton.setEnabled(true);
            actionButton.setAlpha(1.0f);
            actionButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFF4444)); 
        } else {
            actionButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50)); 
            SharedPreferences prefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
            if (prefs.getInt(item.slot.name(), -1) == item.id) {
                actionButton.setText("EQUIPPED");
                actionButton.setEnabled(false);
                actionButton.setAlpha(0.5f);
            } else {
                actionButton.setText("EQUIP");
                actionButton.setEnabled(true);
                actionButton.setAlpha(1.0f);
            }
        }
    }

    private void hideItemInfo() {
        itemInfoPanel.setVisibility(View.GONE);
        selectedItem = null;
    }

    private void loadEquippedItems() {
        SharedPreferences prefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
        for (SlotType type : SlotType.values()) {
            int itemId = prefs.getInt(type.name(), -1);
            ImageView slotView = findViewById(slotViewIds.get(type));
            
            if (itemId != -1) {
                Item item = ItemDB.getItem(itemId);
                if (item != null) {
                    equippedItems.put(type, item);
                    if (slotView != null) {
                        slotView.setImageBitmap(item.iconBitmap);
                        slotView.setOnClickListener(v -> showItemInfo(item, true));
                    }
                } else {
                    if (slotView != null) {
                        slotView.setImageBitmap(null);
                        slotView.setOnClickListener(null);
                    }
                }
            } else {
                if (slotView != null) {
                    slotView.setImageBitmap(null);
                    slotView.setOnClickListener(null);
                }
            }
        }
    }

    private void equipItem(Item item) {
        equippedItems.put(item.slot, item);
        SharedPreferences prefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
        prefs.edit().putInt(item.slot.name(), item.id).apply();
        loadEquippedItems();
        refreshStatsUI();
    }

    private void unequipItem(Item item) {
        equippedItems.remove(item.slot);
        SharedPreferences prefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
        prefs.edit().remove(item.slot.name()).apply();
        loadEquippedItems();
        refreshStatsUI();
    }

    private void refreshStatsUI() {
        SharedPreferences prefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);
        int baseStr = prefs.getInt("str", 10);
        int baseDef = prefs.getInt("def", 10);
        int baseMgc = prefs.getInt("mgc", 10);
        int baseAgi = prefs.getInt("agi", 10);
        statPoints = prefs.getInt("statPoints", 0);
        playerLevel = prefs.getInt("level", 1);
        playerGold = prefs.getInt("gold", 0);

        ((TextView) findViewById(R.id.tv_STR)).setText(String.valueOf(baseStr));
        ((TextView) findViewById(R.id.tv_DEF)).setText(String.valueOf(baseDef));
        ((TextView) findViewById(R.id.tv_MGC)).setText(String.valueOf(baseMgc));
        ((TextView) findViewById(R.id.tv_AGI)).setText(String.valueOf(baseAgi));
        
        if (tvStatPoints != null) tvStatPoints.setText("Stat Points: " + statPoints);
        if (tvLevel != null) tvLevel.setText("Level: " + playerLevel);
        if (tvGold != null) tvGold.setText("Gold: " + playerGold);
    }

    private void spendStatPoint(String statKey) {
        SharedPreferences prefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);
        int currentPoints = prefs.getInt("statPoints", 0);
        
        if (currentPoints > 0) {
            int currentStat = prefs.getInt(statKey, 10);
            prefs.edit()
                .putInt(statKey, currentStat + 1)
                .putInt("statPoints", currentPoints - 1)
                .apply();
            refreshStatsUI();
        } else {
            Toast.makeText(this, "No stat points available!", Toast.LENGTH_SHORT).show();
        }
    }

    public void increaseStr(View view) { spendStatPoint("str"); }
    public void increaseDef(View view) { spendStatPoint("def"); }
    public void increaseMgc(View view) { spendStatPoint("mgc"); }
    public void increaseAgi(View view) { spendStatPoint("agi"); }

    private Item[] inventory = new Item[24];
    private int currentPage = 0;
    private final int[] slotIds = {
            R.id.weapon_slot1, R.id.weapon_slot2, R.id.weapon_slot3, R.id.weapon_slot4,
            R.id.weapon_slot5, R.id.weapon_slot6, R.id.weapon_slot7, R.id.weapon_slot8
    };

    private void loadInventory() {
        for (int i = 0; i < 24; i++) inventory[i] = null;
        SharedPreferences prefs = getSharedPreferences("CharacterItems", MODE_PRIVATE);
        int index = 0;
        for (Item item : ItemDB.getAllItems()) {
            if (prefs.getBoolean("owned_" + item.id, false)) {
                if (index < 24) {
                    inventory[index] = item;
                    index++;
                }
            }
        }
        updateInventoryUI();
    }

    private void updateInventoryUI() {
        int startOffset = currentPage * 8;
        for (int i = 0; i < 8; i++) {
            ImageView slot = findViewById(slotIds[i]);
            if (slot == null) continue;
            int inventoryIndex = startOffset + i;
            final Item item = (inventoryIndex < inventory.length) ? inventory[inventoryIndex] : null;
            if (item != null) {
                slot.setImageBitmap(item.iconBitmap);
                slot.setVisibility(View.VISIBLE);
                slot.setAlpha(isEquipped(item) ? 0.5f : 1.0f);
                slot.setOnClickListener(v -> showItemInfo(item, false));
            } else {
                slot.setImageBitmap(null);
                slot.setOnClickListener(null);
            }
        }
        TextView pageView = findViewById(R.id.INVsitePage);
        if (pageView != null) pageView.setText((currentPage + 1) + "/3");
    }

    private boolean isEquipped(Item item) {
        SharedPreferences prefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
        return prefs.getInt(item.slot.name(), -1) == item.id;
    }

    public void INVnextPage(View view) {
        if (currentPage < 2) {
            currentPage++;
            hideItemInfo();
            updateInventoryUI();
        }
    }
    public void INVprevPage(View view) {
        if (currentPage > 0) {
            currentPage--;
            hideItemInfo();
            updateInventoryUI();
        }
    }
}
