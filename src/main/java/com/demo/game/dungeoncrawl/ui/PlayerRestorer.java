package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.dto.SaveData;
import com.demo.game.dungeoncrawl.logic.ItemFactory;
import com.demo.game.dungeoncrawl.logic.ItemParser;
import com.demo.game.dungeoncrawl.model.Item;
import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.item.Shield;
import com.demo.game.dungeoncrawl.model.item.Weapon;
import com.demo.game.dungeoncrawl.model.map.GameMap;

final class PlayerRestorer {

    private PlayerRestorer() {
    }

    static void restore(SaveData data, GameMap map) {
        Player player = map.getPlayer();

        player.setKills(data.player.kills);
        player.setHp(data.player.hp);
        player.setPosition(data.player.x, data.player.y, map);
        player.getInventory().clear();

        if (data.player.inventory != null) {
            for (String itemId : data.player.inventory) {
                Item item = ItemFactory.createItem(ItemParser.parse(itemId));
                if (item != null) {
                    player.getInventory().add(item);
                }
            }
        }

        Item weapon = ItemFactory.createItem(ItemParser.parse(data.player.weapon));
        if (weapon instanceof Weapon w) {
            player.equipWeapon(w);
        }

        Item shield = ItemFactory.createItem(ItemParser.parse(data.player.shield));
        if (shield instanceof Shield s) {
            player.equipShield(s);
        }

        player.recalculateStats();
    }
}
