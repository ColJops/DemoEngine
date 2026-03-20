package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.ui.Main;

public class Key extends Item {

    private KeyType type;

    public Key(Cell cell, KeyType type) {
        super(cell, type.name() + " Key");
        this.type = type;
    }

    public KeyType getType() {
        return type;
    }

    @Override
    public String getTileName() {
        return switch (type) {
            case BLUE -> "key_blue";
            case RED -> "key_red";
            case GOLD -> "key_gold";
        };
    }

    @Override
    public void onPickup(Player player) {
        if (Main.instance != null) {
            Main.log("Picked up a " + type + " key");
        }
    }

    @Override
    public void use(Player player) {
        if (Main.instance != null) {
            Main.log("You can't use a key like that.");
        }
    }
}