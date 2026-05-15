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

public class BattleActivity extends AppCompatActivity {

    private Monster activeMonster;
    private ImageView monsterIcon;
    private TextView monsterName;
    private ProgressBar monsterHpBar, monsterManaBar;
    private TextView tvMonsterHp, tvMonsterMana;

    private int playerMaxHp, playerCurrentHp, playerMaxMana, playerCurrentMana;
    private int playerStr, playerDef, playerMgc, playerAgi;
    private int playerGold, playerExp, playerLevel, playerStatPoints;
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
    private boolean isPlayerTurn = true; 
    private Handler battleHandler = new Handler();
    private int wave = 1;
    private boolean isInfinite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);

        wave = getIntent().getIntExtra("WAVE", 1);
        isInfinite = getIntent().getBooleanExtra("IS_INFINITE", false);

        initUI();
        loadPlayerData();
        ItemDB.init(this);
        SkillDB.init(this);
        loadEquippedSkills();
        
        activeMonster = new Goblin(this, wave);
        
        setupBattleUI();
        log((isInfinite ? "WAVE " + wave + ": " : "") + "A wild " + activeMonster.name + " appears!");
        log("It's your turn!");
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

        // Skill Slots Initialization
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
        playerDef = statsPrefs.getInt("def", 10);
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
        
        playerMaxHp = 50 + (playerDef * 5);
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
        monsterManaBar.setMax(activeMonster.maxMana > 0 ? activeMonster.maxMana : 100);

        playerName.setText("Hero (Lv " + playerLevel + ")");
        playerHpBar.setMax(playerMaxHp);
        playerManaBar.setMax(playerMaxMana);
        
        updateBars();
    }

    private void log(String message) {
        battleLog.append("\n" + message);
    }

    private void handlePlayerAction(Skill skill) {
        if (!isPlayerTurn || activeMonster.isDead() || playerCurrentHp <= 0) return;

        int totalStr = playerStr + (equippedWeapon != null ? equippedWeapon.strBonus : 0);
        int totalDef = playerDef + (equippedWeapon != null ? equippedWeapon.defBonus : 0);
        int totalMgc = playerMgc + (equippedWeapon != null ? equippedWeapon.mgcBonus : 0);
        int totalAgi = playerAgi + (equippedWeapon != null ? equippedWeapon.agiBonus : 0);

        if (skill == null) {
            int damageValue = totalStr;
            int actualDamage = Math.max(1, damageValue - activeMonster.getTotalDef());
            activeMonster.takeDamage(damageValue);
            log("You attack " + activeMonster.name + " for " + actualDamage + " damage!");
        } else {
            if (currentCooldowns.get(skill.id) > 0) {
                Toast.makeText(this, "Skill on cooldown!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (playerCurrentMana < skill.manaCost) {
                Toast.makeText(this, "Not enough mana!", Toast.LENGTH_SHORT).show();
                return;
            }
            playerCurrentMana -= skill.manaCost;
            currentCooldowns.put(skill.id, skill.cooldown);
            
            int skillValue = skill.calculateValue(totalStr, totalDef, totalMgc, totalAgi, playerMaxHp);
            if (skill.name.toLowerCase().contains("heal")) {
                playerCurrentHp = Math.min(playerMaxHp, playerCurrentHp + skillValue);
                log("You used " + skill.name + " and restored " + skillValue + " HP!");
            } else {
                int actualDamage = Math.max(1, skillValue - activeMonster.getTotalDef());
                activeMonster.takeDamage(skillValue);
                log("You used " + skill.name + " for " + actualDamage + " damage!");
            }
        }

        updateBars();
        updateSkillIcons();
        if (activeMonster.isDead()) checkVictory();
        else {
            isPlayerTurn = false;
            battleHandler.postDelayed(this::monsterTurn, 1000);
        }
    }

    private void monsterTurn() {
        if (activeMonster.isDead() || playerCurrentHp <= 0) return;

        int finalDamage;
        if (activeMonster.currentMana >= activeMonster.maxMana && activeMonster.maxMana > 0) {
            activeMonster.currentMana -= activeMonster.maxMana;
            finalDamage = Math.max(1, 15 + (activeMonster.getTotalStr() / 2) - (playerDef / 2));
            log(activeMonster.name + " uses GOBLIN SPECIAL for " + finalDamage + " damage!");
        } else {
            finalDamage = Math.max(1, activeMonster.getTotalStr() - (playerDef / 2));
            log(activeMonster.name + " attacks you for " + finalDamage + " damage!");
        }
        
        playerCurrentHp -= finalDamage;
        
        // Player Mana Restoration
        int playerManaRegen = 2 + (playerMgc / 5);
        playerCurrentMana = Math.min(playerMaxMana, playerCurrentMana + playerManaRegen);
        
        // Monster Mana Restoration
        activeMonster.currentMana = Math.min(activeMonster.maxMana, activeMonster.currentMana + activeMonster.manaRegen);
        
        // Cooldown Tick
        for (Integer skillId : currentCooldowns.keySet()) {
            int cd = currentCooldowns.get(skillId);
            if (cd > 0) currentCooldowns.put(skillId, cd - 1);
        }
        
        updateBars();
        updateSkillIcons();
        if (playerCurrentHp <= 0) checkDefeat();
        else {
            isPlayerTurn = true;
            log("Your turn!");
        }
    }

    private void updateBars() {
        monsterHpBar.setProgress(activeMonster.currentHp);
        monsterManaBar.setProgress(activeMonster.currentMana);
        playerHpBar.setProgress(playerCurrentHp);
        playerManaBar.setProgress(playerCurrentMana);

        tvMonsterHp.setText(activeMonster.currentHp + "/" + activeMonster.getMaxHp());
        tvMonsterMana.setText(activeMonster.currentMana + "/" + activeMonster.maxMana);
        tvPlayerHp.setText(playerCurrentHp + "/" + playerMaxHp);
        tvPlayerMana.setText(playerCurrentMana + "/" + playerMaxMana);
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
        log(activeMonster.name + " defeated!");
        playerGold += activeMonster.goldReward;
        playerExp += activeMonster.expReward;
        log("Gained " + activeMonster.goldReward + " Gold and " + activeMonster.expReward + " EXP!");
        
        int expToLevel = playerLevel * 100;
        while (playerExp >= expToLevel) {
            playerExp -= expToLevel;
            playerLevel++;
            playerStatPoints += 5;
            log("LEVEL UP! Level " + playerLevel + " (+5 Stat Points)");
            expToLevel = playerLevel * 100;
        }

        savePlayerData();
        if (isInfinite) {
            SharedPreferences p = getSharedPreferences("BattleProgress", MODE_PRIVATE);
            if (wave == p.getInt("infinite_wave", 1)) p.edit().putInt("infinite_wave", wave + 1).apply();
        }
        Toast.makeText(this, "Victory!", Toast.LENGTH_SHORT).show();
        battleHandler.postDelayed(this::finish, 2000);
    }

    private void savePlayerData() {
        getSharedPreferences("CharacterStats", MODE_PRIVATE).edit()
            .putInt("gold", playerGold).putInt("exp", playerExp).putInt("level", playerLevel).putInt("statPoints", playerStatPoints).commit();
    }

    private void checkDefeat() {
        playerCurrentHp = 0;
        log("Defeated...");
        Toast.makeText(this, "Defeat!", Toast.LENGTH_LONG).show();
        battleHandler.postDelayed(this::finish, 2000);
    }
}
