package dungeon.game;

import dungeon.entities.Enemy;
import dungeon.entities.enemies.Demon;
import dungeon.entities.enemies.FinalBoss;
import dungeon.entities.enemies.Goblin;
import dungeon.entities.enemies.Orc;
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