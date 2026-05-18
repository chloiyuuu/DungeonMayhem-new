package dungeon.entities;

public abstract class Enemy extends Entity {
    public Enemy(String name, int maxHp, int defense, int attackDamage, int speed) {
        super(name, maxHp, defense, attackDamage, speed);
    }
}