package com.demo.game.dungeoncrawl.model.item;

import com.demo.game.dungeoncrawl.model.Item;
import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.ui.Main;

public class HealthPotion extends Item {

    public HealthPotion(Object o) {
        super("Health Potion", ItemType.CONSUMABLE, "potion_health");
    }

    @Override
    public void use(Player player) {
        int before = player.getHp();

        player.heal(5);

        int healed = player.getHp() - before;

        if (Main.instance != null) {
            if (healed > 0) {
                Main.log("Used Health Potion (+" + healed + " HP)");
            } else {
                Main.log("Used Health Potion (HP already full)");
            }
        }
    }
}
