package dungeon.ui;

import dungeon.entities.Player;
import dungeon.entities.Enemy;
import dungeon.game.GameStatus;
import dungeon.interfaces.TransactionSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import javax.imageio.ImageIO;
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

    private BufferedImage backgroundImage = null;
    private BufferedImage blurredBackgroundImage = null;
    private String currentScreen = "MENU";

    private BufferedImage generateBlur(BufferedImage img) {
        if (img == null) return null;
        int radius = 10;
        int size = radius * 2 + 1;
        float weight = 1.0f / (size * size);
        float[] data = new float[size * size];
        for (int i = 0; i < data.length; i++) {
            data[i] = weight;
        }
        Kernel kernel = new Kernel(size, size, data);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(img, null);
    }

    private void setScreen(String screenName) {
        currentScreen = screenName  ;
        cardLayout.show(mainPanel, screenName);
        mainPanel.repaint();
    }

    public DungeonMayhemGUI(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        this.cardLayout = new CardLayout();
        this.mainPanel  = new JPanel(cardLayout) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage bg = currentScreen.equals("MENU") ? backgroundImage : blurredBackgroundImage;
                if (bg != null) {
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                    if (currentScreen.equals("MENU")) {
                        g.setColor(new Color(0, 0, 0, 180)); // Darker overlay for menu
                    } else {
                        g.setColor(new Color(0, 0, 0, 140)); // Slightly lighter for gameplay
                    }
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };

        setupFrame();
        interceptSystemOut();
        initializeScreens();
    }

    private void setupFrame() {
        setTitle("DUNGEON MAYHEM");
        setSize(1200, 800);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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
            Color c = GUIStyle.TEXT_MUTED;
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
            } else if (currentEnemy != null && line.contains(currentEnemy.getName())) {
                c = GUIStyle.ACCENT_RED;
            }

            javax.swing.text.StyledDocument doc = battleLog.getStyledDocument();
            javax.swing.text.Style style = battleLog.addStyle("ColorStyle", null);
            javax.swing.text.StyleConstants.setForeground(style, c);
            javax.swing.text.StyleConstants.setFontFamily(style, "Monospaced");
            javax.swing.text.StyleConstants.setFontSize(style, 22);
            javax.swing.text.StyleConstants.setBold(style, true);

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
        setScreen("MENU");
    }

    private JPanel createMainMenu() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
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
                setScreen("CLASS_SELECT");
            } catch (dungeon.exceptions.InvalidNameException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridy = 2; gbc.insets = new Insets(0, 0, 20, 0);
        panel.add(startBtn, gbc);

        JButton continueBtn = GUIStyle.createStyledButton("CONTINUE GAME", false);
        continueBtn.setPreferredSize(new Dimension(250, 60));
        continueBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        continueBtn.addActionListener(e -> {
            if (dungeon.game.SaveManager.loadGame(gameStatus)) {
                activePlayer = gameStatus.getPlayer();
                refreshPartyList();
                refreshUI();
                startNewBattle();
                setScreen("BATTLE");
            } else {
                JOptionPane.showMessageDialog(this, "No save file found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        gbc.gridy = 3;
        panel.add(continueBtn, gbc);

        try {
            File bgFile = new File("C:\\Users\\shdyd\\Downloads\\dungeons-and-dragons-jedd-chevrier-dungeons-and-dragons-curse-of-strahd-tabletop-role-playing-game-in-the-fantasy-genre-hd-wallpaper-f950e84df1eafdcb5697f86f5041e66d.jpg");
            if (!bgFile.exists()) bgFile = new File("background.png");
            if (!bgFile.exists()) bgFile = new File("assets/background.png");
            if (bgFile.exists()) {
                backgroundImage = ImageIO.read(bgFile);
                blurredBackgroundImage = generateBlur(backgroundImage);
            }
        } catch (Exception ex) {
            System.err.println("Could not auto-load background: " + ex.getMessage());
        }

        return panel;
    }

    private JPanel createClassSelect() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        JLabel title = GUIStyle.createStyledLabel("CHARACTER SELECT", GUIStyle.TEXT_MAIN, new Font("Segoe UI", Font.BOLD, 48));
        headerPanel.add(title, BorderLayout.WEST);

        // Add a back button if needed, but let's just stick to the title.
        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel splitPane = new JPanel(new GridBagLayout());
        splitPane.setOpaque(false);

        // LEFT: Class Grid
        JPanel classListWrapper = new JPanel(new BorderLayout());
        classListWrapper.setOpaque(false);
        classListWrapper.setBorder(new EmptyBorder(0, 0, 0, 20));

        JPanel classList = new JPanel(new GridLayout(3, 1, 0, 20));
        classList.setOpaque(false);

        String[] classes = {"Knight", "Archer", "Rogue"};
        ButtonGroup bg = new ButtonGroup();

        // To hold the image placeholder
        JLabel portraitLabel = new JLabel();
        portraitLabel.setHorizontalAlignment(SwingConstants.CENTER);
        portraitLabel.setVerticalAlignment(SwingConstants.CENTER);

        JPanel portraitPanel = new JPanel(new BorderLayout());
        portraitPanel.setOpaque(false);
        portraitPanel.add(portraitLabel, BorderLayout.CENTER);

        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setOpaque(false);

        for (String cls : classes) {
            JToggleButton btn = GUIStyle.createStyledToggleButton(cls);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 28));
            btn.setPreferredSize(new Dimension(250, 80));
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

        GridBagConstraints splitGbc = new GridBagConstraints();
        splitGbc.fill = GridBagConstraints.BOTH;
        splitGbc.weighty = 1.0;
        splitGbc.gridy = 0;

        splitGbc.gridx = 0;
        splitGbc.weightx = 0.15;
        splitGbc.insets = new Insets(0, 0, 0, 40);
        splitPane.add(classListWrapper, splitGbc);

        splitGbc.gridx = 1;
        splitGbc.weightx = 0.35;
        splitGbc.insets = new Insets(0, 0, 0, 60);
        splitPane.add(portraitPanel, splitGbc);

        splitGbc.gridx = 2;
        splitGbc.weightx = 0.50;
        splitGbc.insets = new Insets(0, 0, 0, 60);
        splitPane.add(detailsPanel, splitGbc);

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
        String imagePath = "";
        if (selectedClass.equals("Knight")) {
            imagePath = "C:\\Users\\shdyd\\Downloads\\favpng_413f673ad36d21fd19343f37cb69c12c.png";
        } else if (selectedClass.equals("Archer")) {
            imagePath = "C:\\Users\\shdyd\\Downloads\\favpng_846682a1af2439fe86fbcb39d60430bf.png";
        } else if (selectedClass.equals("Rogue")) {
            imagePath = "C:\\Users\\shdyd\\Downloads\\YjHHZj.png";
        }

        try {
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage();
            int origW = icon.getIconWidth();
            int origH = icon.getIconHeight();
            if (origW > 0 && origH > 0) {
                // Scale proportionately to fit within a 450x650 bounding box
                double ratio = Math.min(450.0 / origW, 650.0 / origH);
                if (ratio > 1.0) ratio = 1.0; // Prevent scaling up small pixel art icons and making them blurry
                int newW = (int) (origW * ratio);
                int newH = (int) (origH * ratio);
                portraitLabel.setIcon(new ImageIcon(img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH)));
            } else {
                portraitLabel.setIcon(icon);
            }
            portraitLabel.setText(null);
        } catch (Exception e) {
            portraitLabel.setText("<html><div style='text-align: center; color: #888888; font-size: 20px; border: 2px dashed #444444; padding: 250px 80px;'>[ " + selectedClass + " Image ]<br><br><span style='font-size: 12px;'>Insert 8-bit image here</span></div></html>");
            portraitLabel.setIcon(null);
        }
        // -------------------------------------------------------------

        Map<String, int[]> stats = Map.of(
                // HP, MP, ATK, DEF, SPD
                "Knight",  new int[]{110, 40,  18, 16, 8},
                "Archer",  new int[]{80, 50,  18, 8, 14},
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
        JLabel nameLbl = GUIStyle.createStyledLabel(selectedClass, GUIStyle.TEXT_MAIN, new Font("Segoe UI", Font.BOLD, 56));
        headerInfo.add(nameLbl, BorderLayout.NORTH);

        JTextArea descArea = new JTextArea(desc.get(selectedClass));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setEditable(false);
        descArea.setOpaque(false);
        descArea.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        descArea.setForeground(Color.LIGHT_GRAY);
        headerInfo.add(descArea, BorderLayout.CENTER);

        JPanel statsGrid = new JPanel(new GridLayout(5, 1, 0, 20));
        statsGrid.setOpaque(false);
        statsGrid.setBorder(new EmptyBorder(0, 0, 0, 0));

        statsGrid.add(createStatRow("HP", s[0], 150, GUIStyle.ACCENT_GREEN));
        statsGrid.add(createStatRow("MP", s[1], 120, GUIStyle.ACCENT_BLUE));
        statsGrid.add(createStatRow("Attack", s[2], 30, GUIStyle.ACCENT_RED));
        statsGrid.add(createStatRow("Defense", s[3], 25, new Color(150, 150, 150)));
        statsGrid.add(createStatRow("Speed", s[4], 30, GUIStyle.ACCENT_PURPLE));

        JPanel statsWrapper = new JPanel(new BorderLayout());
        statsWrapper.setOpaque(false);
        statsWrapper.setBorder(new EmptyBorder(40, 0, 40, 80)); // Padding
        statsWrapper.add(statsGrid, BorderLayout.CENTER);

        detailsPanel.add(headerInfo, BorderLayout.NORTH);
        detailsPanel.add(statsWrapper, BorderLayout.CENTER);

        detailsPanel.revalidate();
        detailsPanel.repaint();
    }

    private JPanel createStatRow(String label, int value, int max, Color color) {
        JPanel panel = new JPanel(new BorderLayout(20, 0));
        panel.setOpaque(false);

        JLabel lbl = GUIStyle.createStyledLabel(label, GUIStyle.TEXT_MUTED, new Font("Segoe UI", Font.BOLD, 30));
        lbl.setPreferredSize(new Dimension(160, 50));

        JLabel valLbl = GUIStyle.createStyledLabel(String.valueOf(value), GUIStyle.TEXT_MAIN, new Font("Segoe UI", Font.BOLD, 30));
        valLbl.setPreferredSize(new Dimension(80, 50));
        valLbl.setHorizontalAlignment(SwingConstants.RIGHT);

        HealthBar bar = new HealthBar(value, max, color, false, "");
        bar.setPreferredSize(new Dimension(150, 40)); // Thicker bar

        JPanel barWrapper = new JPanel(new BorderLayout());
        barWrapper.setOpaque(false);
        barWrapper.setBorder(new EmptyBorder(5, 0, 5, 0));
        barWrapper.add(bar, BorderLayout.CENTER);

        JPanel mainContent = new JPanel(new BorderLayout(20, 0));
        mainContent.setOpaque(false);
        mainContent.add(lbl, BorderLayout.WEST);
        mainContent.add(barWrapper, BorderLayout.CENTER);
        mainContent.add(valLbl, BorderLayout.EAST);

        panel.add(mainContent, BorderLayout.CENTER);

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
            setScreen("BATTLE");
        } catch (dungeon.exceptions.UnknownClassException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createBattlePanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 50, 20, 50)); // More horizontal padding for fullscreen

        panel.add(createHeaderPanel(),   BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new BorderLayout(15, 0));
        centerWrapper.setOpaque(false);
        centerWrapper.add(createPartyPanel(), BorderLayout.WEST);

        JPanel logWrapper = new JPanel(new BorderLayout());
        logWrapper.setOpaque(false);
        logWrapper.setBorder(new EmptyBorder(0, 0, 50, 0));
        logWrapper.add(createLogPanel(), BorderLayout.CENTER);

        centerWrapper.add(logWrapper,   BorderLayout.CENTER);

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
        floorCard.setLayout(new BorderLayout());
        floorCard.setBorder(new EmptyBorder(0, 0, 0, 0));
        floorLabel = GUIStyle.createStyledLabel("FLOOR 1", GUIStyle.ACCENT_BLUE, new Font("Segoe UI", Font.BOLD, 48));
        floorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        floorCard.add(floorLabel, BorderLayout.CENTER);

        JPanel enemyCard = createStatCard();
        enemyNameLabel = GUIStyle.createStyledLabel("Enemy", GUIStyle.ACCENT_RED, new Font("Segoe UI", Font.BOLD, 36));
        enemyHpBar = new HealthBar(100, 100, GUIStyle.ACCENT_RED, true, "HP ");
        enemyMpBar = new HealthBar(100, 100, GUIStyle.ACCENT_BLUE, true, "MP ");

        enemyHpBar.setCustomFontSize(20);
        enemyHpBar.setCustomBarHeight(24);
        enemyMpBar.setCustomFontSize(20);
        enemyMpBar.setCustomBarHeight(24);
        enemyHpBar.setPreferredSize(new Dimension(200, 50));
        enemyMpBar.setPreferredSize(new Dimension(200, 50));

        JPanel enemyBars = new JPanel(new GridLayout(2, 1, 0, 15));
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
        JPanel card = new TransparentPanel(new BorderLayout(10, 10));
        card.setBackground(GUIStyle.BG_PANEL);
        card.setBorder(GUIStyle.createCardBorder());
        return card;
    }

    private JPanel createPartyPanel() {
        partyPanel = new TransparentPanel(new FlowLayout(FlowLayout.CENTER, 10, 15));
        partyPanel.setPreferredSize(new Dimension(220, 0));
        partyPanel.setBackground(GUIStyle.BG_PANEL);
        partyPanel.setBorder(GUIStyle.createCardBorder());

        JLabel title = GUIStyle.createStyledLabel("Your Party", GUIStyle.TEXT_MAIN, GUIStyle.FONT_LABEL);
        partyPanel.add(title);

        return partyPanel;
    }

    public static class TransparentPanel extends JPanel {
        public TransparentPanel(LayoutManager layout) { super(layout); setOpaque(false); }
        public TransparentPanel() { setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            if (getBackground() != null) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
            }
            super.paintComponent(g);
        }
    }

    private JPanel createLogPanel() {
        battleLog = new JTextPane();
        battleLog.setOpaque(false);
        battleLog.setEditable(false);
        battleLog.setBackground(new Color(0, 0, 0, 0)); // Fully transparent
        battleLog.setForeground(GUIStyle.TEXT_MAIN);
        battleLog.setFont(new Font("Monospaced", Font.BOLD, 22));
        battleLog.setMargin(new Insets(15, 15, 15, 15));

        DefaultCaret caret = (DefaultCaret) battleLog.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scroll = new JScrollPane(battleLog);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        // Wrapper will provide the translucent background
        JPanel wrapper = new TransparentPanel(new BorderLayout());
        wrapper.setBackground(GUIStyle.BG_PANEL);
        wrapper.setBorder(GUIStyle.createCardBorder());
        wrapper.add(scroll, BorderLayout.CENTER);

        return wrapper;
    }

    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        panel.setOpaque(false);

        basicBtn = GUIStyle.createStyledButton("Attack", 180, 50);
        skill1Btn = GUIStyle.createStyledButton("Skill 1", 180, 50);
        skill2Btn = GUIStyle.createStyledButton("Skill 2", 180, 50);
        skill3Btn = GUIStyle.createStyledButton("Skill 3", 180, 50);

        openShopBtn = GUIStyle.createStyledButton("Visit Shop", 180, 50);
        openShopBtn.setVisible(false);

        nextFloorBtn = GUIStyle.createStyledButton("Descend", 180, 50);
        nextFloorBtn.setBackground(GUIStyle.ACCENT_GREEN);
        nextFloorBtn.setVisible(false);

        basicBtn.setFont(GUIStyle.FONT_BUTTON);
        skill1Btn.setFont(GUIStyle.FONT_BUTTON);
        skill2Btn.setFont(GUIStyle.FONT_BUTTON);
        skill3Btn.setFont(GUIStyle.FONT_BUTTON);
        openShopBtn.setFont(GUIStyle.FONT_BUTTON);
        nextFloorBtn.setFont(GUIStyle.FONT_BUTTON);

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

        try {
            gameStatus.executePlayerTurn(activePlayer, currentEnemy, actionType);
        } catch (dungeon.exceptions.InsufficientManaException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Not enough mana", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentEnemy.isDead()) {
            handleVictory();
        } else {
            gameStatus.executeEnemyTurn(activePlayer, currentEnemy);
            if (activePlayer.isDead()) {
                System.out.println("☠ " + activePlayer.getName() + " has fallen.");
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

        refreshUI();
        refreshPartyList();
    }

    private void handleVictory() {
        System.out.println("✓ Enemy Defeated!");
        gameStatus.floorCleared();
        setActionButtonsEnabled(false);
        dungeon.game.SaveManager.saveGame(gameStatus);

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
            setScreen("MENU");
        }
    }

    private void advanceFloor() {
        gameStatus.nextFloor();
        startNewBattle();
    }

    private void openShop() {
        Player p = gameStatus.getPlayer();

        String chosen = showShopDialog(p);
        if (chosen != null) {
            if (chosen.equals("_HEAL")) {
                if (gameStatus.healParty()) {
                    refreshUI();
                }
            } else {
                recruitAlly(p, chosen);
            }
        }
    }

    private String showShopDialog(Player p) {
        TransactionSystem shop = gameStatus.getShop();
        List<String> recruited = p.getRecruitedClasses();
        int gold = p.getGold();
        boolean isPartyFull = p.getTeam().size() >= 4;

        JPanel mainPanel = new TransparentPanel(new BorderLayout(15, 15));
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

        // List of recruits + heal
        JPanel gridPanel = new JPanel(new GridLayout(shop.getAllPrices().size() + 1, 1, 10, 10));
        gridPanel.setOpaque(false);

        ButtonGroup group = new ButtonGroup();

        // Add FULL HEAL option
        JPanel healPanel = new JPanel(new BorderLayout(10, 10));
        healPanel.setBackground(GUIStyle.BG_MAIN);
        healPanel.setBorder(BorderFactory.createCompoundBorder(
                GUIStyle.createCardBorder(),
                new EmptyBorder(5, 10, 5, 10)
        ));

        boolean canHeal = gold >= 50;
        JRadioButton healRb = new JRadioButton("Heal Party");
        healRb.setFont(GUIStyle.FONT_HEADER);
        healRb.setForeground(canHeal ? GUIStyle.TEXT_MAIN : GUIStyle.TEXT_MUTED);
        healRb.setBackground(GUIStyle.BG_MAIN);
        healRb.setEnabled(canHeal);
        healRb.setActionCommand("_HEAL");
        group.add(healRb);

        JLabel healStatusLabel = GUIStyle.createStyledLabel(canHeal ? "50 Gold" : "50 Gold (Insufficient)",
                canHeal ? GUIStyle.ACCENT_ORANGE : GUIStyle.TEXT_MUTED,
                GUIStyle.FONT_BODY);

        healPanel.add(healRb, BorderLayout.WEST);
        healPanel.add(healStatusLabel, BorderLayout.EAST);
        gridPanel.add(healPanel);

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

        UIManager.put("Panel.background", GUIStyle.BG_MAIN);
        UIManager.put("OptionPane.background", GUIStyle.BG_MAIN);
        int res = JOptionPane.showConfirmDialog(this, mainPanel, "Tavern", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        return (res == JOptionPane.OK_OPTION && group.getSelection() != null) ? group.getSelection().getActionCommand() : null;
    }

    private void recruitAlly(Player p, String cls) {
        String allyName = JOptionPane.showInputDialog(this, "What shall we call this " + cls + "?");
        if (allyName == null || allyName.trim().isEmpty()) allyName = cls;

        if (gameStatus.recruitAlly(cls, allyName)) {
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
        setScreen("MENU");
    }
}
