package com.example.battlescribe;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
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

public class BattleActivity extends AppCompatActivity {

    private Monster activeMonster;
    private ImageView monsterIcon;
    private TextView monsterName;
    private ProgressBar monsterHpBar, monsterManaBar;
    private TextView tvMonsterHp, tvMonsterMana;

    private int playerMaxHp, playerCurrentHp, playerMaxMana, playerCurrentMana;
    private int playerStr, playerVit, playerMgc, playerAgi;
    private int playerGold, playerExp, playerLevel, playerStatPoints;
    private int playerCharge = 0; // CTB Charge
    private Item equippedWeapon;
    private List<Skill> equippedSkills = new ArrayList<>();
    private Map<Integer, Integer> currentCooldowns = new HashMap<>();

    private TextView playerName;
    private ProgressBar playerHpBar, playerManaBar;
    private TextView tvPlayerHp, tvPlayerMana;
    private ImageView btnBasicAttack;
    private View[] skillContainers = new View[4];
    private ImageView[] skillSlots = new ImageView[4];
    private TextView[] skillCdTexts = new TextView[4];

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

        isAdventureMode = getIntent().getBooleanExtra("IS_ADVENTURE", false);
        isStoryMode = getIntent().getBooleanExtra("STORY_MODE", false);
        String monsterType = getIntent().getStringExtra("MONSTER_TYPE");

        initUI();
        loadPlayerData();
        ItemDB.init(this);
        SkillDB.init(this);
        MaterialDB.init(this);
        loadEquippedSkills();
        
        int monsterLevel = playerLevel;
        double difficultyMult = 1.0;
        
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
        
        // Start the CTB loop
        battleHandler.postDelayed(this::processCTB, 1000);
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

        btnBasicAttack.setOnClickListener(v -> handlePlayerAction(null));
    }

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
        int weaponId = equipPrefs.getInt(SlotType.WEAPON.name(), -1);
        if (weaponId != -1) {
            equippedWeapon = ItemDB.getItem(weaponId);
            if (equippedWeapon != null) btnBasicAttack.setImageBitmap(equippedWeapon.iconBitmap);
        } else {
            btnBasicAttack.setImageResource(android.R.drawable.ic_menu_send);
        }
        
        playerMaxHp = 100 + (playerVit * 10);
        playerCurrentHp = playerMaxHp;
        playerMaxMana = 20 + (playerMgc * 3);
        playerCurrentMana = playerMaxMana;
    }

    private void loadEquippedSkills() {
        SharedPreferences skillPrefs = getSharedPreferences("CharacterSkills", MODE_PRIVATE);
        List<Skill> allSkills = SkillDB.getAllSkills();
        int slotIndex = 0;
        for (Skill skill : allSkills) {
            if (skillPrefs.getBoolean("equipped_" + skill.id, false) && slotIndex < 4) {
                equippedSkills.add(skill);
                currentCooldowns.put(skill.id, 0);
                if (skillContainers[slotIndex] != null) {
                    skillContainers[slotIndex].setVisibility(View.VISIBLE);
                }
                skillSlots[slotIndex].setVisibility(View.VISIBLE);
                skillSlots[slotIndex].setImageBitmap(skill.iconBitmap);
                final Skill s = skill;
                skillSlots[slotIndex].setOnClickListener(v -> handlePlayerAction(s));
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

    private void processCTB() {
        if (activeMonster.isDead() || playerCurrentHp <= 0) return;

        int pSpeed = 10 + (playerAgi + (equippedWeapon != null ? equippedWeapon.agiBonus : 0));
        int mSpeed = activeMonster.getSpeed();

        while (playerCharge < 100 && activeMonster.getCurrentCharge() < 100) {
            playerCharge += pSpeed;
            activeMonster.addCharge(mSpeed);
        }

        if (playerCharge >= 100) {
            playerCharge -= 100;
            isPlayerTurn = true;
            log(getString(R.string.battle_log_your_turn));
        } else {
            activeMonster.addCharge(-100); // Reduce by 100 instead of resetting
            monsterTurn();
        }
    }

    private void handlePlayerAction(Skill skill) {
        if (!isPlayerTurn || activeMonster.isDead() || playerCurrentHp <= 0) return;

        int tStr = playerStr + (equippedWeapon != null ? equippedWeapon.strBonus : 0);
        int tVit = playerVit + (equippedWeapon != null ? equippedWeapon.vitBonus : 0);
        int tMgc = playerMgc + (equippedWeapon != null ? equippedWeapon.mgcBonus : 0);
        int tAgi = playerAgi + (equippedWeapon != null ? equippedWeapon.agiBonus : 0);

        // Crit logic
        double critChance = tAgi * 0.005;
        double critMult = 1.5 + (tStr * 0.01);
        boolean isCrit = rnd.nextDouble() < critChance;

        if (skill == null) {
            int baseDamage = tStr;
            int finalDamage = (int)(baseDamage * (isCrit ? critMult : 1.0));
            int actualDamage = Math.max(1, finalDamage - activeMonster.getTotalVit()); 
            activeMonster.takeDamage(finalDamage);
            String msg = getString(R.string.battle_log_player_attack, activeMonster.name, actualDamage);
            if (isCrit) msg = "CRITICAL! " + msg;
            log(msg);
        } else {
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
                playerCurrentHp = Math.min(playerMaxHp, playerCurrentHp + skillValue);
                log(getString(R.string.battle_log_player_heal, skillValue));
            } else {
                int finalDamage = (int)(skillValue * (isCrit ? critMult : 1.0));
                int actualDamage = Math.max(1, finalDamage - activeMonster.getTotalVit());
                activeMonster.takeDamage(finalDamage);
                String msg = getString(R.string.battle_log_player_skill, skill.name, actualDamage);
                if (isCrit) msg = "CRITICAL! " + msg;
                log(msg);
            }
        }

        updateBars();
        updateSkillIcons();
        if (activeMonster.isDead()) checkVictory();
        else {
            isPlayerTurn = false;
            battleHandler.postDelayed(this::processCTB, 1000);
        }
    }

    private void monsterTurn() {
        if (activeMonster.isDead() || playerCurrentHp <= 0) return;

        // Monster Crit logic
        boolean isCrit = rnd.nextDouble() < activeMonster.getCritChance();
        double critMult = activeMonster.getCritMultiplier();

        int finalDamage;
        if (activeMonster.currentMana >= activeMonster.maxMana && activeMonster.maxMana > 0) {
            activeMonster.currentMana -= activeMonster.maxMana;
            int baseDamage = (int)(15 * (activeMonster.getMaxHp() / 50.0));
            finalDamage = (int)(baseDamage * (isCrit ? critMult : 1.0));
            int actualDamage = Math.max(1, finalDamage - (playerVit / 2));
            log((isCrit ? "CRITICAL! " : "") + getString(R.string.battle_log_monster_special, activeMonster.name, "SPECIAL", actualDamage));
            playerCurrentHp -= actualDamage;
        } else {
            int baseDamage = activeMonster.getTotalStr();
            finalDamage = (int)(baseDamage * (isCrit ? critMult : 1.0));
            int actualDamage = Math.max(1, finalDamage - (playerVit / 2));
            log((isCrit ? "CRITICAL! " : "") + getString(R.string.battle_log_monster_attack, activeMonster.name, actualDamage));
            playerCurrentHp -= actualDamage;
        }
        
        int playerManaRegen = 2 + (playerMgc / 5);
        playerCurrentMana = Math.min(playerMaxMana, playerCurrentMana + playerManaRegen);
        activeMonster.currentMana = Math.min(activeMonster.maxMana, activeMonster.currentMana + activeMonster.manaRegen);
        
        for (Integer skillId : currentCooldowns.keySet()) {
            int cd = currentCooldowns.get(skillId);
            if (cd > 0) currentCooldowns.put(skillId, cd - 1);
        }
        
        updateBars();
        updateSkillIcons();
        if (playerCurrentHp <= 0) checkDefeat();
        else {
            battleHandler.postDelayed(this::processCTB, 1000);
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

    private void checkVictory() {
        log(getString(R.string.battle_log_victory, activeMonster.name));
        
        playerGold += activeMonster.goldReward;
        playerExp += activeMonster.expReward;
        log(getString(R.string.battle_log_rewards, activeMonster.goldReward, activeMonster.expReward));
        
        // Material drops logic
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

        if (isStoryMode && activeMonster instanceof Goblin) {
            SharedPreferences storyPrefs = getSharedPreferences("StoryProgress", MODE_PRIVATE);
            if (storyPrefs.getInt("chapter", 1) == 1) {
                storyPrefs.edit().putInt("chapter", 2).putInt("step", 0).apply();
                log("STORY CHAPTER 2 UNLOCKED!");
            }
        }

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
        battleHandler.postDelayed(this::finish, 2000);
    }

    private void savePlayerData() {
        getSharedPreferences("CharacterStats", MODE_PRIVATE).edit()
            .putInt("gold", playerGold).putInt("exp", playerExp).putInt("level", playerLevel).putInt("statPoints", playerStatPoints).commit();
    }

    private void checkDefeat() {
        playerCurrentHp = 0;
        log(getString(R.string.battle_log_defeat));
        Toast.makeText(this, R.string.battle_log_defeat, Toast.LENGTH_LONG).show();
        battleHandler.postDelayed(this::finish, 2000);
    }
}
