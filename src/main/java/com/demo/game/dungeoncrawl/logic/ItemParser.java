package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.Item;
import com.demo.game.dungeoncrawl.model.item.HealthPotion;
import com.demo.game.dungeoncrawl.model.item.ItemDescriptor;
import com.demo.game.dungeoncrawl.model.item.Key;
import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.item.Shield;
import com.demo.game.dungeoncrawl.model.item.Weapon;

public final class ItemParser {

    private ItemParser() {
    }

    public static ItemDescriptor parse(String id) {
        return parse(id, null);
    }

    public static ItemDescriptor parse(String id, Item fallback) {
        if (id == null) {
            return null;
        }

        if ("Key".equals(id) && fallback instanceof Key key) {
            return ItemDescriptor.key(key.getKeyType());
        }

        if ("Weapon".equals(id) && fallback instanceof Weapon weapon) {
            return ItemDescriptor.weapon(weapon.getAttackBonus(), weapon.getName());
        }

        if ("Shield".equals(id) && fallback instanceof Shield shield) {
            return ItemDescriptor.shield(shield.getDefenseBonus(), shield.getName());
        }

        if (id.startsWith("Key:")) {
            return ItemDescriptor.key(KeyType.valueOf(id.substring("Key:".length())));
        }

        if (id.startsWith("Weapon:")) {
            String[] parts = id.split(":", 3);
            if (parts.length == 3) {
                return ItemDescriptor.weapon(Integer.parseInt(parts[1]), parts[2]);
            }
        }

        if (id.startsWith("Shield:")) {
            String[] parts = id.split(":", 3);
            if (parts.length == 3) {
                return ItemDescriptor.shield(Integer.parseInt(parts[1]), parts[2]);
            }
        }

        return parseLegacyName(id);
    }

    public static String itemId(Item item) {
        if (item instanceof HealthPotion) {
            return "HealthPotion";
        }

        if (item instanceof Key key) {
            return "Key:" + key.getKeyType().name();
        }

        if (item instanceof Weapon weapon) {
            return "Weapon:" + weapon.getAttackBonus() + ":" + weapon.getName();
        }

        if (item instanceof Shield shield) {
            return "Shield:" + shield.getDefenseBonus() + ":" + shield.getName();
        }

        return item.getClass().getSimpleName();
    }

    private static ItemDescriptor parseLegacyName(String id) {
        return switch (id) {
            case "HealthPotion", "Health Potion" -> ItemDescriptor.healthPotion();
            case "Key" -> ItemDescriptor.key(KeyType.BLUE);
            case "Iron Sword", "Weapon" -> ItemDescriptor.weapon(2, "Iron Sword");
            case "Wooden Shield", "Shield" -> ItemDescriptor.shield(1, "Wooden Shield");
            default -> null;
        };
    }
}
