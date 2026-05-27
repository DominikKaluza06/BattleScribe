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
        // Prevent re-initialization if the database is already populated
        if (!allItems.isEmpty()) {
            return;
        }

        // Set options to load bitmaps without scaling to maintain pixel art quality
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        // Load sprite sheets and placeholders
        Bitmap swordSheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.swords, options);
        Bitmap placeholder = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_gallery);

        int swordSize = 32;
        
        // --- WEAPONS ---
        
        // Iron Sword: Basic starting weapon
        addItem(new Item(
            101,              // ID
            "Iron Sword",     // Name
            Bitmap.createBitmap(swordSheet, 0, 0, swordSize, swordSize), // Icon
            SlotType.WEAPON,  // Equipment Slot
            5,                // Strength Bonus
            0,                // Vitality Bonus
            0,                // Magic Bonus
            0,                // Agility Bonus
            50,               // Price (Gold)
            1                 // Required Chapter
        ));

        // Steel Sword: Mid-tier weapon
        addItem(new Item(
            102, 
            "Steel Sword", 
            Bitmap.createBitmap(swordSheet, 32, 0, swordSize, swordSize), 
            SlotType.WEAPON, 
            10, // STR
            0,  // VIT
            0,  // MGC
            0,  // AGI
            150, 
            1
        ));

        // Bloodstone Sword: Powerful late-game weapon
        addItem(new Item(
            103, 
            "Bloodstone Sword", 
            Bitmap.createBitmap(swordSheet, 128, 0, swordSize, swordSize), 
            SlotType.WEAPON, 
            18, // STR
            2,  // VIT
            0,  // MGC
            0,  // AGI
            500, 
            2
        ));
        
        // --- ARMOR ---
        
        // Iron Plate: Basic heavy armor
        addItem(new Item(
            201, 
            "Iron Plate", 
            placeholder, 
            SlotType.ARMOR, 
            0,   // STR
            8,   // VIT
            0,   // MGC
            -2,  // AGI penalty
            100, 
            1
        ));

        // Steel Plate: Advanced heavy armor
        addItem(new Item(
            202, 
            "Steel Plate", 
            placeholder, 
            SlotType.ARMOR, 
            0,  // STR
            15, // VIT
            0,  // MGC
            -4, // AGI penalty
            300, 
            2
        ));

        // --- BOOTS ---
        
        // Leather Boots: Significant speed boost
        addItem(new Item(
            301, 
            "Leather Boots", 
            placeholder, 
            SlotType.BOOTS, 
            0,  // STR
            2,  // VIT
            0,  // MGC
            50, // AGI bonus
            200, 
            1
        ));

        // Running Shoes: Entry level speed boost
        addItem(new Item(
            302, 
            "Running Shoes", 
            placeholder, 
            SlotType.BOOTS, 
            0,  // STR
            0,  // VIT
            0,  // MGC
            15, // AGI bonus
            80, 
            1
        ));
    }

    /**
     * @return A collection of all items currently in the database.
     */
    public static java.util.Collection<Item> getAllItems() {
        return allItems.values();
    }

    /**
     * Helper method to add an item to the map.
     */
    private static void addItem(Item item) {
        allItems.put(item.id, item);
    }

    /**
     * Retrieves an item by its unique ID.
     */
    public static Item getItem(int id) {
        return allItems.get(id);
    }
}
