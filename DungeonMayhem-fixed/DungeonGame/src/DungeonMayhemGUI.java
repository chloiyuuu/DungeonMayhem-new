import dungeon.entities.Player;
import dungeon.entities.enemies.*;
import dungeon.entities.Enemy;
import dungeon.exceptions.GameException;
import dungeon.game.GameStatus;
import dungeon.shop.Shop;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class DungeonMayhemGUI extends JFrame {

    // ── Palette (named constants — not hardcoded per widget) ─────────────────
    private static final Color BG_DARK      = new Color(22, 22, 32);
    private static final Color BG_PANEL     = new Color(35, 35, 52);
    private static final Color BG_CARD      = new Color(42, 42, 62);
    private static final Color ACCENT_RED   = new Color(210, 55, 55);
    private static final Color ACCENT_GOLD  = new Color(230, 185, 55);
    private static final Color ACCENT_BLUE  = new Color(80, 140, 220);
    private static final Color TEXT_LIGHT   = new Color(220, 220, 230);
    private static final Color TEXT_MUTED   = new Color(140, 140, 160);
    private static final Color BTN_NORMAL   = new Color(55, 55, 75);
    private static final Color BTN_HOVER    = new Color(80, 80, 105);
    private static final Color BTN_GREEN    = new Color(45, 110, 60);
    private static final Color BTN_GOLD     = new Color(140, 105, 20);
    private static final Color HP_GREEN     = new Color(65, 185, 65);
    private static final Color HP_RED       = new Color(185, 60, 60);
    private static final Color HP_YELLOW    = new Color(200, 185, 50);

    private GameStatus gameStatus;
    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Battle panel refs
    private JLabel playerNameLabel, playerHpLabel, playerHpBarLabel, playerGoldLabel;
    private JLabel enemyNameLabel, enemyHpLabel, enemyHpBarLabel;
    private JLabel floorLabel, teamLabel;
    private JTextArea battleLog;
    private JButton basicBtn, skill1Btn, skill2Btn, skill3Btn;
    private JButton nextFloorBtn, openShopBtn;
    private Enemy currentEnemy;

    public DungeonMayhemGUI(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        setTitle("Dungeon Mayhem");
        setSize(960, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.setBackground(BG_DARK);

        mainPanel.add(createMainMenu(),    "MENU");
        mainPanel.add(createBattlePanel(), "BATTLE");

        add(mainPanel);
        cardLayout.show(mainPanel, "MENU");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // MAIN MENU
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel createMainMenu() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BG_DARK);
        GridBagConstraints g = new GridBagConstraints();

        JLabel title = new JLabel("DUNGEON MAYHEM");
        title.setFont(new Font("Serif", Font.BOLD, 68));
        title.setForeground(ACCENT_RED);
        g.gridx = 0; g.gridy = 0; g.insets = new Insets(0, 0, 12, 0);
        panel.add(title, g);

        JLabel sub = new JLabel("A Knight. A Princess. 10 Floors of Chaos.");
        sub.setFont(new Font("SansSerif", Font.ITALIC, 16));
        sub.setForeground(TEXT_MUTED);
        g.gridy = 1; g.insets = new Insets(0, 0, 40, 0);
        panel.add(sub, g);

        JButton startBtn = makeButton("⚔  ENTER THE DUNGEON", 320, 65, 22);
        startBtn.addActionListener(e -> showCharacterSetup());
        g.gridy = 2; g.insets = new Insets(0, 0, 0, 0);
        panel.add(startBtn, g);

        JLabel hint = new JLabel("Clear floors · Earn gold · Build your alliance");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 13));
        hint.setForeground(TEXT_MUTED);
        g.gridy = 3; g.insets = new Insets(18, 0, 0, 0);
        panel.add(hint, g);

        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CHARACTER SETUP
    // ═══════════════════════════════════════════════════════════════════════
    private void showCharacterSetup() {
        // Name
        String playerName = null;
        while (playerName == null) {
            String input = JOptionPane.showInputDialog(this,
                    "Enter your Hero's name:", "Character Setup", JOptionPane.QUESTION_MESSAGE);
            if (input == null) return;
            if (input.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "A name is required to enter the dungeon!", "Error", JOptionPane.ERROR_MESSAGE);
            } else { playerName = input.trim(); }
        }

        // Class selection — dynamically built from data, not hardcoded per class
        String[][] classData = {
            {"Knight",  "HP: 150 | DEF: 20 | ATK: 25 | SPD: 10",  "Tanky melee warrior. Spin & Slash skills."},
            {"Archer",  "HP: 100 | DEF: 10 | ATK: 20 | SPD: 15",  "Ranged marksman. Arrow barrage skills."},
            {"Rogue",   "HP:  90 | DEF:  8 | ATK: 22 | SPD: 25",  "Fast assassin. Backstabs & poison."},
            {"Wizard",  "HP:  80 | DEF:  5 | ATK: 30 | SPD: 12",  "Glass cannon. Devastating spells."},
            {"Priest",  "HP: 110 | DEF: 12 | ATK: 15 | SPD: 11",  "Healer. Can restore own HP mid-battle."},
            {"Paladin", "HP: 140 | DEF: 22 | ATK: 20 | SPD:  8",  "Tank-support. Holy strikes & divine shield."}
        };

        JPanel classPanel = new JPanel(new GridLayout(classData.length, 1, 0, 6));
        classPanel.setBackground(BG_PANEL);
        ButtonGroup bg = new ButtonGroup();

        for (String[] cd : classData) {
            JRadioButton rb = new JRadioButton(
                "<html><b style='color:#ddc040'>" + cd[0] + "</b>"
                + " &nbsp;<span style='color:#888888;font-size:10px'>" + cd[1] + "</span>"
                + "<br><span style='color:#aaaaaa;font-size:10px'>" + cd[2] + "</span></html>");
            rb.setBackground(BG_PANEL);
            rb.setForeground(TEXT_LIGHT);
            rb.setActionCommand(cd[0].toLowerCase());
            if (cd[0].equals("Knight")) rb.setSelected(true);
            bg.add(rb);
            classPanel.add(rb);
        }

        int result = JOptionPane.showConfirmDialog(this, classPanel,
                "Choose your class, " + playerName + ":",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        String chosenClass = bg.getSelection().getActionCommand();
        try {
            gameStatus.startGame(playerName, chosenClass);
        } catch (GameException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        log("═══════════════════════════════════════");
        log(" Welcome, " + playerName + " the " + capitalize(chosenClass) + "!");
        log(" The dungeon awaits. Good luck.");
        log("═══════════════════════════════════════");
        startBattle();
        cardLayout.show(mainPanel, "BATTLE");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // BATTLE PANEL (built once, refreshed each fight)
    // ═══════════════════════════════════════════════════════════════════════
    private JPanel createBattlePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBackground(BG_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        panel.add(createStatusBar(),   BorderLayout.NORTH);
        panel.add(createLogArea(),     BorderLayout.CENTER);
        panel.add(createActionBar(),   BorderLayout.SOUTH);

        return panel;
    }

    // ── Top status bar ────────────────────────────────────────────────────
    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout(10, 0));
        bar.setBackground(BG_DARK);

        // Player side
        JPanel pBox = new JPanel(new GridLayout(4, 1, 2, 2));
        pBox.setBackground(BG_PANEL);
        pBox.setBorder(titledBorder("YOUR HERO", ACCENT_GOLD));
        playerNameLabel  = styledLabel("---", ACCENT_GOLD, Font.BOLD, 15);
        playerHpLabel    = styledLabel("HP: --/--", TEXT_LIGHT, Font.PLAIN, 13);
        playerHpBarLabel = styledLabel("[                    ]", HP_GREEN, Font.PLAIN, 12);
        playerGoldLabel  = styledLabel("Gold: 0", ACCENT_GOLD, Font.PLAIN, 12);
        pBox.add(playerNameLabel); pBox.add(playerHpLabel);
        pBox.add(playerHpBarLabel); pBox.add(playerGoldLabel);

        // Floor + team info (center)
        JPanel midBox = new JPanel(new GridLayout(3, 1, 2, 2));
        midBox.setBackground(BG_PANEL);
        midBox.setBorder(titledBorder("STATUS", ACCENT_BLUE));
        floorLabel = styledLabel("Floor 1 / 10", ACCENT_BLUE, Font.BOLD, 14);
        teamLabel  = styledLabel("Party: Solo", TEXT_MUTED, Font.PLAIN, 12);
        JLabel tip = styledLabel("Earn gold · Shop after each floor", TEXT_MUTED, Font.ITALIC, 11);
        midBox.add(floorLabel); midBox.add(teamLabel); midBox.add(tip);

        // Enemy side
        JPanel eBox = new JPanel(new GridLayout(3, 1, 2, 2));
        eBox.setBackground(BG_PANEL);
        eBox.setBorder(titledBorder("ENEMY", ACCENT_RED));
        enemyNameLabel  = styledLabel("---", ACCENT_RED, Font.BOLD, 15);
        enemyHpLabel    = styledLabel("HP: --/--", TEXT_LIGHT, Font.PLAIN, 13);
        enemyHpBarLabel = styledLabel("[                    ]", HP_GREEN, Font.PLAIN, 12);
        eBox.add(enemyNameLabel); eBox.add(enemyHpLabel); eBox.add(enemyHpBarLabel);

        bar.add(pBox,   BorderLayout.WEST);
        bar.add(midBox, BorderLayout.CENTER);
        bar.add(eBox,   BorderLayout.EAST);
        return bar;
    }

    // ── Log area ──────────────────────────────────────────────────────────
    private JScrollPane createLogArea() {
        battleLog = new JTextArea();
        battleLog.setEditable(false);
        battleLog.setBackground(new Color(14, 14, 22));
        battleLog.setForeground(TEXT_LIGHT);
        battleLog.setFont(new Font("Monospaced", Font.PLAIN, 13));
        battleLog.setLineWrap(true);
        battleLog.setWrapStyleWord(true);
        battleLog.setMargin(new Insets(6, 8, 6, 8));
        JScrollPane scroll = new JScrollPane(battleLog);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(55, 55, 78), 1));
        return scroll;
    }

    // ── Action button bar ─────────────────────────────────────────────────
    private JPanel createActionBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        bar.setBackground(BG_DARK);

        basicBtn     = makeButton("⚔ Basic", 130, 50, 13);
        skill1Btn    = makeButton("Skill 1", 130, 50, 13);
        skill2Btn    = makeButton("Skill 2", 130, 50, 13);
        skill3Btn    = makeButton("Skill 3", 130, 50, 13);
        nextFloorBtn = makeButton("➜ Next Floor", 145, 50, 13);
        openShopBtn  = makeButton("🛒 Shop", 130, 50, 13);

        nextFloorBtn.setBackground(BTN_GREEN);
        openShopBtn.setBackground(BTN_GOLD);
        nextFloorBtn.setVisible(false);
        openShopBtn.setVisible(false);

        basicBtn.addActionListener(e  -> playerAction(0));
        skill1Btn.addActionListener(e -> playerAction(1));
        skill2Btn.addActionListener(e -> playerAction(2));
        skill3Btn.addActionListener(e -> playerAction(3));
        nextFloorBtn.addActionListener(e -> advanceFloor());
        openShopBtn.addActionListener(e -> openShop());

        bar.add(basicBtn); bar.add(skill1Btn); bar.add(skill2Btn); bar.add(skill3Btn);
        bar.add(openShopBtn); bar.add(nextFloorBtn);
        return bar;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SHOP
    // ═══════════════════════════════════════════════════════════════════════
    private void openShop() {
        Player p = gameStatus.getPlayer();
        Shop shop = gameStatus.getShop();
        List<String> available = shop.getAvailableCharacters(p.getRecruitedClasses());

        if (available.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All allies have been recruited! Your party is complete.",
                    "Shop", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Build shop panel dynamically from available characters
        JPanel shopPanel = new JPanel(new BorderLayout(0, 10));
        shopPanel.setBackground(BG_PANEL);

        JLabel goldInfo = new JLabel("Your Gold: " + p.getGold() + " 🪙");
        goldInfo.setForeground(ACCENT_GOLD);
        goldInfo.setFont(new Font("SansSerif", Font.BOLD, 14));
        shopPanel.add(goldInfo, BorderLayout.NORTH);

        JPanel listPanel = new JPanel(new GridLayout(available.size(), 1, 0, 6));
        listPanel.setBackground(BG_PANEL);
        ButtonGroup bg = new ButtonGroup();
        JRadioButton[] radios = new JRadioButton[available.size()];

        for (int i = 0; i < available.size(); i++) {
            String cls   = available.get(i);
            int price    = shop.getPrice(cls);
            boolean canAfford = p.getGold() >= price;
            String color = canAfford ? "#ddc040" : "#aa4444";
            JRadioButton rb = new JRadioButton(
                "<html><b style='color:" + color + "'>" + cls + "</b>"
                + "  —  <span style='color:#ddc040'>" + price + " gold</span>"
                + (canAfford ? "" : "  <span style='color:#aa4444'>(Not enough gold)</span>")
                + "</html>");
            rb.setBackground(BG_PANEL);
            rb.setForeground(TEXT_LIGHT);
            rb.setActionCommand(cls);
            rb.setEnabled(canAfford);
            if (i == 0 && canAfford) rb.setSelected(true);
            bg.add(rb);
            listPanel.add(rb);
            radios[i] = rb;
        }
        shopPanel.add(listPanel, BorderLayout.CENTER);

        // Make sure one is selected if any are affordable
        boolean anyAffordable = available.stream().anyMatch(c -> p.getGold() >= shop.getPrice(c));
        if (!anyAffordable) {
            JOptionPane.showMessageDialog(this,
                    "You don't have enough gold for any ally!\nEarn more by clearing floors.",
                    "Shop", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int result = JOptionPane.showConfirmDialog(this, shopPanel,
                "Alliance Shop  —  Recruit an Ally",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION || bg.getSelection() == null) return;

        String chosen = bg.getSelection().getActionCommand();
        int price     = shop.getPrice(chosen);

        // Ask for ally's name
        String allyName = JOptionPane.showInputDialog(this,
                "Name your new " + chosen + ":", "Name Your Ally", JOptionPane.QUESTION_MESSAGE);
        if (allyName == null || allyName.trim().isEmpty()) allyName = chosen;

        if (!p.spendGold(price)) {
            JOptionPane.showMessageDialog(this, "Not enough gold!", "Shop", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Player newAlly = shop.buyCharacter(chosen, allyName.trim(), price + 1 /* already spent */);
        if (newAlly != null) {
            p.recruitCharacter(newAlly);
            log("🤝 " + allyName + " the " + chosen + " joined your party! (-" + price + " gold)");
            refreshUI();
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // BATTLE LOGIC
    // ═══════════════════════════════════════════════════════════════════════
    private void startBattle() {
        currentEnemy = gameStatus.getCurrentFloor().generateMonster();
        int floor    = gameStatus.getCurrentFloor().getFloorNumber();
        log("\n--- Floor " + floor + " / 10 ---");
        log("A " + currentEnemy.getName() + " blocks your path!");
        log("  " + getEnemyDesc(currentEnemy));
        refreshUI();
        setActionButtonsEnabled(true);
        nextFloorBtn.setVisible(false);
        openShopBtn.setVisible(false);
        updateSkillLabels();
    }

    private String getEnemyDesc(Enemy e) {
        if (e instanceof dungeon.entities.enemies.Orc)
            return "(Slow brute — high HP & defense, hits hard)";
        if (e instanceof dungeon.entities.enemies.Goblin)
            return "(Fast pest — low HP, can strike twice)";
        if (e instanceof dungeon.entities.enemies.Demon)
            return "(Dark mage — powerful spells, low defense)";
        if (e instanceof dungeon.entities.enemies.FinalBoss)
            return "(THE DUNGEON LORD — ultimate power!)";
        return "";
    }

    private void updateSkillLabels() {
        Player p = gameStatus.getPlayer();
        // Skill names per class — matches actual method names
        Map<String, String[]> skillNames = Map.of(
            "Knight",  new String[]{"Spin Attack",    "Splash Attack", "Big Slash"},
            "Archer",  new String[]{"Raining Arrows", "Big Arrow",     "Rapid Shot"},
            "Rogue",   new String[]{"Backstab",       "Poison Dart",   "Shadow Clone"},
            "Wizard",  new String[]{"Fireball",       "Ice Shower",    "Explosion"},
            "Priest",  new String[]{"Holy Light",     "Smite",         "Divine Blessing (Heal)"},
            "Paladin", new String[]{"Holy Strike",    "Consecration",  "Divine Shield (Heal)"}
        );
        String cls = p.getClass().getSimpleName();
        String[] names = skillNames.getOrDefault(cls, new String[]{"Skill 1","Skill 2","Skill 3"});
        skill1Btn.setText(names[0]);
        skill2Btn.setText(names[1]);
        skill3Btn.setText(names[2]);
    }

    /** 0 = basic attack, 1/2/3 = skills */
    private void playerAction(int action) {
        Player p = gameStatus.getPlayer();

        switch (action) {
            case 0:
                log("> " + p.getName() + " performs a Basic Attack!");
                p.basicAttack(currentEnemy);
                break;
            case 1:
                log("> " + p.getName() + " uses " + skill1Btn.getText() + "!");
                p.useSkillOne(currentEnemy);
                break;
            case 2:
                log("> " + p.getName() + " uses " + skill2Btn.getText() + "!");
                p.useSkillTwo(currentEnemy);
                break;
            case 3:
                log("> " + p.getName() + " uses " + skill3Btn.getText() + "!");
                p.useSkillThree(currentEnemy);
                break;
        }
        log("  " + currentEnemy.getName() + " HP: "
                + Math.max(currentEnemy.getHp(), 0) + "/" + currentEnemy.getMaxHp());
        refreshUI();

        if (currentEnemy.isDead()) {
            onEnemyDefeated();
            return;
        }

        // Enemy counter-attack
        log("  ↩ " + currentEnemy.getName() + " counter-attacks!");
        currentEnemy.basicAttack(p);
        log("  " + p.getName() + " HP: " + Math.max(p.getHp(), 0) + "/" + p.getMaxHp());
        refreshUI();

        if (p.isDead()) {
            onPlayerDefeated();
        }
    }

    private void onEnemyDefeated() {
        Player p  = gameStatus.getPlayer();
        int floor = gameStatus.getCurrentFloor().getFloorNumber();
        log("✓ " + currentEnemy.getName() + " defeated!");
        gameStatus.floorCleared();
        log("  +" + gameStatus.getGoldRewardPerFloor() + " gold earned! Total: " + p.getGold());
        setActionButtonsEnabled(false);
        refreshUI();

        if (floor >= 10) {
            log("\n🏆 THE DUNGEON LORD HAS FALLEN!");
            log("   " + p.getName() + " rescues the princess. VICTORY!");
            JOptionPane.showMessageDialog(this,
                    p.getName() + " has conquered the dungeon!\nThe princess is saved! YOU WIN!",
                    "Victory!", JOptionPane.INFORMATION_MESSAGE);
        } else {
            openShopBtn.setVisible(true);
            nextFloorBtn.setVisible(true);
            log("  Open the Shop to recruit allies, or descend to Floor " + (floor + 1) + ".");
        }
    }

    private void onPlayerDefeated() {
        log("\n💀 " + gameStatus.getPlayer().getName() + " has fallen. GAME OVER.");
        setActionButtonsEnabled(false);
        int retry = JOptionPane.showConfirmDialog(this,
                "Your hero has been defeated.\nReturn to the main menu?",
                "Game Over", JOptionPane.YES_NO_OPTION);
        if (retry == JOptionPane.YES_OPTION) {
            cardLayout.show(mainPanel, "MENU");
        }
    }

    private void advanceFloor() {
        gameStatus.nextFloor();
        nextFloorBtn.setVisible(false);
        openShopBtn.setVisible(false);
        startBattle();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UI HELPERS
    // ═══════════════════════════════════════════════════════════════════════
    private void refreshUI() {
        Player p = gameStatus.getPlayer();
        int floor = gameStatus.getCurrentFloor().getFloorNumber();

        playerNameLabel.setText(p.getName() + " [" + p.getClass().getSimpleName() + "]");
        playerHpLabel.setText("HP: " + Math.max(p.getHp(), 0) + " / " + p.getMaxHp());
        playerHpBarLabel.setText(buildHpBar(p.getHp(), p.getMaxHp()));
        playerHpBarLabel.setForeground(hpColor(p.getHp(), p.getMaxHp()));
        playerGoldLabel.setText("Gold: " + p.getGold() + " 🪙  |  Party: "
                + (1 + p.getTeam().size()) + " member(s)");

        floorLabel.setText("Floor " + floor + " / 10");

        int teamSize = p.getTeam().size();
        if (teamSize == 0) {
            teamLabel.setText("Party: Solo");
        } else {
            StringBuilder sb = new StringBuilder("Party: " + p.getName());
            for (Player ally : p.getTeam()) sb.append(", ").append(ally.getName());
            teamLabel.setText(sb.toString());
        }

        if (currentEnemy != null) {
            enemyNameLabel.setText(currentEnemy.getName());
            enemyHpLabel.setText("HP: " + Math.max(currentEnemy.getHp(), 0)
                    + " / " + currentEnemy.getMaxHp());
            enemyHpBarLabel.setText(buildHpBar(currentEnemy.getHp(), currentEnemy.getMaxHp()));
            enemyHpBarLabel.setForeground(hpColor(currentEnemy.getHp(), currentEnemy.getMaxHp()));
        }
    }

    private Color hpColor(int hp, int max) {
        float ratio = (float) hp / max;
        if (ratio < 0.25f) return HP_RED;
        if (ratio < 0.5f)  return HP_YELLOW;
        return HP_GREEN;
    }

    private String buildHpBar(int hp, int max) {
        int filled = (int) Math.round(20.0 * Math.max(hp, 0) / max);
        return "[" + "█".repeat(filled) + "░".repeat(20 - filled) + "]";
    }

    private void setActionButtonsEnabled(boolean on) {
        basicBtn.setEnabled(on);
        skill1Btn.setEnabled(on);
        skill2Btn.setEnabled(on);
        skill3Btn.setEnabled(on);
    }

    private void log(String msg) {
        battleLog.append(msg + "\n");
        battleLog.setCaretPosition(battleLog.getDocument().getLength());
    }

    private JButton makeButton(String text, int w, int h, int fontSize) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(w, h));
        btn.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        btn.setBackground(BTN_NORMAL);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(new Color(80, 80, 110), 1));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            final Color orig = btn.getBackground();
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(BTN_HOVER); }
            public void mouseExited(java.awt.event.MouseEvent e)  { btn.setBackground(orig); }
        });
        return btn;
    }

    private JLabel styledLabel(String text, Color color, int style, int size) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(color);
        lbl.setFont(new Font("SansSerif", style, size));
        return lbl;
    }

    private TitledBorder titledBorder(String title, Color color) {
        TitledBorder tb = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(color, 1), title);
        tb.setTitleColor(color);
        tb.setTitleFont(new Font("SansSerif", Font.BOLD, 11));
        return tb;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }
}
