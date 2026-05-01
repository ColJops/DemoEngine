package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.Enemy;
import com.demo.game.dungeoncrawl.model.Item;
import com.demo.game.dungeoncrawl.model.enemy.Bat;
import com.demo.game.dungeoncrawl.model.enemy.Scorpion;
import com.demo.game.dungeoncrawl.model.enemy.Skeleton;
import com.demo.game.dungeoncrawl.model.enemy.Spider;
import com.demo.game.dungeoncrawl.model.enemy.Wasp;
import com.demo.game.dungeoncrawl.model.item.HealthPotion;
import com.demo.game.dungeoncrawl.model.item.Key;
import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.item.Shield;
import com.demo.game.dungeoncrawl.model.item.Weapon;
import com.demo.game.dungeoncrawl.model.map.Cell;

import java.util.concurrent.ThreadLocalRandom;

public final class GameObjectFactory {

    private GameObjectFactory() {
    }

    public static Enemy createEnemy(String type, Cell cell) {
        return switch (type) {
            case "Skeleton" -> new Skeleton(cell);
            case "Spider" -> new Spider(cell);
            case "Scorpion" -> new Scorpion(cell);
            case "Wasp" -> new Wasp(cell);
            case "Bat" -> new Bat(cell);
            default -> null;
        };
    }

    public static Enemy createRandomForestEnemy(Cell cell) {
        double roll = ThreadLocalRandom.current().nextDouble();

        if (roll < 0.5) {
            return new Spider(cell);
        }

        if (roll < 0.8) {
            return new Wasp(cell);
        }

        return new Scorpion(cell);
    }

    public static Item createItem(String id) {
        return createItem(id, null);
    }

    public static Item createItem(String id, Item fallback) {
        if (id == null) {
            return null;
        }

        if ("Key".equals(id) && fallback instanceof Key key) {
            return new Key(key.getKeyType());
        }

        if ("Weapon".equals(id) && fallback instanceof Weapon weapon) {
            return new Weapon(weapon.getName(), weapon.getAttackBonus());
        }

        if ("Shield".equals(id) && fallback instanceof Shield shield) {
            return new Shield(shield.getName(), shield.getDefenseBonus());
        }

        if (id.startsWith("Key:")) {
            return new Key(KeyType.valueOf(id.substring("Key:".length())));
        }

        if (id.startsWith("Weapon:")) {
            String[] parts = id.split(":", 3);
            if (parts.length == 3) {
                return new Weapon(parts[2], Integer.parseInt(parts[1]));
            }
        }

        if (id.startsWith("Shield:")) {
            String[] parts = id.split(":", 3);
            if (parts.length == 3) {
                return new Shield(parts[2], Integer.parseInt(parts[1]));
            }
        }

        return switch (id) {
            case "HealthPotion", "Health Potion" -> new HealthPotion(null);
            case "Key" -> new Key(KeyType.BLUE);
            case "Iron Sword", "Weapon" -> new Weapon("Iron Sword", 2);
            case "Wooden Shield", "Shield" -> new Shield("Wooden Shield", 1);
            default -> null;
        };
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
}
