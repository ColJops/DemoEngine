package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.dto.*;
import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.map.*;
import com.demo.game.dungeoncrawl.model.enemy.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

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
                ? player.getEquippedWeapon().getName()
                : null;

        pd.shield = player.getEquippedShield() != null
                ? player.getEquippedShield().getName()
                : null;

        for (Item item : player.getInventory()) {
            pd.inventory.add(item.getClass().getSimpleName());
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
                if (cell.getType() == CellType.DOOR) {
                    DoorData dd = new DoorData();
                    dd.x = x;
                    dd.y = y;
                    dd.open = (cell.getType() == CellType.FLOOR);
                    data.doors.add(dd);
                }

                // ITEMS
                if (cell.getItem() != null) {
                    ItemData id = new ItemData();
                    id.type = cell.getItem().getClass().getSimpleName();
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
        try {
            return gson.fromJson(new FileReader("save.json"), SaveData.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
