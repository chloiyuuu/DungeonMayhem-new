package dungeon.game;

import dungeon.entities.Player;
import java.io.*;

public class SaveManager {
    private static final String SAVE_FILE = "savegame.txt";

    public static void saveGame(GameStatus gameStatus) {
        System.out.println("📝 Saving game progress to " + SAVE_FILE + "...");
        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {
            Player player = gameStatus.getPlayer();
            writer.println(player.getName());
            writer.println(player.getClass().getSimpleName());
            writer.println(player.getHp());
            writer.println(player.getMana());
            writer.println(player.getGold());
            writer.println(gameStatus.getCurrentFloor().getFloorNumber());
            System.out.println("✅ Progress saved successfully!");
        } catch (IOException e) {
            System.err.println("❌ Error saving game: " + e.getMessage());
        }
    }

    public static boolean loadGame(GameStatus gameStatus) {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return false;
        }

        System.out.println("📂 Found save file. Reading progress from " + SAVE_FILE + "...");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String name = reader.readLine();
            String className = reader.readLine();
            int hp = Integer.parseInt(reader.readLine());
            int mana = Integer.parseInt(reader.readLine());
            int gold = Integer.parseInt(reader.readLine());
            int floorNum = Integer.parseInt(reader.readLine());


            gameStatus.startGame(name, className);
            gameStatus.getPlayer().setHp(hp);
            gameStatus.getPlayer().setMana(mana);
            gameStatus.getPlayer().setGold(gold);


            for (int i = 1; i < floorNum; i++) {
                gameStatus.getCurrentFloor().incrementFloor();
            }

            System.out.println("✅ Progress loaded successfully! Welcome back, " + name + ".");
            return true;
        } catch (Exception e) {
            System.err.println("❌ Failed to load save file: " + e.getMessage());
            return false;
        }
    }
}
