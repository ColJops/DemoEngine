package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.item.ItemType;

public abstract class Item  implements Drawable{

    protected String name;
    protected ItemType type;
    protected String tileName;

    public Item(String name, ItemType type, String tileName) {
        this.name = name;
        this.type = type;
        this.tileName = tileName;
    }

    public String getName() {
        return name;
    }

    public ItemType getType() {
        return type;
    }
    @Override
    public String getTileName() {
        return tileName;
    }

    public boolean use(Player player) {
        return false;
    }
}
