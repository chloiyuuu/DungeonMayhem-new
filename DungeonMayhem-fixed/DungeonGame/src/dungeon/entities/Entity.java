package dungeon.entities;

import dungeon.interfaces.Damageable;

public abstract class Entity implements Damageable {
    private String name;
    private int hp;
    private int maxHp;
    private int defense;
    private int attackDamage;
    private int speed;

    public Entity(String name, int maxHp, int defense, int attackDamage, int speed) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.defense = defense;
        this.attackDamage = attackDamage;
        this.speed = speed;
    }

    public void basicAttack(Damageable target) {
        System.out.println(this.name + " performs a basic attack!");
        target.takeDamage(this.attackDamage);
    }

    @Override
    public void takeDamage(int amount) {
        int actualDamage = Math.max(amount - this.defense, 1);
        this.hp -= actualDamage;
        System.out.println(this.name + " took " + actualDamage + " damage. Remaining HP: " + this.hp);
    }

    @Override
    public boolean isDead() {
        return this.hp <= 0;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public void setHp(int hp) { this.hp = hp; }
    public int getMaxHp() { return maxHp; }
    public int getDefense() { return defense; }
    public int getAttackDamage() { return attackDamage; }
    public int getSpeed() { return speed; }
}