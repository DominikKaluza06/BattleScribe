package com.example.battlescribe;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;

/**
 * Item Database class that manages all available items in the game.
 * Uses SpriteManager to load icons from centralized coordinates.
 */
public class ItemDB {
    private static final Map<Integer, Item> allItems = new HashMap<>();

    public static void init(Context context) {
        if (!allItems.isEmpty()) {
            return;
        }

        SpriteManager.init(context);
        
        // --- WEAPONS ---
        
        addItem(new Item(
            101,              
            "Iron Sword",     
            SpriteManager.getSwordSprite(SpriteManager.IRON_SWORD[0], SpriteManager.IRON_SWORD[1]), 
            SlotType.WEAPON,  
            5, 0, 0, 0, 50, 1                 
        ));

        addItem(new Item(
            102, 
            "Steel Sword", 
            SpriteManager.getSwordSprite(SpriteManager.STEEL_SWORD[0], SpriteManager.STEEL_SWORD[1]), 
            SlotType.WEAPON, 
            10, 0, 0, 0, 150, 1
        ));

        addItem(new Item(
            103, 
            "Bloodstone Sword", 
            SpriteManager.getSwordSprite(SpriteManager.BLOODSTONE_SWORD[0], SpriteManager.BLOODSTONE_SWORD[1]), 
            SlotType.WEAPON, 
            18, 2, 0, 0, 500, 2
        ));
        
        // --- ARMOR ---
        
        addItem(new Item(
            201, 
            "Leather Plate", 
            SpriteManager.getItemSpriteRaw(SpriteManager.LEATHER_ARMOR_RAW[0], SpriteManager.LEATHER_ARMOR_RAW[1]), 
            SlotType.ARMOR, 
            0, 5, 0, 5, 80, 1
        ));

        addItem(new Item(
            202, 
            "Stone Plate", 
            SpriteManager.getItemSpriteRaw(SpriteManager.STONE_ARMOR_RAW[0], SpriteManager.STONE_ARMOR_RAW[1]), 
            SlotType.ARMOR, 
            0, 10, 0, -5, 150, 1
        ));

        addItem(new Item(
            203, 
            "Bronze Plate", 
            SpriteManager.getItemSpriteRaw(SpriteManager.BRONZE_PLATE_ARMOR_RAW[0], SpriteManager.BRONZE_PLATE_ARMOR_RAW[1]), 
            SlotType.ARMOR, 
            0, 18, 0, -10, 300, 2
        ));

        addItem(new Item(
            204, 
            "Iron Plate", 
            SpriteManager.getItemSpriteRaw(SpriteManager.IRON_PLATE_ARMOR_RAW[0], SpriteManager.IRON_PLATE_ARMOR_RAW[1]), 
            SlotType.ARMOR, 
            0, 28, 0, -15, 600, 2
        ));

        // --- BOOTS ---
        
        addItem(new Item(
            302, 
            "Straw Boots", 
            SpriteManager.getItemSprite(SpriteManager.STRAW_BOOTS[0], SpriteManager.STRAW_BOOTS[1]), 
            SlotType.BOOTS, 
            0, 0, 0, 15, 80, 1
        ));

        addItem(new Item(
            301, 
            "Leather Boots", 
            SpriteManager.getItemSprite(SpriteManager.LEATHER_BOOTS[0], SpriteManager.LEATHER_BOOTS[1]), 
            SlotType.BOOTS, 
            0, 2, 0, 50, 200, 1
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
