package dungeon.entities.enemies;

import dungeon.entities.Enemy;
import dungeon.interfaces.Damageable;

public class FinalBoss extends Enemy {
    public FinalBoss() {
        super("The Dungeon Lord", 500, 30, 40, 20);
    }

    public void groundSlam(Damageable target) {
        System.out.println(getName() + " uses Ground Slam! (AoE)");
        target.takeDamage(getAttackDamage() + 15);
    }

    public void rainingMeteors(Damageable target) {
        System.out.println(getName() + " casts Raining Meteors! (AoE)");
        target.takeDamage(getAttackDamage() + 25);
    }

    public void bigSlash(Damageable target) {
        System.out.println(getName() + " performs a Big Slash! (Line Attack)");
        target.takeDamage(getAttackDamage() + 35);
    }
}