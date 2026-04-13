package com.example.battlescribe;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ShopActivity extends AppCompatActivity {

    private Item[] shopItems = new Item[24];
    private int currentPage = 0;
    private final int[] slotIds = {
            R.id.shop_slot1, R.id.shop_slot2, R.id.shop_slot3, R.id.shop_slot4,
            R.id.shop_slot5, R.id.shop_slot6, R.id.shop_slot7, R.id.shop_slot8
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        // Navigation to Character (Reuses existing instance)
        findViewById(R.id.character).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopActivity.this, Character.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
            }
        });

        ItemDB.init(this);
        loadShopItems();
    }

    private void loadShopItems() {
        // Reset the shop array to be empty
        for (int i = 0; i < 24; i++) shopItems[i] = null;
        
        // You can add your own items here manually later, for example:
        // shopItems[0] = ItemDB.getItem(101);
        
        updateShopUI();
    }

    private void updateShopUI() {
        int startOffset = currentPage * 8;
        
        for (int i = 0; i < 8; i++) {
            ImageView slot = findViewById(slotIds[i]);
            int itemIndex = startOffset + i;
            Item item = (itemIndex < shopItems.length) ? shopItems[itemIndex] : null;

            if (item != null) {
                slot.setImageBitmap(item.iconBitmap);
                slot.setVisibility(View.VISIBLE);
            } else {
                slot.setImageBitmap(null);
            }
        }

        TextView pageText = findViewById(R.id.shop_page_text);
        if (pageText != null) {
            pageText.setText((currentPage + 1) + "/3");
        }
    }

    public void buyItem(View view) {
        // Implement buying logic here
    }

    public void SHOPnextPage(View view) {
        if (currentPage < 2) {
            currentPage++;
            updateShopUI();
        }
    }

    public void SHOPprevPage(View view) {
        if (currentPage > 0) {
            currentPage--;
            updateShopUI();
        }
    }
}
