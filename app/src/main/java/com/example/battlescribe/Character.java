package com.example.battlescribe;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Intent;
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

    private Map<SlotType, Item> equippedItems = new HashMap<SlotType, Item>();
    private Map<SlotType, Integer> slotViewIds = new HashMap<SlotType, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        findViewById(R.id.shop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Character.this, ShopActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });
        findViewById(R.id.skills).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SkillsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });


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
                    ImageView slotView = (ImageView) findViewById(slotViewIds.get(type));
                    if (slotView != null) {
                        slotView.setImageBitmap(item.iconBitmap);
                    }
                }
            }
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

                        if (draggedItem != null) {
                            if (draggedItem.slot == targetSlot) {
                                equipItem(draggedItem);
                                return true;
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

    private void equipItem(Item item) {
        equippedItems.put(item.slot, item);

        // Save to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
        prefs.edit().putInt(item.slot.name(), item.id).apply();

        // Update UI
        ImageView slotView = (ImageView) findViewById(slotViewIds.get(item.slot));
        if (slotView != null) {
            slotView.setImageBitmap(item.iconBitmap);
        }

        refreshStatsUI();
    }

    private void refreshStatsUI() {
        SharedPreferences prefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);
        int baseStr = prefs.getInt("str", 10);
        int baseDef = prefs.getInt("def", 10);
        int baseMgc = prefs.getInt("mgc", 10);
        int baseAgi = prefs.getInt("agi", 10);

        // Display only base stats as requested
        ((TextView) findViewById(R.id.tv_STR)).setText(String.valueOf(baseStr));
        ((TextView) findViewById(R.id.tv_DEF)).setText(String.valueOf(baseDef));
        ((TextView) findViewById(R.id.tv_MGC)).setText(String.valueOf(baseMgc));
        ((TextView) findViewById(R.id.tv_AGI)).setText(String.valueOf(baseAgi));
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
        for (int i = 0; i < 24; i++) {
            inventory[i] = null;
        }
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
            ImageView slot = (ImageView) findViewById(slotIds[i]);
            if (slot == null) {
                continue;
            }
            
            int inventoryIndex = startOffset + i;
            final Item item;
            if (inventoryIndex < inventory.length) {
                item = inventory[inventoryIndex];
            } else {
                item = null;
            }

            if (item != null) {
                slot.setImageBitmap(item.iconBitmap);
                slot.setVisibility(View.VISIBLE);
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
                slot.setOnLongClickListener(null);
            }
        }
        TextView pageView = (TextView) findViewById(R.id.INVsitePage);
        if (pageView != null) {
            pageView.setText((currentPage + 1) + "/3");
        }
    }

    public void INVnextPage(View view) {
        if (currentPage < 2) {
            currentPage++;
            updateInventoryUI();
        }
    }
    public void INVprevPage(View view) {
        if (currentPage > 0) {
            currentPage--;
            updateInventoryUI();
        }
    }
}
