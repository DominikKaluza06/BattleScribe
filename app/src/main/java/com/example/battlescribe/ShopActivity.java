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

public class ShopActivity extends AppCompatActivity {

    private Item[] shopItems = new Item[24];
    private int currentPage = 0;
    private Item selectedItem = null;
    
    private final int[] slotIds = {
            R.id.shop_slot1, R.id.shop_slot2, R.id.shop_slot3, R.id.shop_slot4,
            R.id.shop_slot5, R.id.shop_slot6, R.id.shop_slot7, R.id.shop_slot8
    };

    private View itemInfoPanel;
    private ImageView selectedItemIcon;
    private TextView selectedItemName;
    private TextView selectedItemDesc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Initialize UI components for the description panel
        itemInfoPanel = findViewById(R.id.item_info_panel);
        selectedItemIcon = findViewById(R.id.selected_item_icon);
        selectedItemName = findViewById(R.id.selected_item_name);
        selectedItemDesc = findViewById(R.id.selected_item_desc);

        // Navigation listeners
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

        ItemDB.init(this);
        loadShopItems();
    }

    private void loadShopItems() {
        // Clear shop
        for (int i = 0; i < 24; i++) shopItems[i] = null;
        
        // Add manual items here for testing
        shopItems[0] = ItemDB.getItem(101); // Iron Sword
        shopItems[1] = ItemDB.getItem(102); // Steel Sword
        
        updateShopUI();
    }

    private void updateShopUI() {
        int startOffset = currentPage * 8;
        
        for (int i = 0; i < 8; i++) {
            ImageView slot = findViewById(slotIds[i]);
            int itemIndex = startOffset + i;
            final Item item = (itemIndex < shopItems.length) ? shopItems[itemIndex] : null;

            if (item != null) {
                slot.setImageBitmap(item.iconBitmap);
                slot.setVisibility(View.VISIBLE);
                slot.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showItemDescription(item);
                    }
                });
            } else {
                slot.setImageBitmap(null);
                slot.setOnClickListener(null);
            }
        }

        TextView pageText = findViewById(R.id.shop_page_text);
        if (pageText != null) {
            pageText.setText((currentPage + 1) + "/3");
        }
    }

    private void showItemDescription(Item item) {
        selectedItem = item;
        itemInfoPanel.setVisibility(View.VISIBLE);
        selectedItemIcon.setImageBitmap(item.iconBitmap);
        selectedItemName.setText(item.name);
        
        String desc = "Slot: " + item.slot.name() + "\n" +
                     "STR: +" + item.strBonus + " | " +
                     "DEF: +" + item.defBonus + " | " +
                     "MGC: +" + item.mgcBonus + " | " +
                     "AGI: +" + item.agiBonus;
        selectedItemDesc.setText(desc);
    }

    public void buyItem(View view) {
        if (selectedItem == null) return;

        SharedPreferences prefs = getSharedPreferences("CharacterItems", MODE_PRIVATE);
        
        // Mark item as owned
        prefs.edit().putBoolean("owned_" + selectedItem.id, true).apply();
        
        Toast.makeText(this, "Bought " + selectedItem.name + "!", Toast.LENGTH_SHORT).show();
        
        // Optional: Hide panel after buying
        itemInfoPanel.setVisibility(View.INVISIBLE);
        selectedItem = null;
    }

    public void SHOPnextPage(View view) {
        if (currentPage < 2) {
            currentPage++;
            itemInfoPanel.setVisibility(View.INVISIBLE);
            updateShopUI();
        }
    }

    public void SHOPprevPage(View view) {
        if (currentPage > 0) {
            currentPage--;
            itemInfoPanel.setVisibility(View.INVISIBLE);
            updateShopUI();
        }
    }
}
