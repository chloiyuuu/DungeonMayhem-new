package dungeon.interfaces;

import dungeon.entities.Enemy;

public interface LevelSystem {
    int getFloorNumber();
    void incrementFloor() throws dungeon.exceptions.MaxFloorReachedException;
    Enemy generateMonster();
}
