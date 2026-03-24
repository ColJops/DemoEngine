package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.item.Key;
import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.item.Shield;
import com.demo.game.dungeoncrawl.model.item.Weapon;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.ui.Main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Player extends Actor implements Drawable {


    private int kills = 0;
    private int baseAttack = 2;
    private int baseDefense = 1;

    private Weapon equippedWeapon;
    private Shield equippedShield;

    private List<Item> inventory = new ArrayList<>();
    private final int INVENTORY_SIZE = 10;

    public Player(Cell cell) {
        super(cell, 20, 6, 2);
    }

    @Override
    public String getTileName() {
        return "player";
    }

    public void addKill() {
        kills++;
    }

    public int getKills() {
        return kills;
    }

    public void addItem(Item item) {
        inventory.add(item);
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public Shield getEquippedShield() {
        return equippedShield;
    }

    public boolean hasKey(KeyType type) {
        return inventory.stream()
                .filter(item -> item instanceof Key)
                .map(item -> (Key) item)
                .anyMatch(key -> key.getKeyType() == type);
    }

    public void useKey(KeyType type) {
        for (Iterator<Item> it = inventory.iterator(); it.hasNext(); ) {
            Item item = it.next();
            if (item instanceof Key key && key.getKeyType() == type) {
                it.remove();
                break;
            }
        }
    }

    public int getAttack() {
        int value = baseAttack;

        if (equippedWeapon != null) {
            value += equippedWeapon.getAttackBonus();
        }

        return value;
    }

    public int getDefense() {
        int value = baseDefense;

        if (equippedShield != null) {
            value += equippedShield.getDefenseBonus();
        }

        return value;
    }

    public void pickUp(Item item) {

        if (inventory.size() >= INVENTORY_SIZE) {
            Main.log("Inventory full!");
            return;
        }

        inventory.add(item);

        if (item instanceof Weapon weapon) {
            equipWeapon(weapon);
            Main.log("Picked up " + weapon.getName() + " (+" + weapon.getAttackBonus() + " ATK)");

        } else if (item instanceof Shield shield) {
            equipShield(shield);
            Main.log("Picked up " + shield.getName() + " (+" + shield.getDefenseBonus() + " DEF)");

        } else if (item instanceof Key key) {
            Main.log("Picked up " + key.getKeyType() + " key");

        } else {
            Main.log("Picked up " + item.getName());
        }
    }

    public void equipWeapon(Weapon newWeapon) {

        if (equippedWeapon != null) {
            inventory.add(equippedWeapon);
        }

        inventory.remove(newWeapon);
        equippedWeapon = newWeapon;
    }

    public void equipShield(Shield newShield) {

        if (equippedShield != null) {
            inventory.add(equippedShield);
        }

        inventory.remove(newShield);
        equippedShield = newShield;
    }

    public boolean isEquipped(Item item) {
        return item == equippedWeapon || item == equippedShield;
    }

}