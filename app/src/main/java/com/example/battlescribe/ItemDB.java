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

        int swordSize = 32;
        
        // Iron Sword
        Bitmap ironSwordBitmap = Bitmap.createBitmap(swordSheet, 0, 0, swordSize, swordSize);
        addItem(new Item(101, "Iron Sword", ironSwordBitmap, SlotType.WEAPON, 5, 0, 0, 0));

        // Steel Sword
        Bitmap steelSwordBitmap = Bitmap.createBitmap(swordSheet, 32, 0, swordSize, swordSize);
        addItem(new Item(102, "Steel Sword", steelSwordBitmap, SlotType.WEAPON, 10, 0, 0, 0));

        // Bloodstone Sword (5th in line 1: x = 4 * 32 = 128)
        Bitmap bloodstoneSwordBitmap = Bitmap.createBitmap(swordSheet, 128, 0, swordSize, swordSize);
        addItem(new Item(103, "Bloodstone Sword", bloodstoneSwordBitmap, SlotType.WEAPON, 18, 2, 0, 0));
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