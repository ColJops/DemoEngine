package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.ui.Main;

public class Key extends Item {

    public Key(Cell cell) {
        super(cell, "Key");
    }

    @Override
    public String getTileName() {
        return "key";
    }

    @Override
    public void onPickup(Player player) {
        if (Main.instance != null) {
            Main.log("Picked up a key");
        }
    }

    @Override
    public void use(Player player) {
        // klucz nie jest używany ręcznie
    }
}