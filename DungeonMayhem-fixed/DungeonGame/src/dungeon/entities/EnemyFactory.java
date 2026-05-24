package dungeon.entities;

import dungeon.entities.enemies.*;

public class EnemyFactory {
    public static Enemy createEnemyForFloor(int floorNumber) {
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
