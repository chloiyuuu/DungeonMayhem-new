import dungeon.game.GameStatus;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        GameStatus gameStatus = new GameStatus();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                DungeonMayhemGUI gui = new DungeonMayhemGUI(gameStatus);
                gui.setVisible(true);
            }
        });
    }
}