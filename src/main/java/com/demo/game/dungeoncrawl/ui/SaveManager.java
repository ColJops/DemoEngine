package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.dto.*;
import com.demo.game.dungeoncrawl.logic.GameObjectFactory;
import com.demo.game.dungeoncrawl.logic.MapLoader;
import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.item.Shield;
import com.demo.game.dungeoncrawl.model.item.Weapon;
import com.demo.game.dungeoncrawl.model.map.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {

    public static void save(GameMap map, int currentLevel) {

        SaveData data = new SaveData();
        data.level = currentLevel;

        Player player = map.getPlayer();

        // PLAYER
        PlayerData pd = new PlayerData();
        pd.x = player.getX();
        pd.y = player.getY();
        pd.hp = player.getHp();
        pd.kills = player.getKills();

        pd.weapon = player.getEquippedWeapon() != null
                ? GameObjectFactory.itemId(player.getEquippedWeapon())
                : null;

        pd.shield = player.getEquippedShield() != null
                ? GameObjectFactory.itemId(player.getEquippedShield())
                : null;

        for (Item item : player.getInventory()) {
            pd.inventory.add(GameObjectFactory.itemId(item));
        }

        data.player = pd;

        // ENEMIES
        for (Actor actor : map.getActors()) {
            if (actor instanceof Enemy enemy) {
                EnemyData ed = new EnemyData();
                ed.type = enemy.getClass().getSimpleName();
                ed.x = enemy.getX();
                ed.y = enemy.getY();
                ed.hp = enemy.getHp();
                data.enemies.add(ed);
            }
        }

        // DOORS + ITEMS
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {

                Cell cell = map.getCell(x, y);

                // DOORS
                if (cell.getRequiredKey() != null || cell.getType() == CellType.DOOR) {
                    DoorData dd = new DoorData();
                    dd.x = x;
                    dd.y = y;
                    dd.open = (cell.getType() == CellType.FLOOR);
                    data.doors.add(dd);
                }

                // ITEMS
                if (cell.getItem() != null) {
                    ItemData id = new ItemData();
                    id.type = GameObjectFactory.itemId(cell.getItem());
                    id.x = x;
                    id.y = y;
                    data.items.add(id);
                }
            }
        }

        // SAVE JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Writer writer = new FileWriter("save.json")) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SaveData load() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("save.json")) {
            return gson.fromJson(reader, SaveData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static GameMap restoreMap(SaveData data) {
        if (data == null || data.player == null) {
            return null;
        }

        GameMap map = MapLoader.loadMap("map" + data.level + ".txt");
        List<RestoredItem> restoredItems = createRestoredItems(data, map);

        clearEnemies(map);
        clearItems(map);
        restorePlayer(data, map);
        restoreEnemies(data, map);
        restoreItems(restoredItems);
        restoreDoors(data, map);

        return map;
    }

    private static List<RestoredItem> createRestoredItems(SaveData data, GameMap map) {
        List<RestoredItem> restoredItems = new ArrayList<>();

        if (data.items == null) {
            return restoredItems;
        }

        for (ItemData itemData : data.items) {
            Cell cell = map.getCell(itemData.x, itemData.y);
            if (cell == null) {
                continue;
            }

            Item item = GameObjectFactory.createItem(itemData.type, cell.getItem());
            if (item != null) {
                restoredItems.add(new RestoredItem(cell, item));
            }
        }

        return restoredItems;
    }

    private static void clearEnemies(GameMap map) {
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Cell cell = map.getCell(x, y);

                if (cell.getActor() instanceof Enemy) {
                    cell.setActor(null);
                }
            }
        }

        map.getActors().removeIf(actor -> actor instanceof Enemy);
    }

    private static void clearItems(GameMap map) {
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                map.getCell(x, y).setItem(null);
            }
        }
    }

    private static void restorePlayer(SaveData data, GameMap map) {
        Player player = map.getPlayer();

        player.setKills(data.player.kills);
        player.setHp(data.player.hp);
        player.setPosition(data.player.x, data.player.y, map);
        player.getInventory().clear();

        if (data.player.inventory != null) {
            for (String itemId : data.player.inventory) {
                Item item = GameObjectFactory.createItem(itemId);
                if (item != null) {
                    player.getInventory().add(item);
                }
            }
        }

        Item weapon = GameObjectFactory.createItem(data.player.weapon);
        if (weapon instanceof Weapon w) {
            player.equipWeapon(w);
        }

        Item shield = GameObjectFactory.createItem(data.player.shield);
        if (shield instanceof Shield s) {
            player.equipShield(s);
        }

        player.recalculateStats();
    }

    private static void restoreEnemies(SaveData data, GameMap map) {
        if (data.enemies == null) {
            return;
        }

        for (EnemyData enemyData : data.enemies) {
            Cell cell = map.getCell(enemyData.x, enemyData.y);
            if (cell == null) {
                continue;
            }

            Enemy enemy = GameObjectFactory.createEnemy(enemyData.type, cell);
            if (enemy != null) {
                enemy.setHp(enemyData.hp);
                map.addActor(enemy);
            }
        }
    }

    private static void restoreItems(List<RestoredItem> restoredItems) {
        for (RestoredItem restoredItem : restoredItems) {
            restoredItem.cell().setItem(restoredItem.item());
        }
    }

    private static void restoreDoors(SaveData data, GameMap map) {
        if (data.doors == null) {
            return;
        }

        for (DoorData doorData : data.doors) {
            Cell cell = map.getCell(doorData.x, doorData.y);
            if (cell == null) {
                continue;
            }

            cell.setType(doorData.open ? CellType.FLOOR : CellType.DOOR);
        }
    }

    private record RestoredItem(Cell cell, Item item) {
    }
}
