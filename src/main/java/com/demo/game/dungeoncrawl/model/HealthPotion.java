package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.ui.Main;

public class HealthPotion extends Item {

    public HealthPotion(Cell cell) {
        super(cell, "Health Potion");
    }

    @Override
    public String getTileName() {
        return "potion_health";
    }

    @Override
    public void onPickup(Player player) {
        if (Main.instance != null) {
            Main.log("Picked up Health Potion");
        }
    }

    @Override
    public void use(Player player) {
        player.takeDamage(-5);

        if (Main.instance != null) {
            Main.log("Used Health Potion (+5 HP)");
        }
    }
}
