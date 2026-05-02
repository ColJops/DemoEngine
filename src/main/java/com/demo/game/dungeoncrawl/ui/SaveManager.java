package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.dto.*;
import com.demo.game.dungeoncrawl.logic.ItemParser;
import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.map.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class SaveManager {

    public static void save(GameMap map, int currentLevel) {
        save(map, currentLevel, Path.of("save.json"));
    }

    public static void save(GameMap map, int currentLevel, Path savePath) {

        SaveData data = new SaveData();
        data.version = SaveData.CURRENT_VERSION;
        data.level = currentLevel;

        Player player = map.getPlayer();

        // PLAYER
        PlayerData pd = new PlayerData();
        pd.x = player.getX();
        pd.y = player.getY();
        pd.hp = player.getHp();
        pd.kills = player.getKills();

        pd.weapon = player.getEquippedWeapon() != null
                ? ItemParser.itemId(player.getEquippedWeapon())
                : null;

        pd.shield = player.getEquippedShield() != null
                ? ItemParser.itemId(player.getEquippedShield())
                : null;

        for (Item item : player.getInventory()) {
            pd.inventory.add(ItemParser.itemId(item));
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
                    dd.requiredKey = cell.getRequiredKey();
                    dd.isExit = cell.isExit();
                    data.doors.add(dd);
                }

                // ITEMS
                if (cell.getItem() != null) {
                    ItemData id = new ItemData();
                    id.type = ItemParser.itemId(cell.getItem());
                    id.x = x;
                    id.y = y;
                    data.items.add(id);
                }
            }
        }

        // SAVE JSON
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (Writer writer = Files.newBufferedWriter(savePath)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SaveData load() {
        return load(Path.of("save.json"));
    }

    public static SaveData load(Path savePath) {
        Gson gson = new Gson();
        try (Reader reader = Files.newBufferedReader(savePath)) {
            return SaveMigrator.migrate(gson.fromJson(reader, SaveData.class));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static GameMap restoreMap(SaveData data) {
        return MapRestorer.restore(SaveMigrator.migrate(data));
    }
}
