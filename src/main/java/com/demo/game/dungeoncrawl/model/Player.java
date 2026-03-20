package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.ui.Main;

import java.util.ArrayList;
import java.util.List;

public class Player extends Actor implements Drawable {

    private int kills = 0;
    private List<Item> inventory = new ArrayList<>();

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

    public boolean hasKey(KeyType type) {
        return inventory.stream()
                .filter(i -> i instanceof Key)
                .map(i -> (Key) i)
                .anyMatch(k -> k.getType() == type);
    }

    public void useKey(KeyType type) {
        inventory.removeIf(i -> i instanceof Key k && k.getType() == type);
    }

}