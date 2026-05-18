package dungeon.game;

import dungeon.entities.Player;
import dungeon.entities.players.*;
import dungeon.exceptions.GameException;
import dungeon.shop.Shop;

public class GameStatus {
    private Player player;
    private CurrentFloor currentFloor;
    private Shop shop;
    private boolean isGameOver;
    private boolean hasWon;

    // Gold reward per floor cleared
    private static final int GOLD_PER_FLOOR = 50;

    public GameStatus() {
        this.shop = new Shop();
    }

    /**
     * Starts the game. Player always begins as Knight per the design doc.
     * The player's chosen name is applied to the Knight.
     */
    public void startGame(String playerName, String playerClass) throws GameException {
        switch (playerClass.toLowerCase()) {
            case "knight":  this.player = new Knight(playerName);  break;
            case "archer":  this.player = new Archer(playerName);  break;
            case "rogue":   this.player = new Rogue(playerName);   break;
            case "wizard":  this.player = new Wizard(playerName);  break;
            case "priest":  this.player = new Priest(playerName);  break;
            case "paladin": this.player = new Paladin(playerName); break;
            default:
                throw new GameException("Unknown class: " + playerClass);
        }
        this.currentFloor = new CurrentFloor();
        this.isGameOver   = false;
        this.hasWon       = false;
        System.out.println("Game Started! " + playerName + " the "
                + playerClass + " enters the dungeon.");
    }

    /** Called when the current floor's enemy is defeated. Awards gold. */
    public void floorCleared() {
        player.collectReward(GOLD_PER_FLOOR);
        System.out.println("Floor " + currentFloor.getFloorNumber() + " cleared!");
    }

    /** Descend to next floor */
    public void nextFloor() {
        try {
            currentFloor.incrementFloor();
            System.out.println("Descending to floor " + currentFloor.getFloorNumber() + "...");
        } catch (GameException e) {
            System.out.println("Error transitioning floors: " + e.getMessage());
        }
    }

    public void checkVictory() {
        if (currentFloor.getFloorNumber() >= 10 && !player.isDead()) {
            System.out.println("Victory! The Dungeon Lord has been defeated!");
            this.hasWon    = true;
            this.isGameOver = true;
        } else if (player.isDead()) {
            System.out.println(player.getName() + " has fallen...");
            this.isGameOver = true;
        }
    }

    public Player getPlayer()             { return player; }
    public CurrentFloor getCurrentFloor() { return currentFloor; }
    public Shop getShop()                 { return shop; }
    public boolean isGameOver()           { return isGameOver; }
    public boolean hasWon()               { return hasWon; }
    public int getGoldRewardPerFloor()    { return GOLD_PER_FLOOR; }
}
