package dungeon.entities;

import dungeon.entities.players.*;
import dungeon.exceptions.UnknownClassException;

public class CharacterFactory {

    public static Player createPlayer(String characterClass, String heroName) throws UnknownClassException {
        if (characterClass == null) {
            throw new UnknownClassException("Class cannot be null");
        }

        switch (characterClass.toLowerCase()) {
            case "knight":  return new Knight(heroName);
            case "archer":  return new Archer(heroName);
            case "rogue":   return new Rogue(heroName);
            case "wizard":  return new Wizard(heroName);
            case "priest":  return new Priest(heroName);
            case "paladin": return new Paladin(heroName);
            default:
                throw new UnknownClassException("Unknown class: " + characterClass);
        }
    }
}
