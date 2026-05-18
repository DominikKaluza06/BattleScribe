package com.example.battlescribe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.util.HashMap;
import java.util.Map;

public class ItemDB {
    private static final Map<Integer, Item> allItems = new HashMap<>();

    public static void init(Context context) {
        if (!allItems.isEmpty()) return;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        Bitmap swordSheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.swords, options);
        // Using a placeholder for armor until you have a real sprite sheet
        Bitmap placeholder = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_gallery);

        int swordSize = 32;
        
        // Weapons
        Bitmap ironSwordBitmap = Bitmap.createBitmap(swordSheet, 0, 0, swordSize, swordSize);
        addItem(new Item(101, "Iron Sword", ironSwordBitmap, SlotType.WEAPON, 5, 0, 0, 0, 50));

        Bitmap steelSwordBitmap = Bitmap.createBitmap(swordSheet, 32, 0, swordSize, swordSize);
        addItem(new Item(102, "Steel Sword", steelSwordBitmap, SlotType.WEAPON, 10, 0, 0, 0, 150));

        Bitmap bloodstoneSwordBitmap = Bitmap.createBitmap(swordSheet, 128, 0, swordSize, swordSize);
        addItem(new Item(103, "Bloodstone Sword", bloodstoneSwordBitmap, SlotType.WEAPON, 18, 2, 0, 0, 500));
        

        addItem(new Item(201, "Iron Plate", placeholder, SlotType.ARMOR, 0, 8, 0, -2, 100));
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