package dungeon.entities.players;

import dungeon.entities.Player;
import dungeon.interfaces.Damageable;

public class Wizard extends Player {
    public Wizard(String name) {
        super(name, 80, 120, 5, 30, 12, 7);
    }

    public void fireball(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(15);
        System.out.println("Wizard casts Fireball!");
        target.takeDamage(getAttackDamage() + 20);
    }

    public void iceShower(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(25);
        System.out.println("Wizard casts Ice Shower!");
        target.takeDamage(getAttackDamage() + 15);
    }

    public void explosion(Damageable target) throws dungeon.exceptions.InsufficientManaException {
        useMana(40);
        System.out.println("Wizard casts Explosion!");
        target.takeDamage(getAttackDamage() + 35);
    }

    @Override public void useSkillOne(Damageable target) throws dungeon.exceptions.InsufficientManaException { fireball(target); }
    @Override public void useSkillTwo(Damageable target) throws dungeon.exceptions.InsufficientManaException { iceShower(target); }
    @Override public void useSkillThree(Damageable target) throws dungeon.exceptions.InsufficientManaException { explosion(target); }
}