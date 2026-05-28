package com.example.battlescribe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Core battle activity that handles the turn-based combat system (CTB),
 * damage calculations, critical hits, and player/monster interactions.
 */
public class BattleActivity extends AppCompatActivity {

    // --- Monster UI and Data ---
    private Monster activeMonster;
    private ImageView monsterIcon;
    private TextView monsterName;
    private ProgressBar monsterHpBar, monsterManaBar;
    private TextView tvMonsterHp, tvMonsterMana;

    // --- Player Data ---
    private int playerMaxHp, playerCurrentHp, playerMaxMana, playerCurrentMana;
    private int playerStr, playerVit, playerMgc, playerAgi;
    private int playerGold, playerExp, playerLevel, playerStatPoints;
    
    /**
     * Charge meter for the CTB (Charge Time Battle) system.
     * When it reaches 100, the player takes a turn.
     */
    private int playerCharge = 0; 
    
    private Item equippedWeapon;
    private Map<SlotType, Item> equippedItems = new HashMap<>();
    private List<Skill> equippedSkills = new ArrayList<>();
    private Map<Integer, Integer> currentCooldowns = new HashMap<>();

    // --- Player UI ---
    private TextView playerName;
    private ProgressBar playerHpBar, playerManaBar;
    private TextView tvPlayerHp, tvPlayerMana;
    private ImageView btnBasicAttack;
    private View[] skillContainers = new View[4];
    private ImageView[] skillSlots = new ImageView[4];
    private TextView[] skillCdTexts = new TextView[4];

    // --- Logic Utilities ---
    private TextView battleLog;
    private boolean isPlayerTurn = false; 
    private Handler battleHandler = new Handler();
    private boolean isAdventureMode = false;
    private boolean isStoryMode = false;
    private Random rnd = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        // Extract settings from the intent that launched this battle
        isAdventureMode = getIntent().getBooleanExtra("IS_ADVENTURE", false);
        isStoryMode = getIntent().getBooleanExtra("STORY_MODE", false);
        String monsterType = getIntent().getStringExtra("MONSTER_TYPE");

        initUI();
        ItemDB.init(this);
        SkillDB.init(this);
        MaterialDB.init(this);
        loadPlayerData();
        loadEquippedSkills();
        
        // In Adventure Mode, monsters match the player's level
        int monsterLevel = playerLevel;
        double difficultyMult = 1.0;
        
        // Spawn the target enemy
        if (isStoryMode) {
            activeMonster = new Goblin(this, 1, 1.0);
        } else if (isAdventureMode) {
            if (monsterType != null) {
                if (monsterType.equals("SKELETON")) {
                    activeMonster = new Skeleton(this, monsterLevel, difficultyMult);
                } else if (monsterType.equals("ZOMBIE")) {
                    activeMonster = new Zombie(this, monsterLevel, difficultyMult);
                } else {
                    activeMonster = new Goblin(this, monsterLevel, difficultyMult);
                }
            } else {
                activeMonster = new Zombie(this, monsterLevel, difficultyMult);
            }
        } else {
            activeMonster = new Goblin(this, 1, 1.0);
        }
        
        setupBattleUI();
        log(getString(R.string.battle_log_start, activeMonster.name));
        
        // Kick off the Charge Time loop
        battleHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                processCTB();
            }
        }, 1000);
    }

    private void initUI() {
        monsterIcon = findViewById(R.id.monster_icon);
        monsterName = findViewById(R.id.monster_name);
        monsterHpBar = findViewById(R.id.monster_hp_bar);
        monsterManaBar = findViewById(R.id.monster_mana_bar);
        tvMonsterHp = findViewById(R.id.tv_monster_hp_num);
        tvMonsterMana = findViewById(R.id.tv_monster_mana_num);

        playerName = findViewById(R.id.player_name);
        playerHpBar = findViewById(R.id.player_hp_bar);
        playerManaBar = findViewById(R.id.player_mana_bar);
        tvPlayerHp = findViewById(R.id.tv_player_hp_num);
        tvPlayerMana = findViewById(R.id.tv_player_mana_num);

        battleLog = findViewById(R.id.battle_log_text);
        btnBasicAttack = findViewById(R.id.btn_basic_attack);

        int[] slotIds = {R.id.skill_slot1, R.id.skill_slot2, R.id.skill_slot3, R.id.skill_slot4};
        int[] cdIds = {R.id.tv_skill_cd1, R.id.tv_skill_cd2, R.id.tv_skill_cd3, R.id.tv_skill_cd4};

        for (int i = 0; i < 4; i++) {
            skillSlots[i] = findViewById(slotIds[i]);
            skillCdTexts[i] = findViewById(cdIds[i]);
            if (skillSlots[i] != null) {
                skillContainers[i] = (View) skillSlots[i].getParent();
            }
        }

        // Basic attack listener
        btnBasicAttack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePlayerAction(null);
            }
        });
    }

    /**
     * Loads player stats and computes max HP/Mana based on current attributes.
     */
    private void loadPlayerData() {
        SharedPreferences statsPrefs = getSharedPreferences("CharacterStats", MODE_PRIVATE);
        playerStr = statsPrefs.getInt("str", 10);
        playerVit = statsPrefs.getInt("vit", 10);
        playerMgc = statsPrefs.getInt("mgc", 10);
        playerAgi = statsPrefs.getInt("agi", 10);
        playerGold = statsPrefs.getInt("gold", 0);
        playerExp = statsPrefs.getInt("exp", 0);
        playerLevel = statsPrefs.getInt("level", 1);
        playerStatPoints = statsPrefs.getInt("statPoints", 0);

        SharedPreferences equipPrefs = getSharedPreferences("EquippedItems", MODE_PRIVATE);
        int totalVitBonus = 0;
        int totalMgcBonus = 0;

        // Iterate through all slots to sum up bonuses
        for (SlotType type : SlotType.values()) {
            int itemId = equipPrefs.getInt(type.name(), -1);
            if (itemId != -1) {
                Item item = ItemDB.getItem(itemId);
                if (item != null) {
                    equippedItems.put(type, item);
                    totalVitBonus += item.vitBonus;
                    totalMgcBonus += item.mgcBonus;
                    if (type == SlotType.WEAPON) {
                        equippedWeapon = item;
                        btnBasicAttack.setImageBitmap(item.iconBitmap);
                    }
                }
            }
        }
        
        if (equippedWeapon == null) {
            btnBasicAttack.setImageResource(android.R.drawable.ic_menu_send);
        }
        
        // Calculate dynamic limits
        int totalVit = playerVit + totalVitBonus;
        int totalMgc = playerMgc + totalMgcBonus;
        
        // Formula: Base 50 HP at 10 VIT, +10 HP per extra point
        playerMaxHp = 50 + (totalVit - 10) * 10;
        playerCurrentHp = playerMaxHp;
        
        // Formula: Base 20 Mana, +3 per MGC point
        playerMaxMana = 20 + (totalMgc * 3);
        playerCurrentMana = playerMaxMana;
    }

    /**
     * Checks which skills are marked as equipped in storage and prepares them for battle.
     */
    private void loadEquippedSkills() {
        SharedPreferences skillPrefs = getSharedPreferences("CharacterSkills", MODE_PRIVATE);
        List<Skill> allSkills = SkillDB.getAllSkills();
        int slotIndex = 0;
        for (final Skill skill : allSkills) {
            if (skillPrefs.getBoolean("equipped_" + skill.id, false) && slotIndex < 4) {
                equippedSkills.add(skill);
                currentCooldowns.put(skill.id, 0);
                
                if (skillContainers[slotIndex] != null) {
                    skillContainers[slotIndex].setVisibility(View.VISIBLE);
                }
                
                skillSlots[slotIndex].setVisibility(View.VISIBLE);
                skillSlots[slotIndex].setImageBitmap(skill.iconBitmap);
                
                // Listener for specific skill slot
                skillSlots[slotIndex].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handlePlayerAction(skill);
                    }
                });
                slotIndex++;
            }
        }
    }

    private void setupBattleUI() {
        monsterName.setText(activeMonster.name);
        monsterIcon.setImageBitmap(activeMonster.icon);
        monsterHpBar.setMax(activeMonster.getMaxHp());
        monsterHpBar.setProgress(activeMonster.currentHp);
        monsterManaBar.setMax(activeMonster.maxMana > 0 ? activeMonster.maxMana : 100);

        playerName.setText(getString(R.string.hero_lv_format, playerLevel));
        playerHpBar.setMax(playerMaxHp);
        playerManaBar.setMax(playerMaxMana);
        
        updateBars();
    }

    private void log(String message) {
        battleLog.append("\n" + message);
    }

    /**
     * CTB Processing: Increments charge values based on speed.
     * Speed is derived from Agility.
     */
    private void processCTB() {
        if (activeMonster.isDead() || playerCurrentHp <= 0) return;

        // Player total agility including equipment
        int totalAgi = playerAgi;
        for (Item item : equippedItems.values()) {
            totalAgi += item.agiBonus;
        }
        
        int pSpeed = 10 + totalAgi; 
        int mSpeed = activeMonster.getSpeed(); 

        // Loop until someone hits the 100 charge threshold
        while (playerCharge < 100 && activeMonster.getCurrentCharge() < 100) {
            playerCharge += pSpeed;
            activeMonster.addCharge(mSpeed);
        }

        if (playerCharge >= 100) {
            playerCharge -= 100; // Deduct cost of turn
            isPlayerTurn = true;
            log(getString(R.string.battle_log_your_turn));
        } else {
            activeMonster.reduceCharge(100); // Deduct cost of turn
            monsterTurn();
        }
    }

    /**
     * Executes player's chosen action.
     * @param skill Null for basic attack, otherwise uses the selected Skill.
     */
    private void handlePlayerAction(Skill skill) {
        if (!isPlayerTurn || activeMonster.isDead() || playerCurrentHp <= 0) return;

        // Total stats calculation
        int tStr = playerStr;
        int tVit = playerVit;
        int tMgc = playerMgc;
        int tAgi = playerAgi;
        for (Item item : equippedItems.values()) {
            tStr += item.strBonus;
            tVit += item.vitBonus;
            tMgc += item.mgcBonus;
            tAgi += item.agiBonus;
        }

        // Critical Hit logic:
        // AGI -> Increases Crit Chance (0.5% per point)
        // STR -> Increases Crit Damage (150% base + 1% per point)
        double critChance = tAgi * 0.005;
        double critMult = 1.5 + (tStr * 0.01);
        boolean isCrit = rnd.nextDouble() < critChance;

        if (skill == null) {
            // Basic Weapon Attack
            int baseDamage = tStr;
            int finalDamage = (int)(baseDamage * (isCrit ? critMult : 1.0));
            
            activeMonster.takeDamage(finalDamage);
            
            String msg = getString(R.string.battle_log_player_attack, activeMonster.name, finalDamage);
            if (isCrit) msg = "CRITICAL! " + msg;
            log(msg);
        } else {
            // Player Skill Usage
            if (currentCooldowns.get(skill.id) > 0) {
                Toast.makeText(this, getString(R.string.toast_skill_on_cooldown), Toast.LENGTH_SHORT).show();
                return;
            }
            if (playerCurrentMana < skill.manaCost) {
                Toast.makeText(this, getString(R.string.toast_not_enough_mana), Toast.LENGTH_SHORT).show();
                return;
            }
            playerCurrentMana -= skill.manaCost;
            currentCooldowns.put(skill.id, skill.cooldown);
            
            int skillValue = skill.calculateValue(tStr, tVit, tMgc, tAgi, playerMaxHp);
            
            if (skill.name.toLowerCase().contains("heal")) {
                // Healing Skill
                playerCurrentHp = Math.min(playerMaxHp, playerCurrentHp + skillValue);
                log(getString(R.string.battle_log_player_heal, skillValue));
            } else {
                // Damage Skill
                int finalDamage = (int)(skillValue * (isCrit ? critMult : 1.0));
                activeMonster.takeDamage(finalDamage);
                String msg = getString(R.string.battle_log_player_skill, skill.name, finalDamage);
                if (isCrit) msg = "CRITICAL! " + msg;
                log(msg);
            }
        }

        updateBars();
        updateSkillIcons();
        
        if (activeMonster.isDead()) {
            checkVictory();
        } else {
            isPlayerTurn = false;
            // Return to timing loop
            battleHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    processCTB();
                }
            }, 1000);
        }
    }

    /**
     * Logic for the AI monster's turn.
     * Damage is reduced by player's Vitality (1 Defense per VIT).
     */
    private void monsterTurn() {
        if (activeMonster.isDead() || playerCurrentHp <= 0) return;

        // Player total VIT for defense
        int tVit = playerVit;
        int tMgc = playerMgc;
        for (Item item : equippedItems.values()) {
            tVit += item.vitBonus;
            tMgc += item.mgcBonus;
        }

        boolean isCrit = rnd.nextDouble() < activeMonster.getCritChance();
        double critMult = activeMonster.getCritMultiplier();

        int damageToDeal;
        if (activeMonster.currentMana >= activeMonster.maxMana && activeMonster.maxMana > 0) {
            // Special Attack when mana is full
            activeMonster.currentMana -= activeMonster.maxMana;
            int baseDamage = (int)(15 * (activeMonster.getMaxHp() / 50.0));
            damageToDeal = (int)(baseDamage * (isCrit ? critMult : 1.0));
            int actualDamage = Math.max(1, damageToDeal - tVit); 
            log((isCrit ? "CRITICAL! " : "") + getString(R.string.battle_log_monster_special, activeMonster.name, "SPECIAL", actualDamage));
            playerCurrentHp -= actualDamage;
        } else {
            // Standard Monster Attack
            int baseDamage = activeMonster.getTotalStr();
            damageToDeal = (int)(baseDamage * (isCrit ? critMult : 1.0));
            int actualDamage = Math.max(1, damageToDeal - tVit);
            log((isCrit ? "CRITICAL! " : "") + getString(R.string.battle_log_monster_attack, activeMonster.name, actualDamage));
            playerCurrentHp -= actualDamage;
        }
        
        // Mana regeneration logic
        int playerManaRegen = 2 + (tMgc / 5);
        playerCurrentMana = Math.min(playerMaxMana, playerCurrentMana + playerManaRegen);
        activeMonster.currentMana = Math.min(activeMonster.maxMana, activeMonster.currentMana + activeMonster.manaRegen);
        
        // Cooldown ticking
        for (Integer skillId : currentCooldowns.keySet()) {
            int cd = currentCooldowns.get(skillId);
            if (cd > 0) currentCooldowns.put(skillId, cd - 1);
        }
        
        updateBars();
        updateSkillIcons();
        
        if (playerCurrentHp <= 0) {
            checkDefeat();
        } else {
            // Return to timing loop
            battleHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    processCTB();
                }
            }, 1000);
        }
    }

    private void updateBars() {
        monsterHpBar.setProgress(activeMonster.currentHp);
        monsterManaBar.setProgress(activeMonster.currentMana);
        playerHpBar.setProgress(playerCurrentHp);
        playerManaBar.setProgress(playerCurrentMana);

        tvMonsterHp.setText(getString(R.string.label_hp_format, activeMonster.currentHp, activeMonster.getMaxHp()));
        tvMonsterMana.setText(getString(R.string.label_mana_format, activeMonster.currentMana, activeMonster.maxMana));
        tvPlayerHp.setText(getString(R.string.label_hp_format, playerCurrentHp, playerMaxHp));
        tvPlayerMana.setText(getString(R.string.label_mana_format, playerCurrentMana, playerMaxMana));
    }

    private void updateSkillIcons() {
        for (int i = 0; i < equippedSkills.size(); i++) {
            Skill s = equippedSkills.get(i);
            int cd = currentCooldowns.get(s.id);
            if (cd > 0) {
                skillSlots[i].setAlpha(0.4f);
                skillCdTexts[i].setVisibility(View.VISIBLE);
                skillCdTexts[i].setText(String.valueOf(cd));
            } else {
                skillSlots[i].setAlpha(1.0f);
                skillCdTexts[i].setVisibility(View.GONE);
            }
        }
    }


    /**
     * Standard utility to hide status and navigation bars for fullscreen immersion.
     */
    private void hideSystemUI() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            final WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    /**
     * Rewards the player upon victory and checks for level ups.
     */
    private void checkVictory() {
        log(getString(R.string.battle_log_victory, activeMonster.name));
        
        playerGold += activeMonster.goldReward;
        playerExp += activeMonster.expReward;
        log(getString(R.string.battle_log_rewards, activeMonster.goldReward, activeMonster.expReward));
        
        // Handle monster item drops
        SharedPreferences matPrefs = getSharedPreferences("MaterialInventory", MODE_PRIVATE);
        SharedPreferences.Editor matEditor = matPrefs.edit();
        for (Map.Entry<Integer, Double> drop : activeMonster.materialDrops.entrySet()) {
            if (rnd.nextDouble() < drop.getValue()) {
                int matId = drop.getKey();
                Material mat = MaterialDB.getMaterial(matId);
                if (mat != null) {
                    int count = matPrefs.getInt("mat_" + matId, 0);
                    matEditor.putInt("mat_" + matId, count + 1);
                    log("Dropped: " + mat.name + "!");
                }
            }
        }
        matEditor.apply();

        // Advance story if applicable
        if (isStoryMode && activeMonster instanceof Goblin) {
            SharedPreferences storyPrefs = getSharedPreferences("StoryProgress", MODE_PRIVATE);
            if (storyPrefs.getInt("chapter", 1) == 1) {
                storyPrefs.edit().putInt("chapter", 2).putInt("step", 0).apply();
                log("STORY CHAPTER 2 UNLOCKED!");
            }
        }

        // Check for Level Up (Threshold: Level * 100 EXP)
        int expToLevel = playerLevel * 100;
        while (playerExp >= expToLevel) {
            playerExp -= expToLevel;
            playerLevel++;
            playerStatPoints += 5;
            log(getString(R.string.battle_log_level_up, playerLevel));
            expToLevel = playerLevel * 100;
        }

        savePlayerData();
        Toast.makeText(this, "Victory!", Toast.LENGTH_SHORT).show();
        
        battleHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }

    private void savePlayerData() {
        getSharedPreferences("CharacterStats", MODE_PRIVATE).edit()
            .putInt("gold", playerGold)
            .putInt("exp", playerExp)
            .putInt("level", playerLevel)
            .putInt("statPoints", playerStatPoints)
            .commit();
    }

    private void checkDefeat() {
        playerCurrentHp = 0;
        log(getString(R.string.battle_log_defeat));
        Toast.makeText(this, R.string.battle_log_defeat, Toast.LENGTH_LONG).show();
        
        battleHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 2000);
    }
}
