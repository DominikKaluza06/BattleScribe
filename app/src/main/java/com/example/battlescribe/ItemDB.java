package com.example.battlescribe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.HashMap;
import java.util.Map;

/**
 * Item Database class that manages all available items in the game.
 * It initializes items with their respective stats, icons, and costs.
 */
public class ItemDB {
    private static final Map<Integer, Item> allItems = new HashMap<>();

    /**
     * Initializes the database with weapons, armor, and boots.
     * 
     * @param context Application context used to load resources.
     */
    public static void init(Context context) {
        if (!allItems.isEmpty()) {
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Load sprite sheets
        Bitmap swordSheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.swords, options);
        Bitmap mainSheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.sprite_rpg_items_icons_48x48px, options);
        Bitmap placeholder = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_gallery);

        int swordSize = 32;
        int mainSize = 48;
        
        // --- WEAPONS ---
        
        addItem(new Item(
            101,              
            "Iron Sword",     
            Bitmap.createBitmap(swordSheet, 0, 0, swordSize, swordSize), 
            SlotType.WEAPON,  
            5,                
            0,                
            0,                
            0,                
            50,               
            1                 
        ));

        addItem(new Item(
            102, 
            "Steel Sword", 
            Bitmap.createBitmap(swordSheet, 32, 0, swordSize, swordSize), 
            SlotType.WEAPON, 
            10, 
            0,  
            0,  
            0,  
            150, 
            1
        ));

        addItem(new Item(
            103, 
            "Bloodstone Sword", 
            Bitmap.createBitmap(swordSheet, 128, 0, swordSize, swordSize), 
            SlotType.WEAPON, 
            18, 
            2,  
            0,  
            0,  
            500, 
            2
        ));
        
        // --- ARMOR ---
        
        addItem(new Item(
            201, 
            "Iron Plate", 
            placeholder, 
            SlotType.ARMOR, 
            0,   
            8,   
            0,   
            -2,  
            100, 
            1
        ));

        addItem(new Item(
            202, 
            "Steel Plate", 
            placeholder, 
            SlotType.ARMOR, 
            0,  
            15, 
            0,  
            -4, 
            300, 
            2
        ));

        // --- BOOTS ---
        
        // Straw Boots (Renamed from Running Shoes) - Row 4, Column 21 (x=1008, y=192)
        addItem(new Item(
            302, 
            "Straw Boots", 
            Bitmap.createBitmap(mainSheet, 1008, 192, mainSize, mainSize), 
            SlotType.BOOTS, 
            0,  
            0,  
            0,  
            15, 
            80, 
            1
        ));

        // Leather Boots - Row 4, Column 22 (x=1056, y=192)
        addItem(new Item(
            301, 
            "Leather Boots", 
            Bitmap.createBitmap(mainSheet, 1056, 192, mainSize, mainSize), 
            SlotType.BOOTS, 
            0,  
            2,  
            0,  
            50, 
            200, 
            1
        ));
    }

    public static java.util.Collection<Item> getAllItems() {
        return allItems.values();
    }

    private static void addItem(Item item) {
        allItems.put(item.id, item);
    }

    public static Item getItem(int id) {
        return allItems.get(id);
    }
}
