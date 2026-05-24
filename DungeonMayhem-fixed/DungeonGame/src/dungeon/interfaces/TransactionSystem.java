package dungeon.interfaces;

import dungeon.entities.Player;
import java.util.List;
import java.util.Map;

public interface TransactionSystem {
    List<String> getAvailableCharacters(List<String> alreadyRecruited);
    int getPrice(String characterClass);
    Player buyCharacter(String characterClass, String heroName, int playerGold);
    Map<String, Integer> getAllPrices();
}
