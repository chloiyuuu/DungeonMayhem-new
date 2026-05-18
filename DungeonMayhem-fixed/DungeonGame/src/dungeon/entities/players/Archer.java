package dungeon.entities.players;

import dungeon.entities.Player;
import dungeon.interfaces.Damageable;

public class Archer extends Player {
    public Archer(String name) {
        super(name, 100, 10, 20, 15, 10);
    }

    public void rainingArrows(Damageable target) {
        System.out.println("Archer uses Raining Arrows!");
        target.takeDamage(getAttackDamage() + 10);
    }

    public void bigArrow(Damageable target) {
        System.out.println("Archer uses Big Arrow Move!");
        target.takeDamage(getAttackDamage() + 25);
    }

    public void rapidShot(Damageable target) {
        System.out.println("Archer uses Rapid Shot!");
        target.takeDamage(getAttackDamage() + 15);
    }

    @Override public void useSkillOne(Damageable target) { rainingArrows(target); }
    @Override public void useSkillTwo(Damageable target) { bigArrow(target); }
    @Override public void useSkillThree(Damageable target) { rapidShot(target); }
}