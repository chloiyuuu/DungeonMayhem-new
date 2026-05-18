package dungeon.entities.enemies;

import dungeon.entities.Enemy;
import dungeon.interfaces.Damageable;

public class Orc extends Enemy {
    // Orc: slow but tanky with high defense, specializes in brute force
    public Orc() {
        super("Orc", 120, 12, 18, 5);
    }

    // Orc slams the ground, dealing heavy damage but leaving itself open
    public void warCry(Damageable target) {
        System.out.println(getName() + " lets out a War Cry and charges!");
        target.takeDamage(getAttackDamage() + 10);
    }

    // Orc raises its shield, but can still retaliate
    public void shieldBash(Damageable target) {
        System.out.println(getName() + " bashes with its shield!");
        target.takeDamage(getAttackDamage() + 5);
    }

    public String getDescription() {
        return "A slow but durable brute. High HP and defense, low speed.";
    }
}
