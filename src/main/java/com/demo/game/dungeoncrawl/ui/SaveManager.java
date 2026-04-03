package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.item.*;
import com.demo.game.dungeoncrawl.model.map.GameMap;

import java.io.*;
import java.util.*;

public class SaveManager {

    private static final String SAVE_FILE = "save.txt";

    public static void save(GameMap map, int currentLevel) {

        try (PrintWriter writer = new PrintWriter(new FileWriter(SAVE_FILE))) {

            Player player = map.getPlayer();

            writer.println("level=" + currentLevel);
            writer.println("hp=" + player.getHp());
            writer.println("kills=" + player.getKills());

            writer.println("weapon=" + (player.getEquippedWeapon() != null
                    ? player.getEquippedWeapon().getName()
                    : "none"));

            writer.println("shield=" + (player.getEquippedShield() != null
                    ? player.getEquippedShield().getName()
                    : "none"));

            // inventory
            for (Item item : player.getInventory()) {
                writer.println("item=" + item.getName());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SaveData load() {

        SaveData data = new SaveData();

        try (BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("=");

                switch (parts[0]) {
                    case "level" -> data.level = Integer.parseInt(parts[1]);
                    case "hp" -> data.hp = Integer.parseInt(parts[1]);
                    case "kills" -> data.kills = Integer.parseInt(parts[1]);
                    case "weapon" -> data.weapon = parts[1];
                    case "shield" -> data.shield = parts[1];
                    case "item" -> data.items.add(parts[1]);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }
}
