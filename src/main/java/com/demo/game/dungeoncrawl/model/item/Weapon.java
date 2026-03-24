package com.demo.game.dungeoncrawl.model.item;

import com.demo.game.dungeoncrawl.model.Item;
import com.demo.game.dungeoncrawl.model.item.ItemType;

public class Weapon extends Item {

    private int attackBonus;

    public Weapon(String name, int attackBonus) {
        super(name, ItemType.WEAPON, "sword");
        this.attackBonus = attackBonus;
    }

    public int getAttackBonus() {
        return attackBonus;
    }
}
