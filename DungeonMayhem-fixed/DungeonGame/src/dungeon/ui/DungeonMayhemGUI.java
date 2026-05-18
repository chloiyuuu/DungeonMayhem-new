package dungeon.ui;

import dungeon.entities.Player;
import dungeon.entities.Enemy;
import dungeon.exceptions.GameException;
import dungeon.game.GameStatus;
import dungeon.shop.Shop;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class DungeonMayhemGUI extends JFrame {

    private final GameStatus gameStatus;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;

    private JPanel classSelectPanel;
    private String selectedClass = "Knight";
    private String heroName = "Hero";

    private JPanel partyPanel;
    private JLabel activeHeroNameLabel, activeHeroHpText;
    private HealthBar activeHeroHpBar;

    private JLabel enemyNameLabel, floorLabel;
    private HealthBar enemyHpBar;

    private JTextPane battleLog;
    private JButton basicBtn, skill1Btn, skill2Btn, skill3Btn;
    private JButton nextFloorBtn, openShopBtn;

    private Enemy currentEnemy;
    private Player activePlayer;
    private List<JButton> partyButtons = new ArrayList<>();

    public DungeonMayhemGUI(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        this.cardLayout = new CardLayout();
        this.mainPanel  = new JPanel(cardLayout);

        setupFrame();
        interceptSystemOut();
        initializeScreens();
    }

    private void setupFrame() {
        setTitle("DUNGEON MAYHEM");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setBackground(GUIStyle.BG_MAIN);
    }

    private void interceptSystemOut() {
        PrintStream interceptor = new PrintStream(new OutputStream() {
            private StringBuilder buffer = new StringBuilder();

            @Override
            public void write(int b) {
                if (b == '\n') {
                    String line = buffer.toString();
                    buffer.setLength(0);
                    SwingUtilities.invokeLater(() -> addLogMessage(line));
                } else {
                    buffer.append((char) b);
                }
            }
        });
        System.setOut(interceptor);
    }

    private void addLogMessage(String line) {
        if (battleLog != null) {
            Color c = GUIStyle.TEXT_MAIN;
            if (line.contains("Remaining HP:") || (line.contains("took") && line.contains("damage"))) {
                line = "   💥 " + line;
                c = GUIStyle.ACCENT_RED;
            } else if (line.contains("attacks!")) {
                line = "\n⚔ " + line;
                c = GUIStyle.ACCENT_ORANGE;
            } else if (line.contains("casts")) {
                line = "\n✨ " + line;
                c = GUIStyle.ACCENT_PURPLE;
            } else if (line.contains("heals") || line.contains("restored") || line.contains("restores")) {
                c = GUIStyle.ACCENT_GREEN;
            } else if (line.contains("defeated") || line.contains("fallen")) {
                c = GUIStyle.TEXT_MUTED;
            } else if (line.contains("FLOOR") || line.contains("appears!")) {
                c = GUIStyle.ACCENT_BLUE;
            }

            javax.swing.text.StyledDocument doc = battleLog.getStyledDocument();
            javax.swing.text.Style style = battleLog.addStyle("ColorStyle", null);
            javax.swing.text.StyleConstants.setForeground(style, c);

            try {
                doc.insertString(doc.getLength(), line + "\n", style);
            } catch (Exception e) {}
        }
    }

    private void initializeScreens() {
        mainPanel.setBackground(GUIStyle.BG_MAIN);
        mainPanel.add(createMainMenu(),    "MENU");
        mainPanel.add(createClassSelect(), "CLASS_SELECT");
        mainPanel.add(createBattlePanel(), "BATTLE");
        add(mainPanel);
        cardLayout.show(mainPanel, "MENU");
    }

    private JPanel createMainMenu() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(GUIStyle.BG_MAIN);
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = GUIStyle.createStyledLabel("DUNGEON MAYHEM", GUIStyle.TEXT_MAIN, GUIStyle.FONT_TITLE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.insets = new Insets(0, 0, 10, 0);
        panel.add(title, gbc);

        JLabel subtitle = GUIStyle.createStyledLabel("10 Floors · Infinite Glory · No Mercy", GUIStyle.TEXT_MUTED, new Font("Segoe UI", Font.ITALIC, 18));
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 50, 0);
        panel.add(subtitle, gbc);

        JButton startBtn = GUIStyle.createStyledButton("ENTER THE DUNGEON", true);
        startBtn.setPreferredSize(new Dimension(250, 60));
        startBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        startBtn.addActionListener(e -> {
            try {
                String name = JOptionPane.showInputDialog(this, "What is your Hero's name?", "Identity", JOptionPane.QUESTION_MESSAGE);
                if (name == null) return;

                name = name.trim();
                if (name.isEmpty()) {
                    throw new dungeon.exceptions.GameException("A hero must have a name!");
                }
                if (name.contains(" ")) {
                    throw new dungeon.exceptions.GameException("Spaces are not allowed in the hero's name!");
                }

                heroName = name;
                cardLayout.show(mainPanel, "CLASS_SELECT");
            } catch (dungeon.exceptions.GameException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(startBtn, gbc);

        return panel;
    }

    private JPanel createClassSelect() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(GUIStyle.BG_MAIN);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel title = GUIStyle.createStyledLabel("Choose Your Destiny", GUIStyle.TEXT_MAIN, new Font("Segoe UI", Font.BOLD, 32));
        panel.add(title, BorderLayout.NORTH);

        JPanel splitPane = new JPanel(new BorderLayout(30, 0));
        splitPane.setOpaque(false);

        JPanel classList = new JPanel(new GridLayout(6, 1, 10, 10));
        classList.setOpaque(false);
        classList.setPreferredSize(new Dimension(250, 0));

        String[] classes = {"Knight", "Archer", "Rogue", "Wizard", "Priest", "Paladin"};
        ButtonGroup bg = new ButtonGroup();

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setOpaque(false);

        for (String cls : classes) {
            JToggleButton btn = GUIStyle.createStyledToggleButton(cls);
            btn.setFont(GUIStyle.FONT_HEADER); // keep header font
            if (cls.equals("Knight")) btn.setSelected(true);
            btn.addActionListener(e -> {
                selectedClass = cls;
                updateClassDetails(detailsPanel);
            });
            bg.add(btn);
            classList.add(btn);
        }

        updateClassDetails(detailsPanel);

        splitPane.add(classList, BorderLayout.WEST);
        splitPane.add(detailsPanel, BorderLayout.CENTER);
        panel.add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setOpaque(false);
        JButton confirmBtn = GUIStyle.createStyledButton("Confirm Selection", true);
        confirmBtn.setPreferredSize(new Dimension(200, 50));
        confirmBtn.addActionListener(e -> startGame());
        bottomPanel.add(confirmBtn);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateClassDetails(JPanel detailsPanel) {
        detailsPanel.removeAll();
        detailsPanel.setBackground(GUIStyle.BG_PANEL);
        detailsPanel.setBorder(GUIStyle.createCardBorder());

        Map<String, int[]> stats = Map.of(
                "Knight",  new int[]{120, 15, 10, 5},
                "Archer",  new int[]{80,  20, 5,  15},
                "Rogue",   new int[]{70,  25, 3,  20},
                "Wizard",  new int[]{60,  30, 2,  12},
                "Priest",  new int[]{110, 15, 12, 11},
                "Paladin", new int[]{140, 18, 15, 8}
        );
        Map<String, String> desc = Map.of(
                "Knight",  "A heavily armored warrior with high HP and defense. Melee combatant.",
                "Archer",  "A ranged DPS expert with high speed and strong attacks but low defense.",
                "Rogue",   "A fast assassin capable of dealing massive damage quickly.",
                "Wizard",  "A glass cannon relying on powerful magic spells.",
                "Priest",  "A divine support who can restore HP mid-battle. Essential for survival.",
                "Paladin", "A holy tank who balances strong defense with sacred attacks."
        );

        int[] s = stats.get(selectedClass);

        JPanel headerInfo = new JPanel(new GridLayout(2,1));
        headerInfo.setOpaque(false);
        headerInfo.add(GUIStyle.createStyledLabel(selectedClass, GUIStyle.TEXT_MAIN, GUIStyle.FONT_TITLE));

        JTextArea descArea = new JTextArea(desc.get(selectedClass));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setFont(GUIStyle.FONT_BODY);
        descArea.setForeground(GUIStyle.TEXT_MUTED);
        headerInfo.add(descArea);

        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(30, 0, 0, 0));

        statsGrid.add(createStatBox("HP", s[0], 150, GUIStyle.ACCENT_GREEN));
        statsGrid.add(createStatBox("Attack", s[1], 40, GUIStyle.ACCENT_RED));
        statsGrid.add(createStatBox("Defense", s[2], 20, GUIStyle.ACCENT_BLUE));
        statsGrid.add(createStatBox("Speed", s[3], 30, GUIStyle.ACCENT_PURPLE));

        detailsPanel.add(headerInfo, BorderLayout.NORTH);
        detailsPanel.add(statsGrid, BorderLayout.CENTER);

        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private JPanel createStatBox(String name, int value, int max, Color color) {
        JPanel box = new JPanel(new BorderLayout(5, 5));
        box.setBackground(GUIStyle.BTN_NORMAL);
        box.setBorder(new EmptyBorder(15, 15, 15, 15));

        JLabel nameLbl = GUIStyle.createStyledLabel(name, GUIStyle.TEXT_MUTED, GUIStyle.FONT_BODY);
        JLabel valLbl = GUIStyle.createStyledLabel(String.valueOf(value), color, new Font("Segoe UI", Font.BOLD, 24));

        HealthBar bar = new HealthBar(value, max, color, false, "");

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(nameLbl, BorderLayout.NORTH);
        top.add(valLbl, BorderLayout.CENTER);

        box.add(top, BorderLayout.CENTER);
        box.add(bar, BorderLayout.SOUTH);
        return box;
    }

    private void startGame() {
        try {
            gameStatus.startGame(heroName, selectedClass);
            startNewBattle();
            cardLayout.show(mainPanel, "BATTLE");
        } catch (GameException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createBattlePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(GUIStyle.BG_MAIN);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        panel.add(createHeaderPanel(),   BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new BorderLayout(15, 0));
        centerWrapper.setOpaque(false);
        centerWrapper.add(createPartyPanel(), BorderLayout.WEST);
        centerWrapper.add(createLogPanel(),   BorderLayout.CENTER);

        panel.add(centerWrapper,         BorderLayout.CENTER);
        panel.add(createActionPanel(),   BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new GridLayout(1, 3, 15, 0));
        header.setOpaque(false);

        JPanel playerCard = createStatCard();
        activeHeroNameLabel = GUIStyle.createStyledLabel("Hero", GUIStyle.TEXT_MAIN, GUIStyle.FONT_HEADER);
        activeHeroHpBar = new HealthBar(100, 100, GUIStyle.ACCENT_GREEN, false, "");
        activeHeroHpText = GUIStyle.createStyledLabel("100/100 HP", GUIStyle.TEXT_MUTED, GUIStyle.FONT_BODY);
        playerCard.add(activeHeroNameLabel);
        playerCard.add(activeHeroHpBar);
        playerCard.add(activeHeroHpText);

        JPanel floorCard = createStatCard();
        floorLabel = GUIStyle.createStyledLabel("FLOOR 1", GUIStyle.ACCENT_BLUE, GUIStyle.FONT_HEADER);
        JLabel vsLabel = GUIStyle.createStyledLabel("VS", GUIStyle.TEXT_MUTED, GUIStyle.FONT_BODY);
        floorCard.add(floorLabel);
        floorCard.add(vsLabel);

        JPanel enemyCard = createStatCard();
        enemyNameLabel = GUIStyle.createStyledLabel("Enemy", GUIStyle.ACCENT_RED, GUIStyle.FONT_HEADER);
        enemyHpBar = new HealthBar(100, 100, GUIStyle.ACCENT_RED, true, "HP ");
        enemyCard.add(enemyNameLabel);
        enemyCard.add(enemyHpBar);

        header.add(playerCard);
        header.add(floorCard);
        header.add(enemyCard);

        return header;
    }

    private JPanel createStatCard() {
        JPanel card = new JPanel(new GridLayout(3, 1, 5, 5));
        card.setBackground(GUIStyle.BG_PANEL);
        card.setBorder(GUIStyle.createCardBorder());
        return card;
    }

    private JPanel createPartyPanel() {
        partyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        partyPanel.setPreferredSize(new Dimension(180, 0));
        partyPanel.setBackground(GUIStyle.BG_PANEL);
        partyPanel.setBorder(GUIStyle.createCardBorder());

        JLabel title = GUIStyle.createStyledLabel("Your Party", GUIStyle.TEXT_MAIN, GUIStyle.FONT_LABEL);
        partyPanel.add(title);

        return partyPanel;
    }

    private JScrollPane createLogPanel() {
        battleLog = new JTextPane();
        battleLog.setEditable(false);
        battleLog.setBackground(GUIStyle.BG_PANEL);
        battleLog.setForeground(GUIStyle.TEXT_MAIN);
        battleLog.setFont(GUIStyle.FONT_MONO);
        battleLog.setMargin(new Insets(15, 15, 15, 15));

        DefaultCaret caret = (DefaultCaret) battleLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scroll = new JScrollPane(battleLog);
        scroll.setBorder(GUIStyle.createCardBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setOpaque(false);

        basicBtn = GUIStyle.createStyledButton("Attack", false);
        skill1Btn = GUIStyle.createStyledButton("Skill 1", false);
        skill2Btn = GUIStyle.createStyledButton("Skill 2", false);
        skill3Btn = GUIStyle.createStyledButton("Skill 3", true);

        openShopBtn = GUIStyle.createStyledButton("Visit Shop", false);
        openShopBtn.setVisible(false);

        nextFloorBtn = GUIStyle.createStyledButton("Descend", true);
        nextFloorBtn.setBackground(GUIStyle.ACCENT_GREEN);
        nextFloorBtn.setVisible(false);

        basicBtn.addActionListener(e -> handlePlayerAction(0));
        skill1Btn.addActionListener(e -> handlePlayerAction(1));
        skill2Btn.addActionListener(e -> handlePlayerAction(2));
        skill3Btn.addActionListener(e -> handlePlayerAction(3));
        openShopBtn.addActionListener(e -> openShop());
        nextFloorBtn.addActionListener(e -> advanceFloor());

        panel.add(basicBtn);
        panel.add(skill1Btn);
        panel.add(skill2Btn);
        panel.add(skill3Btn);
        panel.add(openShopBtn);
        panel.add(nextFloorBtn);

        return panel;
    }

    private void startNewBattle() {
        currentEnemy = gameStatus.getCurrentFloor().generateMonster();
        int fNum = gameStatus.getCurrentFloor().getFloorNumber();

        battleLog.setText("");
        System.out.println("--- FLOOR " + fNum + " / 10 ---");
        System.out.println("A wild " + currentEnemy.getName() + " appears!");

        setActivePlayer(gameStatus.getPlayer());
        refreshPartyList();

        refreshUI();
        setActionButtonsEnabled(true);
        nextFloorBtn.setVisible(false);
        openShopBtn.setVisible(false);
    }

    private void refreshPartyList() {
        partyPanel.removeAll();
        partyButtons.clear();

        JLabel title = GUIStyle.createStyledLabel("Party Roster", GUIStyle.TEXT_MUTED, GUIStyle.FONT_LABEL);
        partyPanel.add(title);

        addPartyMemberButton(gameStatus.getPlayer());
        for (Player ally : gameStatus.getPlayer().getTeam()) {
            addPartyMemberButton(ally);
        }

        partyPanel.revalidate();
        partyPanel.repaint();
    }

    private void addPartyMemberButton(Player p) {
        String name = p.getName() + " (" + p.getHp() + "/" + p.getMaxHp() + ")";
        JButton btn = GUIStyle.createStyledButton(name, p == activePlayer);
        btn.setPreferredSize(new Dimension(160, 40));
        if (p.isDead()) {
            btn.setEnabled(false);
            btn.setText("☠ " + p.getName());
            btn.setBackground(GUIStyle.BG_MAIN);
            btn.setForeground(GUIStyle.TEXT_MUTED);
        } else {
            btn.addActionListener(e -> setActivePlayer(p));
        }
        partyButtons.add(btn);
        partyPanel.add(btn);
    }

    private void setActivePlayer(Player p) {
        this.activePlayer = p;
        updateSkillButtons();
        refreshPartyList();
        refreshUI();
    }

    private void handlePlayerAction(int actionType) {
        if (activePlayer.isDead()) {
            JOptionPane.showMessageDialog(this, "This hero has fallen!");
            return;
        }

        executePlayerMove(activePlayer, actionType);

        if (currentEnemy.isDead()) {
            handleVictory();
        } else {
            executeEnemyTurn(activePlayer);
        }

        refreshUI();
        refreshPartyList();
    }

    private void executePlayerMove(Player p, int type) {
        switch (type) {
            case 0: p.basicAttack(currentEnemy); break;
            case 1: p.useSkillOne(currentEnemy); break;
            case 2: p.useSkillTwo(currentEnemy); break;
            case 3: p.useSkillThree(currentEnemy); break;
        }
    }

    private void executeEnemyTurn(Player p) {
        System.out.println(currentEnemy.getName() + " retaliates against " + p.getName() + "!");
        currentEnemy.basicAttack(p);

        if (p.isDead()) {
            System.out.println("☠ " + p.getName() + " has fallen.");
            for (Player ally : gameStatus.getPlayer().getTeam()) {
                if (!ally.isDead()) {
                    setActivePlayer(ally);
                    return;
                }
            }
            if (!gameStatus.getPlayer().isDead()) {
                setActivePlayer(gameStatus.getPlayer());
                return;
            }
            handleGameOver();
        }
    }

    private void handleVictory() {
        System.out.println("✓ Enemy Defeated!");
        gameStatus.floorCleared();
        setActionButtonsEnabled(false);

        int floor = gameStatus.getCurrentFloor().getFloorNumber();
        if (floor >= 10) {
            showVictoryMessage();
        } else {
            openShopBtn.setVisible(true);
            nextFloorBtn.setVisible(true);
        }
    }

    private void handleGameOver() {
        setActionButtonsEnabled(false);
        if (JOptionPane.showConfirmDialog(this, "Entire party defeated... Try again?", "SLAIN", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            cardLayout.show(mainPanel, "MENU");
        }
    }

    private void advanceFloor() {
        gameStatus.nextFloor();
        startNewBattle();
    }

    private void openShop() {
        Player p = gameStatus.getPlayer();
        Shop shop = gameStatus.getShop();

        String chosen = showShopDialog(p);
        if (chosen != null) {
            recruitAlly(p, shop, chosen);
        }
    }

    private String showShopDialog(Player p) {
        Shop shop = gameStatus.getShop();
        List<String> recruited = p.getRecruitedClasses();
        int gold = p.getGold();
        boolean isPartyFull = p.getTeam().size() >= 4;

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(GUIStyle.BG_PANEL);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        headerPanel.setOpaque(false);
        JLabel titleLabel = GUIStyle.createStyledLabel("Tavern Recruits", GUIStyle.TEXT_MAIN, GUIStyle.FONT_HEADER);
        JLabel goldLabel = GUIStyle.createStyledLabel("💸 Gold: " + gold, GUIStyle.ACCENT_ORANGE, GUIStyle.FONT_HEADER);
        headerPanel.add(titleLabel);
        headerPanel.add(goldLabel);

        JPanel topContainer = new JPanel(new BorderLayout());
        topContainer.setOpaque(false);
        topContainer.add(headerPanel, BorderLayout.CENTER);

        if (isPartyFull) {
            JLabel warningLabel = GUIStyle.createStyledLabel("Party is full! Max 4 recruits.", GUIStyle.ACCENT_RED, GUIStyle.FONT_LABEL);
            warningLabel.setHorizontalAlignment(SwingConstants.CENTER);
            topContainer.add(warningLabel, BorderLayout.SOUTH);
        }
        mainPanel.add(topContainer, BorderLayout.NORTH);

        // List of recruits
        JPanel gridPanel = new JPanel(new GridLayout(shop.getAllPrices().size(), 1, 10, 10));
        gridPanel.setOpaque(false);

        ButtonGroup group = new ButtonGroup();
        for (Map.Entry<String, Integer> entry : shop.getAllPrices().entrySet()) {
            String cls = entry.getKey();
            int cost = entry.getValue();
            boolean isRecruited = recruited.contains(cls);

            JPanel itemPanel = new JPanel(new BorderLayout(10, 10));
            itemPanel.setBackground(GUIStyle.BG_MAIN);
            itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    GUIStyle.createCardBorder(),
                    new EmptyBorder(5, 10, 5, 10)
            ));

            String status = cost + " Gold";
            if (isRecruited) status = "Already Recruited";
            else if (isPartyFull) status = "Party Full";
            else if (gold < cost) status = cost + " Gold (Insufficient)";

            boolean canRecruit = !isRecruited && !isPartyFull && gold >= cost;

            JRadioButton rb = new JRadioButton(cls);
            rb.setFont(GUIStyle.FONT_HEADER);
            rb.setForeground(canRecruit ? GUIStyle.TEXT_MAIN : GUIStyle.TEXT_MUTED);
            rb.setBackground(GUIStyle.BG_MAIN);
            rb.setEnabled(canRecruit);
            rb.setActionCommand(cls);
            group.add(rb);

            JLabel statusLabel = GUIStyle.createStyledLabel(status,
                    canRecruit ? GUIStyle.ACCENT_ORANGE : GUIStyle.TEXT_MUTED,
                    GUIStyle.FONT_BODY);

            itemPanel.add(rb, BorderLayout.WEST);
            itemPanel.add(statusLabel, BorderLayout.EAST);
            gridPanel.add(itemPanel);
        }

        mainPanel.add(gridPanel, BorderLayout.CENTER);

        UIManager.put("Panel.background", GUIStyle.BG_PANEL);
        UIManager.put("OptionPane.background", GUIStyle.BG_PANEL);
        int res = JOptionPane.showConfirmDialog(this, mainPanel, "Tavern", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return (res == JOptionPane.OK_OPTION && group.getSelection() != null) ? group.getSelection().getActionCommand() : null;
    }

    private void recruitAlly(Player p, Shop shop, String cls) {
        String allyName = JOptionPane.showInputDialog(this, "What shall we call this " + cls + "?");
        if (allyName == null || allyName.trim().isEmpty()) allyName = cls;

        int cost = shop.getPrice(cls);
        if (p.spendGold(cost)) {
            Player ally = shop.buyCharacter(cls, allyName.trim(), cost + 1);
            p.recruitCharacter(ally);
            System.out.println("🤝 " + allyName + " the " + cls + " joined the party!");
            refreshPartyList();
        }
    }

    private void refreshUI() {
        if (activePlayer != null) {
            activeHeroNameLabel.setText(activePlayer.getName() + " [" + activePlayer.getClass().getSimpleName() + "]");
            activeHeroHpBar.updateHealth(activePlayer.getHp(), activePlayer.getMaxHp());
            activeHeroHpText.setText(activePlayer.getHp() + " / " + activePlayer.getMaxHp() + " HP");
        }

        int floor = gameStatus.getCurrentFloor().getFloorNumber();
        floorLabel.setText("FLOOR " + floor);

        if (currentEnemy != null) {
            enemyNameLabel.setText(currentEnemy.getName());
            enemyHpBar.updateHealth(currentEnemy.getHp(), currentEnemy.getMaxHp());
        }
    }

    private void updateSkillButtons() {
        if (activePlayer == null) return;
        String cls = activePlayer.getClass().getSimpleName();
        Map<String, String[]> map = Map.of(
                "Knight",  new String[]{"Spin Attack", "Splash", "Mega Slash"},
                "Archer",  new String[]{"Rain Arrows", "Volt",   "Rapid"},
                "Rogue",   new String[]{"Backstab",    "Poison", "Shadow"},
                "Wizard",  new String[]{"Fireball",    "Ice",    "Explode"},
                "Priest",  new String[]{"Heal Light",  "Smite",  "Blessing"},
                "Paladin", new String[]{"Holy Strike", "Consec", "Shield"}
        );
        String[] names = map.getOrDefault(cls, new String[]{"S1", "S2", "S3"});
        skill1Btn.setText(names[0]);
        skill2Btn.setText(names[1]);
        skill3Btn.setText(names[2]);
    }

    private void setActionButtonsEnabled(boolean enabled) {
        basicBtn.setEnabled(enabled);
        skill1Btn.setEnabled(enabled);
        skill2Btn.setEnabled(enabled);
        skill3Btn.setEnabled(enabled);
    }

    private void showVictoryMessage() {
        JOptionPane.showMessageDialog(this, "The Dungeon Lord has fallen. You are the champion!", "IMMORTAL", JOptionPane.INFORMATION_MESSAGE);
        cardLayout.show(mainPanel, "MENU");
    }
}
