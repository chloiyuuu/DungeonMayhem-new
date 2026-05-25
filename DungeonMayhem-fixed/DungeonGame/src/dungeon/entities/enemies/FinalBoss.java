package dungeon.entities.enemies;

import dungeon.entities.Enemy;
import dungeon.interfaces.Damageable;

public class FinalBoss extends Enemy {
    public FinalBoss() {
        super("The Dungeon Lord", 500, 150, 15, 40, 20);
    }

    private boolean groundSlam(Damageable target) {
        try {
            useMana(20);
            System.out.println(getName() + " uses Ground Slam! (AoE)");
            target.takeDamage(getAttackDamage() + 15);
            return true;
        } catch (dungeon.exceptions.InsufficientManaException e) {
            return false;
        }
    }

    private boolean rainingMeteors(Damageable target) {
        try {
            useMana(30);
            System.out.println(getName() + " casts Raining Meteors! (AoE)");
            target.takeDamage(getAttackDamage() + 25);
            return true;
        } catch (dungeon.exceptions.InsufficientManaException e) {
            return false;
        }
    }

    private boolean bigSlash(Damageable target) {
        try {
            useMana(40);
            System.out.println(getName() + " performs a Big Slash! (Line Attack)");
            target.takeDamage(getAttackDamage() + 35);
            return true;
        } catch (dungeon.exceptions.InsufficientManaException e) {
            return false;
        }
    }

    @Override
    public void performAction(Damageable target) {
        int rand = (int) (Math.random() * 4);
        switch (rand) {
            case 0:
                basicAttack(target);
                break;
            case 1:
                if (!groundSlam(target)) basicAttack(target);
                break;
            case 2:
                if (!rainingMeteors(target)) basicAttack(target);
                break;
            case 3:
                if (!bigSlash(target)) basicAttack(target);
                break;
        }
    }
}