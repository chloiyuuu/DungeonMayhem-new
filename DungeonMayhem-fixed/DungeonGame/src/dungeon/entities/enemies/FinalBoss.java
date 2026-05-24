package dungeon.entities.enemies;

import dungeon.entities.Enemy;
import dungeon.interfaces.Damageable;

public class FinalBoss extends Enemy {
    public FinalBoss() {
        super("The Dungeon Lord", 500, 200, 30, 40);
    }

    public boolean groundSlam(Damageable target) {
        if (!useMana(20)) return false;
        System.out.println(getName() + " uses Ground Slam! (AoE)");
        target.takeDamage(getAttackDamage() + 15);
        return true;
    }

    public boolean rainingMeteors(Damageable target) {
        if (!useMana(40)) return false;
        System.out.println(getName() + " casts Raining Meteors! (AoE)");
        target.takeDamage(getAttackDamage() + 25);
        return true;
    }

    public boolean bigSlash(Damageable target) {
        if (!useMana(30)) return false;
        System.out.println(getName() + " performs a Big Slash! (Line Attack)");
        target.takeDamage(getAttackDamage() + 35);
        return true;
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