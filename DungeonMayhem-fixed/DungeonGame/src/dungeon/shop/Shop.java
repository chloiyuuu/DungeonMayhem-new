package dungeon.shop;

import dungeon.entities.Player;
import dungeon.entities.players.*;
import java.util.*;

public class Shop {
    // All recruitable allies with their gold prices
    private static final Map<String, Integer> PRICES = new LinkedHashMap<>();
    static {
        PRICES.put("Wizard",  80);
        PRICES.put("Archer",  70);
        PRICES.put("Priest",  90);
        PRICES.put("Paladin", 100);
        PRICES.put("Rogue",   75);
    }

    /**
     * Returns a list of ally names that the player hasn't recruited yet.
     */
    public List<String> getAvailableCharacters(List<String> alreadyRecruited) {
        List<String> available = new ArrayList<>();
        for (String name : PRICES.keySet()) {
            if (!alreadyRecruited.contains(name)) {
                available.add(name);
            }
        }
        return available;
    }

    public int getPrice(String characterClass) {
        return PRICES.getOrDefault(characterClass, 0);
    }

    /**
     * Attempts to buy a character. Returns the new Player ally or null if not enough gold.
     */
    public Player buyCharacter(String characterClass, String heroName, int playerGold) {
        int price = getPrice(characterClass);
        if (playerGold < price) return null;

        switch (characterClass) {
            case "Wizard":  return new Wizard(heroName);
            case "Archer":  return new Archer(heroName);
            case "Priest":  return new Priest(heroName);
            case "Paladin": return new Paladin(heroName);
            case "Rogue":   return new Rogue(heroName);
            default:        return null;
        }
    }

    public Map<String, Integer> getAllPrices() {
        return Collections.unmodifiableMap(PRICES);
    }
}
