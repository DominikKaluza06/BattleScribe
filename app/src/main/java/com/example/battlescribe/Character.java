package com.example.battlescribe;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.HashMap;
import java.util.Map;

public class Character extends AppCompatActivity {

    private Map<SlotType, Item> equippedItems = new HashMap<>();
    private Map<SlotType, Integer> slotViewIds = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        // Map SlotTypes to their ImageView IDs
        slotViewIds.put(SlotType.WEAPON, R.id.weapon_slot);
        slotViewIds.put(SlotType.HELMET, R.id.helmet_slot);
        slotViewIds.put(SlotType.ARMOR, R.id.armor_slot);
        slotViewIds.put(SlotType.BOOTS, R.id.boots_slot);
        slotViewIds.put(SlotType.RING, R.id.ring_slot);

        ItemDB.init(this);
        loadEquippedItems();
        loadInventory();
        setupEquipmentDragListeners();
        refreshStatsUI();
    }

    private void loadEquippedItems() {
        SharedPreferences prefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
        for (SlotType type : SlotType.values()) {
            int itemId = prefs.getInt(type.name(), -1);
            if (itemId != -1) {
                Item item = ItemDB.getItem(itemId);
                if (item != null) {
                    equippedItems.put(type, item);
                    ImageView slotView = findViewById(slotViewIds.get(type));
                    slotView.setImageBitmap(item.iconBitmap);
                }
            }
        }
    }

    private void setupEquipmentDragListeners() {
        View.OnDragListener dragListener = (v, event) -> {
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
                        if (entry.getValue() == v.getId()) {
                            targetSlot = entry.getKey();
                            break;
                        }
                    }

                    if (draggedItem != null && draggedItem.slot == targetSlot) {
                        equipItem(draggedItem);
                        return true;
                    }
                    return false;
            }
            return false;
        };

        for (int resId : slotViewIds.values()) {
            findViewById(resId).setOnDragListener(dragListener);
        }
    }

    private void equipItem(Item item) {
        equippedItems.put(item.slot, item);

        // Save to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
        prefs.edit().putInt(item.slot.name(), item.id).apply();

        // Update UI
        ImageView slotView = findViewById(slotViewIds.get(item.slot));
        slotView.setImageBitmap(item.iconBitmap);

        refreshStatsUI();
    }

    private void refreshStatsUI() {
        SharedPreferences prefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);
        int baseStr = prefs.getInt("str", 10);
        int baseDef = prefs.getInt("def", 10);
        int baseMgc = prefs.getInt("mgc", 10);
        int baseAgi = prefs.getInt("agi", 10);

        int bonusStr = 0, bonusDef = 0, bonusMgc = 0, bonusAgi = 0;
        for (Item item : equippedItems.values()) {
            bonusStr += item.strBonus;
            bonusDef += item.defBonus;
            bonusMgc += item.mgcBonus;
            bonusAgi += item.agiBonus;
        }

        ((TextView) findViewById(R.id.tv_STR)).setText(String.valueOf(baseStr + bonusStr));
        ((TextView) findViewById(R.id.tv_DEF)).setText(String.valueOf(baseDef + bonusDef));
        ((TextView) findViewById(R.id.tv_MGC)).setText(String.valueOf(baseMgc + bonusMgc));
        ((TextView) findViewById(R.id.tv_AGI)).setText(String.valueOf(baseAgi + bonusAgi));
    }

    private void incrementStat(String statKey) {
        SharedPreferences prefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);
        int currentBase = prefs.getInt(statKey, 10);
        prefs.edit().putInt(statKey, currentBase + 1).apply();
        refreshStatsUI();
    }

    public void increaseStr(View view) { incrementStat("str"); }
    public void increaseDef(View view) { incrementStat("def"); }
    public void increaseMgc(View view) { incrementStat("mgc"); }
    public void increaseAgi(View view) { incrementStat("agi"); }

    // Inventory logic
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
            if (prefs.getBoolean("owned_" + item.id, false) && index < 24) {
                inventory[index++] = item;
            }
        }
        updateInventoryUI();
    }

    private void updateInventoryUI() {
        int startOffset = currentPage * 8;
        for (int i = 0; i < 8; i++) {
            ImageView slot = findViewById(slotIds[i]);
            int inventoryIndex = startOffset + i;
            Item item = (inventoryIndex < inventory.length) ? inventory[inventoryIndex] : null;

            if (item != null) {
                slot.setImageBitmap(item.iconBitmap);
                slot.setVisibility(View.VISIBLE);
                slot.setOnLongClickListener(v -> {
                    ClipData.Item clipItem = new ClipData.Item(String.valueOf(item.id));
                    ClipData dragData = new ClipData(item.name, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, clipItem);
                    v.startDragAndDrop(dragData, new View.DragShadowBuilder(v), null, 0);
                    return true;
                });
            } else {
                slot.setImageBitmap(null);
                slot.setOnLongClickListener(null);
            }
        }
        ((TextView) findViewById(R.id.INVsitePage)).setText((currentPage + 1) + "/3");
    }

    public void INVnextPage(View view) { if (currentPage < 2) { currentPage++; updateInventoryUI(); } }
    public void INVprevPage(View view) { if (currentPage > 0) { currentPage--; updateInventoryUI(); } }
}