package dungeon.interfaces;

import dungeon.exceptions.InsufficientManaException;

public interface SkillCaster {
    void useSkillOne(Damageable target) throws InsufficientManaException;
    void useSkillTwo(Damageable target) throws InsufficientManaException;
    void useSkillThree(Damageable target) throws InsufficientManaException;
}