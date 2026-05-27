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
        Bitmap placeholder = BitmapFactory.decodeResource(context.getResources(), android.R.drawable.ic_menu_gallery);

        int swordSize = 32;
        
        // Weapons (id, name, icon, slot, str, vit, mgc, agi, price, requiredChapter)
        Bitmap ironSwordBitmap = Bitmap.createBitmap(swordSheet, 0, 0, swordSize, swordSize);
        addItem(new Item(101, "Iron Sword", ironSwordBitmap, SlotType.WEAPON, 5, 0, 0, 0, 50, 1));

        Bitmap steelSwordBitmap = Bitmap.createBitmap(swordSheet, 32, 0, swordSize, swordSize);
        addItem(new Item(102, "Steel Sword", steelSwordBitmap, SlotType.WEAPON, 10, 0, 0, 0, 150, 1));

        Bitmap bloodstoneSwordBitmap = Bitmap.createBitmap(swordSheet, 128, 0, swordSize, swordSize);
        addItem(new Item(103, "Bloodstone Sword", bloodstoneSwordBitmap, SlotType.WEAPON, 18, 2, 0, 0, 500, 2));
        
        // Armor
        addItem(new Item(201, "Iron Plate", placeholder, SlotType.ARMOR, 0, 8, 0, -2, 100, 1));
        addItem(new Item(202, "Steel Plate", placeholder, SlotType.ARMOR, 0, 15, 0, -4, 300, 2));

        // Boots
        addItem(new Item(301, "Leather Boots", placeholder, SlotType.BOOTS, 0, 2, 0, 5, 50, 1));
        addItem(new Item(302, "Running Shoes", placeholder, SlotType.BOOTS, 0, 0, 0, 30, 200, 1));
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