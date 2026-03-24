package com.demo.game.dungeoncrawl.model.item;

import com.demo.game.dungeoncrawl.model.Item;
import com.demo.game.dungeoncrawl.model.item.ItemType;

public class Shield extends Item {

    private int defenseBonus;

    public Shield(String name, int defenseBonus) {
        super(name, ItemType.SHIELD, "shield");
        this.defenseBonus = defenseBonus;
    }

    public int getDefenseBonus() {
        return defenseBonus;
    }
}
