package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.logic.Drawable;
import com.demo.game.dungeoncrawl.model.map.Cell;

public abstract class Item implements Drawable {

    protected Cell cell;
    protected String name;

    public Item(Cell cell, String name) {
        this.cell = cell;
        this.name = name;
        cell.setItem(this);
    }

    public Cell getCell() {
        return cell;
    }

    public String getName() {
        return name;
    }

    public abstract void onPickup(Player player);
    public abstract void use(Player player);
}
