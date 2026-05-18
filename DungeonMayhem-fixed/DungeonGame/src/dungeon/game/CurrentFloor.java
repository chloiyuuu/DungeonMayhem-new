package dungeon.game;

import dungeon.entities.Enemy;
import dungeon.entities.enemies.Demon;
import dungeon.entities.enemies.FinalBoss;
import dungeon.entities.enemies.Goblin;
import dungeon.entities.enemies.Orc;
import dungeon.exceptions.GameException;

public class CurrentFloor {
    private int floorNumber;

    public CurrentFloor() {
        this.floorNumber = 1;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void incrementFloor() throws GameException {
        if (floorNumber >= 10) {
            throw new GameException("Cannot go beyond floor 10!");
        }
        floorNumber++;
    }

    public Enemy generateMonster() {
        if (floorNumber >= 1 && floorNumber <= 3) {
            return new Orc();
        } else if (floorNumber >= 4 && floorNumber <= 6) {
            return new Goblin();
        } else if (floorNumber >= 7 && floorNumber <= 9) {
            return new Demon();
        } else {
            return new FinalBoss();
        }
    }
}