package com.demo.game.dungeoncrawl.model.item;

import com.demo.game.dungeoncrawl.model.Item;
import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.ui.Main;

public class HealthPotion extends Item {

    public HealthPotion() {
        super("Health Potion", ItemType.CONSUMABLE, "potion_health");
    }

    @Override
    public void use(Player player) {
        player.takeDamage(-5);

        if (Main.instance != null) {
            Main.log("Used Health Potion (+5 HP)");
        }
    }
}
