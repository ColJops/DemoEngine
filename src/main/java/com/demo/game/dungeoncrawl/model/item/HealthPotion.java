package com.demo.game.dungeoncrawl.model.item;

import com.demo.game.dungeoncrawl.model.Item;
import com.demo.game.dungeoncrawl.model.Player;

public class HealthPotion extends Item {

    public HealthPotion() {
        super("Health Potion", ItemType.CONSUMABLE, "potion_health");
    }

    @Override
    public boolean use(Player player) {
        int before = player.getHp();
        player.heal(5);
        return player.getHp() > before;
    }
}
