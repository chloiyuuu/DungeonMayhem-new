package dungeon.entities.enemies;

import dungeon.entities.Enemy;
import dungeon.interfaces.Damageable;

public class Demon extends Enemy {
    public Demon() {
        super("Demon", 100, 8, 28, 16);
    }

    private boolean soulDrain(Damageable target) {
        System.out.println(getName() + " casts Soul Drain, siphoning your life force!");
        target.takeDamage(getAttackDamage() + 15);
        return true;
    }

    private boolean hellfireRing(Damageable target) {
        System.out.println(getName() + " summons a Hellfire Ring!");
        target.takeDamage(getAttackDamage() + 20);
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
                if (!soulDrain(target)) basicAttack(target);
                break;
            case 2:
                if (!hellfireRing(target)) basicAttack(target);
                break;
        }
    }

    public String getDescription() {
        return "A dark magic user. High damage spells but low physical defense.";
    }
}
