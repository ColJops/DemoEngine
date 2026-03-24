package com.demo.game.dungeoncrawl.model.item;

import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.Item;
import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.ui.Main;

public class Key extends Item {

    private KeyType keyType;

    public Key(KeyType keyType) {
        super(keyType.name() + " Key", ItemType.KEY, getTileNameForKey(keyType));
        this.keyType = keyType;
    }

    public KeyType getKeyType() {
        return keyType;
    }

    private static String getTileNameForKey(KeyType type) {
        return switch (type) {
            case BLUE -> "key_blue";
            case RED -> "key_red";
            case GOLD -> "key_gold";
        };
    }
}