package dungeon.shop;

import dungeon.entities.Player;
import dungeon.interfaces.TransactionSystem;
import java.util.*;

public class Shop implements TransactionSystem {
    private static final Map<String, Integer> PRICES = new LinkedHashMap<>();
    static {
        PRICES.put("Wizard",  80);
        PRICES.put("Archer",  70);
        PRICES.put("Priest",  90);
        PRICES.put("Paladin", 100);
        PRICES.put("Rogue",   75);
    }

    @Override
    public List<String> getAvailableCharacters(List<String> alreadyRecruited) {
        List<String> available = new ArrayList<>();
        for (String name : PRICES.keySet()) {
            if (!alreadyRecruited.contains(name)) {
                available.add(name);
            }
        }
        return available;
    }

    @Override
    public int getPrice(String characterClass) {
        return PRICES.getOrDefault(characterClass, 0);
    }

    @Override
    public Player buyCharacter(String characterClass, String heroName, int playerGold) {
        int price = getPrice(characterClass);
        if (playerGold < price) return null;

        try {
            return dungeon.entities.CharacterFactory.createPlayer(characterClass, heroName);
        } catch (dungeon.exceptions.UnknownClassException e) {
            return null;
        }
    }

    @Override
    public Map<String, Integer> getAllPrices() {
        return Collections.unmodifiableMap(PRICES);
    }
}
