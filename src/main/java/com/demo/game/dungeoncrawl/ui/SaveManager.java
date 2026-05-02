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

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // =========================
    // SAVE (SLOT)
    // =========================
    public static void save(GameMap map, int level, int slot) {
        Path path = Path.of("save_slot_" + slot + ".json");
        save(map, level, path);
    }

    // =========================
    // SAVE (CORE)
    // =========================
    public static void save(GameMap map, int level, Path path) {

        SaveData data = new SaveData();
        data.version = SaveData.CURRENT_VERSION;
        data.level = level;
        data.timestamp = System.currentTimeMillis();

        Player player = map.getPlayer();

        // ===== PLAYER =====
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

        // ===== ENEMIES =====
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

        // ===== MAP (DOORS + ITEMS) =====
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

        // ===== WRITE JSON =====
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================
    // LOAD (SLOT)
    // =========================
    public static SaveData load(int slot) {
        Path path = Path.of("save_slot_" + slot + ".json");

        if (!Files.exists(path)) {
            return null; // ✔ brak pliku = OK
        }

        try {
            String json = Files.readString(path);
            return GSON.fromJson(json, SaveData.class);
        } catch (Exception e) {
            System.out.println("Error reading save: " + path);
            e.printStackTrace();
            return null;
        }
    }

    // =========================
    // LOAD (CORE)
    // =========================
    public static SaveData load(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            SaveData data = GSON.fromJson(reader, SaveData.class);
            return SaveMigrator.migrate(data);
        } catch (Exception e) {
            if (!Files.exists(path)) {
                return null; // cisza, to normalne
            }
            return null;
        }
    }

    // =========================
    // RESTORE MAP (OPTIONAL)
    // =========================
    public static GameMap restoreMap(SaveData data) {
        return MapRestorer.restore(SaveMigrator.migrate(data));
    }

    public static boolean exists(int slot) {
        return Files.exists(Path.of("save_slot_" + slot + ".json"));
    }

    //Formatowanie czasu
    public static String formatTime(long timestamp) {
        return new java.text.SimpleDateFormat("dd.MM HH:mm")
                .format(new java.util.Date(timestamp));
    }

    public static void delete(int slot) {
        try {
            Files.deleteIfExists(Path.of("save_slot_" + slot + ".json"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}