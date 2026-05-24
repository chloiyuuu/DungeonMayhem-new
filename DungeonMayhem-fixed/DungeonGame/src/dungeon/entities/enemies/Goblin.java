package dungeon.entities.enemies;

import dungeon.entities.Enemy;
import dungeon.interfaces.Damageable;

public class Goblin extends Enemy {
    public Goblin() {
        super("Goblin", 150, 15, 20, 30);
    }

    public boolean flurryStab(Damageable target) {
        System.out.println(getName() + " unleashes a Flurry Stab!");
        target.takeDamage(getAttackDamage() + 8);
        target.takeDamage(getAttackDamage() + 4);
        return true;
    }

    public boolean junkThrow(Damageable target) {
        System.out.println(getName() + " hurls junk at you from a distance!");
        target.takeDamage(getAttackDamage() + 6);
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
                if (!flurryStab(target)) basicAttack(target);
                break;
            case 2:
                if (!junkThrow(target)) basicAttack(target);
                break;
        }
    }

    public String getDescription() {
        return "A quick and sneaky pest. Low HP, high speed, can strike twice.";
    }
}
