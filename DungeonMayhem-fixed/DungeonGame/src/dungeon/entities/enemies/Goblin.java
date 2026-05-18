package dungeon.entities.enemies;

import dungeon.entities.Enemy;
import dungeon.interfaces.Damageable;

public class Goblin extends Enemy {
    // Goblin: fragile but very fast and sneaky, hits multiple times
    public Goblin() {
        super("Goblin", 70, 5, 14, 20);
    }

    // Goblin stabs rapidly with its rusty dagger
    public void flurryStab(Damageable target) {
        System.out.println(getName() + " unleashes a Flurry Stab!");
        target.takeDamage(getAttackDamage() + 8);
        target.takeDamage(getAttackDamage() + 4); // hits twice
    }

    // Goblin throws a stolen rock/junk at the target
    public void junkThrow(Damageable target) {
        System.out.println(getName() + " hurls junk at you from a distance!");
        target.takeDamage(getAttackDamage() + 6);
    }

    public String getDescription() {
        return "A quick and sneaky pest. Low HP, high speed, can strike twice.";
    }
}
