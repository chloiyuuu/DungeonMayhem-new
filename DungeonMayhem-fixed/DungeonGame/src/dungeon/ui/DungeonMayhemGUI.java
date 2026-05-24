package dungeon.ui;

import dungeon.entities.Player;
import dungeon.entities.Enemy;
import dungeon.exceptions.GameException;
import dungeon.game.GameStatus;
import dungeon.interfaces.TransactionSystem;

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
    private JLabel activeHeroNameLabel, activeHeroHpText, activeHeroMpText;
    private HealthBar activeHeroHpBar, activeHeroMpBar;

    private JLabel enemyNameLabel, floorLabel;
    private HealthBar enemyHpBar, enemyMpBar;

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
            } else if (line.contains("mana") || line.contains("MP")) {
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
                    throw new dungeon.exceptions.InvalidNameException("A hero must have a name!");
                }
                if (name.contains(" ")) {
                    throw new dungeon.exceptions.InvalidNameException("Spaces are not allowed in the hero's name!");
                }

                heroName = name;
                cardLayout.show(mainPanel, "CLASS_SELECT");
            } catch (dungeon.exceptions.InvalidNameException ex) {
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

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = GUIStyle.createStyledLabel("CHARACTER SELECT", GUIStyle.TEXT_MAIN, new Font("Segoe UI", Font.BOLD, 36));
        headerPanel.add(title, BorderLayout.WEST);

        // Add a back button if needed, but let's just stick to the title.
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel splitPane = new JPanel(new BorderLayout(30, 0));
        splitPane.setOpaque(false);

        // LEFT: Class Grid
        JPanel classListWrapper = new JPanel(new BorderLayout());
        classListWrapper.setOpaque(false);
        classListWrapper.setBorder(new EmptyBorder(0, 0, 0, 20));

        JPanel classList = new JPanel(new GridLayout(3, 1, 0, 20));
        classList.setOpaque(false);
        classList.setPreferredSize(new Dimension(250, 250));

        String[] classes = {"Knight", "Archer", "Rogue"};
        ButtonGroup bg = new ButtonGroup();

        // To hold the image placeholder
        JLabel portraitLabel = new JLabel();
        portraitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        portraitLabel.setVerticalAlignment(SwingConstants.CENTER);
        portraitLabel.setPreferredSize(new Dimension(300, 500));

        JPanel portraitPanel = new JPanel(new BorderLayout());
        portraitPanel.setOpaque(false);
        portraitPanel.add(portraitLabel, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setOpaque(false);
        detailsPanel.setPreferredSize(new Dimension(350, 0));

        for (String cls : classes) {
            JToggleButton btn = GUIStyle.createStyledToggleButton(cls);
            btn.setFont(GUIStyle.FONT_HEADER);
            if (cls.equals("Knight")) btn.setSelected(true);
            btn.addActionListener(e -> {
                selectedClass = cls;
                updateClassDetails(detailsPanel, portraitLabel);
            });
            bg.add(btn);
            classList.add(btn);
        }
        classListWrapper.add(classList, BorderLayout.NORTH);

        updateClassDetails(detailsPanel, portraitLabel);

        splitPane.add(classListWrapper, BorderLayout.WEST);
        splitPane.add(portraitPanel, BorderLayout.CENTER);
        splitPane.add(detailsPanel, BorderLayout.EAST);

        panel.add(splitPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JButton confirmBtn = GUIStyle.createStyledButton("SELECT HERO", true);
        confirmBtn.setPreferredSize(new Dimension(250, 50));
        confirmBtn.addActionListener(e -> startGame());

        JPanel confirmWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER));
        confirmWrapper.setOpaque(false);
        confirmWrapper.add(confirmBtn);

        bottomPanel.add(confirmWrapper, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void updateClassDetails(JPanel detailsPanel, JLabel portraitLabel) {
        detailsPanel.removeAll();

        // -------------------------------------------------------------
        // INSERT CUSTOM IMAGE PATHS HERE
        // Replace the path with your own 8-bit image paths.
        // e.g. String imagePath = "assets/images/" + selectedClass.toLowerCase() + ".png";
        // ImageIcon icon = new ImageIcon(imagePath);
        // portraitLabel.setIcon(icon);
        // For now, we will just use a text placeholder:
        portraitLabel.setText("<html><div style='text-align: center; color: #888888; font-size: 20px; border: 2px dashed #444444; padding: 150px 50px;'>[ " + selectedClass + " Image ]<br><br><span style='font-size: 12px;'>Insert 8-bit image here</span></div></html>");
        portraitLabel.setIcon(null);
        // -------------------------------------------------------------

        Map<String, int[]> stats = Map.of(
                // HP, MP, ATK, DEF, SPD
                "Knight",  new int[]{150, 50,  25, 20, 10},
                "Archer",  new int[]{100, 60,  20, 10, 15},
                "Rogue",   new int[]{90,  70,  22, 8,  25},
                "Wizard",  new int[]{80,  120, 30, 5,  12},
                "Priest",  new int[]{110, 100, 15, 12, 11},
                "Paladin", new int[]{140, 80,  20, 22, 8}
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

        JPanel headerInfo = new JPanel(new BorderLayout(0, 10));
        headerInfo.setOpaque(false);
        JLabel nameLbl = GUIStyle.createStyledLabel(selectedClass, GUIStyle.TEXT_MAIN, new Font("Segoe UI", Font.BOLD, 42));
        headerInfo.add(nameLbl, BorderLayout.NORTH);

        JTextArea descArea = new JTextArea(desc.get(selectedClass));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        descArea.setForeground(Color.LIGHT_GRAY);
        headerInfo.add(descArea, BorderLayout.CENTER);

        JPanel statsGrid = new JPanel(new GridLayout(5, 1, 0, 15));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(30, 0, 0, 0));

        statsGrid.add(createStatRow("HP", s[0], 150, GUIStyle.ACCENT_GREEN));
        statsGrid.add(createStatRow("MP", s[1], 120, GUIStyle.ACCENT_BLUE));
        statsGrid.add(createStatRow("Attack", s[2], 30, GUIStyle.ACCENT_RED));
        statsGrid.add(createStatRow("Defense", s[3], 25, new Color(150, 150, 150)));
        statsGrid.add(createStatRow("Speed", s[4], 30, GUIStyle.ACCENT_PURPLE));

        detailsPanel.add(headerInfo, BorderLayout.NORTH);
        detailsPanel.add(statsGrid, BorderLayout.CENTER);

        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private JPanel createStatRow(String label, int value, int max, Color color) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        JLabel lbl = GUIStyle.createStyledLabel(label, GUIStyle.TEXT_MUTED, new Font("Segoe UI", Font.BOLD, 14));
        lbl.setPreferredSize(new Dimension(80, 20));

        JLabel valLbl = GUIStyle.createStyledLabel(String.valueOf(value), GUIStyle.TEXT_MAIN, new Font("Segoe UI", Font.BOLD, 14));
        valLbl.setPreferredSize(new Dimension(40, 20));
        valLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        HealthBar bar = new HealthBar(value, max, color, false, "");
        bar.setPreferredSize(new Dimension(150, 8));

        JPanel barWrapper = new JPanel(new BorderLayout());
        barWrapper.setOpaque(false);
        barWrapper.setBorder(new EmptyBorder(6, 0, 6, 0));
        barWrapper.add(bar, BorderLayout.CENTER);

        panel.add(lbl, BorderLayout.WEST);
        panel.add(barWrapper, BorderLayout.CENTER);
        panel.add(valLbl, BorderLayout.EAST);

        return panel;
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
        } catch (dungeon.exceptions.UnknownClassException ex) {
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
        activeHeroMpBar = new HealthBar(50, 50, GUIStyle.ACCENT_BLUE, false, "");
        activeHeroMpText = GUIStyle.createStyledLabel("50/50 MP", GUIStyle.TEXT_MUTED, GUIStyle.FONT_BODY);

        JPanel playerBars = new JPanel(new GridLayout(4, 1, 0, 0));
        playerBars.setOpaque(false);
        playerBars.add(activeHeroHpBar);
        playerBars.add(activeHeroHpText);
        playerBars.add(activeHeroMpBar);
        playerBars.add(activeHeroMpText);

        playerCard.setLayout(new BorderLayout());
        playerCard.add(activeHeroNameLabel, BorderLayout.NORTH);
        playerCard.add(playerBars, BorderLayout.CENTER);

        JPanel floorCard = createStatCard();
        floorLabel = GUIStyle.createStyledLabel("FLOOR 1", GUIStyle.ACCENT_BLUE, GUIStyle.FONT_HEADER);
        JLabel vsLabel = GUIStyle.createStyledLabel("VS", GUIStyle.TEXT_MUTED, GUIStyle.FONT_BODY);
        floorCard.add(floorLabel);
        floorCard.add(vsLabel);

        JPanel enemyCard = createStatCard();
        enemyNameLabel = GUIStyle.createStyledLabel("Enemy", GUIStyle.ACCENT_RED, GUIStyle.FONT_HEADER);
        enemyHpBar = new HealthBar(100, 100, GUIStyle.ACCENT_RED, true, "HP ");
        enemyMpBar = new HealthBar(100, 100, GUIStyle.ACCENT_BLUE, true, "MP ");

        JPanel enemyBars = new JPanel(new GridLayout(2, 1, 0, 5));
        enemyBars.setOpaque(false);
        enemyBars.add(enemyHpBar);
        enemyBars.add(enemyMpBar);

        enemyCard.setLayout(new BorderLayout());
        enemyCard.add(enemyNameLabel, BorderLayout.NORTH);
        enemyCard.add(enemyBars, BorderLayout.CENTER);

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

        boolean actionSuccessful = executePlayerMove(activePlayer, actionType);
        if (!actionSuccessful) {
            return;
        }

        if (currentEnemy.isDead()) {
            handleVictory();
        } else {
            executeEnemyTurn(activePlayer);
        }

        refreshUI();
        refreshPartyList();
    }

    private boolean executePlayerMove(Player p, int type) {
        try {
            switch (type) {
                case 0: p.basicAttack(currentEnemy); break;
                case 1: p.useSkillOne(currentEnemy); break;
                case 2: p.useSkillTwo(currentEnemy); break;
                case 3: p.useSkillThree(currentEnemy); break;
                default: return false;
            }
            return true;
        } catch (dungeon.exceptions.InsufficientManaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Not enough mana", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    }

    private void executeEnemyTurn(Player p) {
        System.out.println(currentEnemy.getName() + " attacks " + p.getName() + "!");
        currentEnemy.performAction(p);

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
        TransactionSystem shop = gameStatus.getShop();

        String chosen = showShopDialog(p);
        if (chosen != null) {
            recruitAlly(p, shop, chosen);
        }
    }

    private String showShopDialog(Player p) {
        TransactionSystem shop = gameStatus.getShop();
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

    private void recruitAlly(Player p, TransactionSystem shop, String cls) {
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
            activeHeroMpBar.updateHealth(activePlayer.getMana(), activePlayer.getMaxMana());
            activeHeroMpText.setText(activePlayer.getMana() + " / " + activePlayer.getMaxMana() + " MP");
        }

        int floor = gameStatus.getCurrentFloor().getFloorNumber();
        floorLabel.setText("FLOOR " + floor);

        if (currentEnemy != null) {
            enemyNameLabel.setText(currentEnemy.getName());
            enemyHpBar.updateHealth(currentEnemy.getHp(), currentEnemy.getMaxHp());
            if (currentEnemy.getMaxMana() > 0) {
                enemyMpBar.setVisible(true);
                enemyMpBar.updateHealth(currentEnemy.getMana(), currentEnemy.getMaxMana());
            } else {
                enemyMpBar.setVisible(false);
            }
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
