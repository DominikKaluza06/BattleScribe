package com.example.battlescribe;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.view.View;
import android.widget.TextView;

public class Character extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);

        // Load stats
        SharedPreferences prefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);

        // Get saved values (default to 10 if nothing is saved yet)
        ((TextView)findViewById(R.id.STR)).setText(String.valueOf(prefs.getInt("str", 10)));
        ((TextView)findViewById(R.id.DEF)).setText(String.valueOf(prefs.getInt("def", 10)));
        ((TextView)findViewById(R.id.MGC)).setText(String.valueOf(prefs.getInt("mgc", 10)));
        ((TextView)findViewById(R.id.AGI)).setText(String.valueOf(prefs.getInt("agi", 10)));

        ItemDB.init(this); // Najprej pripravi slike
        loadInventory();   // Nato jih naloži v tabelo in prikaži
    }



    private void incrementStat(int textViewId, String statKey) {
        TextView statView = findViewById(textViewId);int currentScore = Integer.parseInt(statView.getText().toString());
        int newScore = currentScore + 1;

        // Update the UI
        statView.setText(String.valueOf(newScore));

        // SAVE to SharedPreferences
        SharedPreferences prefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(statKey, newScore);
        editor.apply(); // This saves the data to the phone's storage
    }

    public void increaseStr(View view) {
        incrementStat(R.id.STR, "str");
    }

    public void increaseDef(View view) {
        incrementStat(R.id.DEF, "def");
    }

    public void increaseMgc(View view) {
        incrementStat(R.id.MGC, "mgc");
    }

    public void increaseAgi(View view) {
        incrementStat(R.id.AGI, "mgc");
    }

    //Items
    private Item[] inventory = new Item[24];
    private int currentPage = 1;

    // A fixed array of your ImageView IDs from the XML
    private final int[] slotIds = {
            R.id.weapon_slot1, R.id.weapon_slot2, R.id.weapon_slot3, R.id.weapon_slot4,
            R.id.weapon_slot5, R.id.weapon_slot6, R.id.weapon_slot7, R.id.weapon_slot8
    };

    private void loadInventory() {
        // 1. Ponastavi tabelo na prazno
        for (int i = 0; i < 24; i++) inventory[i] = null;

        // 2. Dobi podatke o lastništvu (predpostavimo, da hraniš ID-je kupljenih itemov)
        SharedPreferences prefs = getSharedPreferences("CharacterItems", MODE_PRIVATE);

        int inventoryIndex = 0;

        // 3. Gremo skozi vse predmete v bazi
        // Opomba: V ItemDB moraš imeti metodo, ki vrne seznam vseh predmetov (npr. ItemDB.getAllItems())
        for (Item item : ItemDB.getAllItems()) {
            // Preverimo, če ima igralec ta predmet (npr. v SharedPreferences imaš shranjeno "owned_101" = true)
            boolean isOwned = prefs.getBoolean("owned_" + item.id, false);

            if (isOwned && inventoryIndex < 24) {
                inventory[inventoryIndex] = item;
                inventoryIndex++;
            }
        }

        // 4. Osveži slike na zaslonu
        updateInventoryUI();
    }

    public void nextPage(View view) {
        if (currentPage < 2) {
            currentPage++;
            updateInventoryUI();
        }
    }

    public void prevPage(View view) {
        if (currentPage > 0) {
            currentPage--;
            updateInventoryUI();
        }
    }
    private void updateInventoryUI() {
        int startOffset = currentPage * 8; // Začne pri 0, 8 ali 16

        for (int i = 0; i < 8; i++) {
            // Use the array to find the View instead of getIdentifier
            android.widget.ImageView slot = findViewById(slotIds[i]);

            // Safety check to ensure we don't go out of bounds of the inventory array
            int inventoryIndex = startOffset + i;
            Item item;
            if (inventoryIndex < inventory.length) {
                item = inventory[inventoryIndex];
            } else {
                item = null;
            }


            if (item != null) {
                slot.setImageBitmap(item.iconBitmap);
                slot.setVisibility(View.VISIBLE);
            } else {
                // Če je slot prazen, lahko skrijemo sliko ali damo prazno ikono
                slot.setImageBitmap(null);

            }
        }
        TextView pageText = findViewById(R.id.INVsitePage);
        pageText.setText((currentPage + 1) + "/3");
    }
}