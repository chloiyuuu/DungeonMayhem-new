package dungeon.entities.enemies;

import dungeon.entities.Enemy;
import dungeon.interfaces.Damageable;

public class Orc extends Enemy {
    public Orc() {
        super("Orc", 120, 12, 18, 5);
    }

    public boolean warCry(Damageable target) {
        System.out.println(getName() + " lets out a War Cry and charges!");
        target.takeDamage(getAttackDamage() + 10);
        return true;
    }

    public boolean shieldBash(Damageable target) {
        System.out.println(getName() + " bashes with its shield!");
        target.takeDamage(getAttackDamage() + 5);
        return true;
    }

    @Override
    public void performAction(Damageable target) {
        int rand = (int) (Math.random() * 3);
        switch (rand) {
            case 0:
                basicAttack(target);
                break;
            case 1:
                if (!warCry(target)) basicAttack(target);
                break;
            case 2:
                if (!shieldBash(target)) basicAttack(target);
                break;
        }
    }

    public String getDescription() {
        return "A slow but durable brute. High HP and defense, low speed.";
    }
}
