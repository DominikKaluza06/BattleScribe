package com.example.battlescribe;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.DragEvent;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private TextView tvStatPoints;
    private TextView tvLevel;
    private TextView tvCharGold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        hideSystemUI();

        tvStatPoints = findViewById(R.id.tv_stat_points);
        tvLevel = findViewById(R.id.tv_level);
        tvCharGold = findViewById(R.id.tv_char_gold);
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
        setupEquipmentDragListeners();
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

    private void setupEquipmentDragListeners() {
        View.OnDragListener dragListener = new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setAlpha(0.5f);
                        return true;
                    case DragEvent.ACTION_DRAG_EXITED:
                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setAlpha(1.0f);
                        return true;
                    case DragEvent.ACTION_DROP:
                        ClipData.Item data = event.getClipData().getItemAt(0);
                        int itemId = Integer.parseInt(data.getText().toString());
                        Item draggedItem = ItemDB.getItem(itemId);

                        SlotType targetSlot = null;
                        for (Map.Entry<SlotType, Integer> entry : slotViewIds.entrySet()) {
                            if (entry.getValue().equals(v.getId())) {
                                targetSlot = entry.getKey();
                                break;
                            }
                        }

                        if (draggedItem != null && targetSlot != null) {
                            if (draggedItem.slot == targetSlot) {
                                equipItem(draggedItem);
                                return true;
                            } else {
                                Toast.makeText(Character.this, "Wrong slot type!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        return false;
                }
                return false;
            }
        };

        for (Integer resId : slotViewIds.values()) {
            View view = findViewById(resId);
            if (view != null) {
                view.setOnDragListener(dragListener);
            }
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
        getSharedPreferences("StoryProgress", MODE_PRIVATE).edit().clear().commit();
        
        Toast.makeText(this, getString(R.string.toast_reset_fresh), Toast.LENGTH_SHORT).show();
        
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
                     "STR: +" + item.strBonus + " | " + "VIT: +" + item.vitBonus + "\n" +
                     "MGC: +" + item.mgcBonus + " | " + "AGI: +" + item.agiBonus;
        selectedItemDesc.setText(desc);

        if (fromEquipSlot) {
            actionButton.setText(R.string.btn_unequip);
            actionButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.RED)); 
        } else {
            actionButton.setText(R.string.btn_equip);
            actionButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.parseColor("#4CAF50"))); 
        }
        actionButton.setEnabled(true);
        actionButton.setAlpha(1.0f);
    }

    private void hideItemInfo() {
        itemInfoPanel.setVisibility(View.GONE);
        selectedItem = null;
    }

    private void loadEquippedItems() {
        SharedPreferences prefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
        equippedItems.clear();
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
        SharedPreferences prefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
        prefs.edit().putInt(item.slot.name(), item.id).apply();
        loadEquippedItems();
        loadInventory(); // Refresh to remove from list
        refreshStatsUI();
    }

    private void unequipItem(Item item) {
        SharedPreferences prefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
        prefs.edit().remove(item.slot.name()).apply();
        loadEquippedItems();
        loadInventory(); // Refresh to add back to list
        refreshStatsUI();
    }

    private void refreshStatsUI() {
        SharedPreferences prefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);
        int baseStr = prefs.getInt("str", 10);
        int baseVit = prefs.getInt("vit", 10);
        int baseMgc = prefs.getInt("mgc", 10);
        int baseAgi = prefs.getInt("agi", 10);
        statPoints = prefs.getInt("statPoints", 0);
        playerLevel = prefs.getInt("level", 1);
        int gold = prefs.getInt("gold", 0);

        ((TextView) findViewById(R.id.tv_STR)).setText(String.valueOf(baseStr));
        ((TextView) findViewById(R.id.tv_VIT)).setText(String.valueOf(baseVit));
        ((TextView) findViewById(R.id.tv_MGC)).setText(String.valueOf(baseMgc));
        ((TextView) findViewById(R.id.tv_AGI)).setText(String.valueOf(baseAgi));
        
        if (tvStatPoints != null) tvStatPoints.setText(getString(R.string.label_stat_points, statPoints));
        if (tvLevel != null) tvLevel.setText(getString(R.string.label_level, playerLevel));
        if (tvCharGold != null) tvCharGold.setText(getString(R.string.label_gold, gold));
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
            Toast.makeText(this, getString(R.string.toast_no_stat_points), Toast.LENGTH_SHORT).show();
        }
    }

    public void increaseStr(View view) { spendStatPoint("str"); }
    public void increaseVit(View view) { spendStatPoint("vit"); }
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
                        inventory[index] = item;
                        index++;
                    }
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
                slot.setAlpha(1.0f);
                slot.setOnClickListener(v -> showItemInfo(item, false));
                
                // Add long click listener for dragging
                slot.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        ClipData.Item clipItem = new ClipData.Item(String.valueOf(item.id));
                        ClipData dragData = new ClipData(item.name, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, clipItem);
                        v.startDragAndDrop(dragData, new View.DragShadowBuilder(v), null, 0);
                        return true;
                    }
                });
            } else {
                slot.setImageBitmap(null);
                slot.setOnClickListener(null);
                slot.setOnLongClickListener(null);
            }
        }
        TextView pageView = findViewById(R.id.INVsitePage);
        if (pageView != null) pageView.setText((currentPage + 1) + "/3");
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
