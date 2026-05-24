import dungeon.game.GameStatus;
import dungeon.ui.DungeonMayhemGUI;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        GameStatus gameStatus = new GameStatus();

        SwingUtilities.invokeLater(() -> {
            DungeonMayhemGUI gui = new DungeonMayhemGUI(gameStatus);
            gui.setVisible(true);
        });
    }
}
