package com.example.battlescribe;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Centralized manager for all game sprites and icons.
 * This class acts as a coordinate table for extracting icons from sprite sheets.
 */
public class SpriteManager {
    private static Bitmap mainSheet;
    private static Bitmap swordSheet;
    private static Bitmap skillSheet;
    
    private static final int GRID_SIZE_48 = 48;
    private static final int SWORD_SIZE_32 = 32;
    private static final int SKILL_SIZE_62 = 62;

    public static void init(Context context) {
        if (mainSheet != null && swordSheet != null && skillSheet != null) return;
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;
        
        if (mainSheet == null) {
            mainSheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.sprite_rpg_items_icons_48x48px, options);
        }
        if (swordSheet == null) {
            swordSheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.swords, options);
        }
        if (skillSheet == null) {
            skillSheet = BitmapFactory.decodeResource(context.getResources(), R.drawable.skill_icons_62x62, options);
        }
    }

    /**
     * Extracts a 48x48 sprite from the main item sheet using grid coordinates.
     */
    public static Bitmap getItemSprite(int col, int row) {
        if (mainSheet == null) return null;
        int x = col * GRID_SIZE_48;
        int y = row * GRID_SIZE_48;
        
        if (x < 0 || y < 0 || x + GRID_SIZE_48 > mainSheet.getWidth() || y + GRID_SIZE_48 > mainSheet.getHeight()) {
            return null;
        }
        
        return Bitmap.createBitmap(mainSheet, x, y, GRID_SIZE_48, GRID_SIZE_48);
    }

    /**
     * Extracts a 48x48 sprite from the main item sheet using exact pixel coordinates.
     */
    public static Bitmap getItemSpriteRaw(int x, int y) {
        if (mainSheet == null) return null;
        if (x < 0 || y < 0 || x + GRID_SIZE_48 > mainSheet.getWidth() || y + GRID_SIZE_48 > mainSheet.getHeight()) {
            return null;
        }
        return Bitmap.createBitmap(mainSheet, x, y, GRID_SIZE_48, GRID_SIZE_48);
    }

    public static Bitmap getSwordSprite(int col, int row) {
        if (swordSheet == null) return null;
        int x = col * SWORD_SIZE_32;
        int y = row * SWORD_SIZE_32;
        
        if (x < 0 || y < 0 || x + SWORD_SIZE_32 > swordSheet.getWidth() || y + SWORD_SIZE_32 > swordSheet.getHeight()) {
            return null;
        }
        
        return Bitmap.createBitmap(swordSheet, x, y, SWORD_SIZE_32, SWORD_SIZE_32);
    }

    /**
     * Extracts a 62x62 sprite from the skill sheet using exact pixel coordinates.
     */
    public static Bitmap getSkillSpriteRaw(int x, int y) {
        if (skillSheet == null) return null;
        if (x < 0 || y < 0 || x + SKILL_SIZE_62 > skillSheet.getWidth() || y + SKILL_SIZE_62 > skillSheet.getHeight()) {
            return null;
        }
        return Bitmap.createBitmap(skillSheet, x, y, SKILL_SIZE_62, SKILL_SIZE_62);
    }

    // --- SPRITE COORDINATE TABLE ---

    // Weapons (Sword Sheet - 32x32)
    public static final int[] IRON_SWORD = {0, 0};
    public static final int[] STEEL_SWORD = {1, 0};
    public static final int[] BLOODSTONE_SWORD = {4, 0};

    // Armor Items (Main Sheet - 48x48) - Raw Pixel Coordinates
    public static final int[] LEATHER_ARMOR_RAW    = {1008, 290};
    public static final int[] STONE_ARMOR_RAW      = {1056, 290};
    public static final int[] BRONZE_PLATE_ARMOR_RAW = {1104, 290};
    public static final int[] IRON_PLATE_ARMOR_RAW = {1152, 290};
    
    // Boots Items (Main Sheet - 48x48) - Grid Coordinates
    public static final int[] STRAW_BOOTS = {21, 4};
    public static final int[] LEATHER_BOOTS = {22, 4};

    // Materials (Main Sheet - 48x48) - Exact Pixel Coordinates
    public static final int[] WOOD_RAW      = {574, 386};
    public static final int[] STONE_RAW     = {862, 390};
    public static final int[] BRONZE_ORE_RAW = {574, 437};
    public static final int[] IRON_ORE_RAW = {622, 437};
    public static final int[] BRONZE_BAR_RAW = {574, 482};
    public static final int[] IRON_BAR_RAW = {624, 482};
    public static final int[] LEATHER_RAW = {384, 670};
    public static final int[] BLOODSTONE_SHARD_RAW = {94, 820};

    // Skills (Skill Sheet - 62x62) - Fixed Pixel Coordinates
    // Power Strike starts at (496, 186).
    // Fireball starts at (124, 248). Next ones count from there.
    public static final int[] POWER_STRIKE_PX = {496, 186};
    public static final int[] FIREBALL_PX = {124, 248};
    public static final int[] HEAL_PX = {620, 186};

    // Legacy / Other Materials
    public static final int[] LEATHER_MAT = {14, 7};
    public static final int[] BLOODSTONE_SHARD = {15, 7};
}
