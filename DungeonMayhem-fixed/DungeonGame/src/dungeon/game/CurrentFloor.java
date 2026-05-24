package dungeon.game;

import dungeon.entities.Enemy;
import dungeon.exceptions.GameException;
import dungeon.interfaces.LevelSystem;

public class CurrentFloor implements LevelSystem {
    private int floorNumber;

    public CurrentFloor() {
        this.floorNumber = 1;
    }

    @Override
    public int getFloorNumber() {
        return floorNumber;
    }

    @Override
    public void incrementFloor() throws dungeon.exceptions.MaxFloorReachedException {
        if (floorNumber >= 10) {
            throw new dungeon.exceptions.MaxFloorReachedException("Cannot go beyond floor 10!");
        }
        floorNumber++;
    }

    @Override
    public Enemy generateMonster() {
        return dungeon.entities.EnemyFactory.createEnemyForFloor(floorNumber);
    }
}