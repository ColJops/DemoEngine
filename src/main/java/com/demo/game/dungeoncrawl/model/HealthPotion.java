package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.ui.Main;

public class HealthPotion extends Item {

    public HealthPotion(Cell cell) {
        super(cell, "Health Potion");
    }

    @Override
    public String getTileName() {
        return "potion";
    }

    @Override
    public void onPickup(Player player) {
        player.takeDamage(-5); // heal

        if (Main.instance != null) {
            Main.log("Picked up Health Potion (+5 HP)");
        }
    }
}
