package dungeon.entities.players;

import dungeon.entities.Player;
import dungeon.interfaces.Damageable;

public class Wizard extends Player {
    public Wizard(String name) {
        super(name, 80, 5, 30, 12, 7); 
    }

    public void fireball(Damageable target) {
        System.out.println("Wizard casts Fireball!");
        target.takeDamage(getAttackDamage() + 20);
    }

    public void iceShower(Damageable target) {
        System.out.println("Wizard casts Ice Shower!");
        target.takeDamage(getAttackDamage() + 15);
    }

    public void explosion(Damageable target) {
        System.out.println("Wizard casts Explosion!");
        target.takeDamage(getAttackDamage() + 35);
    }

    @Override public void useSkillOne(Damageable target) { fireball(target); }
    @Override public void useSkillTwo(Damageable target) { iceShower(target); }
    @Override public void useSkillThree(Damageable target) { explosion(target); }
}