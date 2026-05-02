package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.item.*;
import com.demo.game.dungeoncrawl.model.map.BiomeType;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.CellType;
import com.demo.game.dungeoncrawl.model.map.GameMap;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class MapLoader {
    private static final Logger LOGGER = Logger.getLogger(MapLoader.class.getName());
    private static final boolean STRICT_LOADING = Boolean.getBoolean("dungeoncrawl.strictMapLoading");

    public static GameMap loadMap(String mapName) {
        InputStream is = MapLoader.class.getResourceAsStream("/" + mapName);
        Scanner scanner = new Scanner(is);

        int width = scanner.nextInt();
        int height = scanner.nextInt();
        scanner.nextLine();

        GameMap map = new GameMap(width, height, CellType.EMPTY);
        map.setBiome(readBiome(scanner.nextLine(), mapName));

        Map<Character, Consumer<Cell>> mapDefinitions = createMapDefinitions(map);

        for (int y = 0; y < height; y++) {
            String line = scanner.hasNextLine() ? scanner.nextLine() : "";

            for (int x = 0; x < width; x++) {

                Cell cell = map.getCell(x, y);

                char c = (x < line.length()) ? line.charAt(x) : '#';
                Consumer<Cell> definition = mapDefinitions.get(c);
                if (definition == null) {
                    warnOrThrow("Unrecognized character '" + c + "' in " + mapName
                            + " at x=" + x + ", y=" + y + ". Leaving cell empty.");
                    continue;
                }
                definition.accept(cell);
            }
        }
        return map;
    }

    private static BiomeType readBiome(String line, String mapName) {
        String[] parts = line.trim().split("\\s+");
        if (parts.length == 2 && "BIOME".equalsIgnoreCase(parts[0])) {
            try {
                return BiomeType.valueOf(parts[1].toUpperCase(Locale.ROOT));
            } catch (IllegalArgumentException e) {
                warnOrThrow("Invalid biome in " + mapName + ": " + parts[1] + ". Using DUNGEON.");
            }
        } else {
            warnOrThrow("Missing biome header in " + mapName + ". Expected: BIOME <type>. Using DUNGEON.");
        }

        return BiomeType.DUNGEON;
    }

    private static void warnOrThrow(String message) {
        if (STRICT_LOADING) {
            throw new IllegalArgumentException(message);
        }

        LOGGER.warning(message);
    }

    private static Map<Character, Consumer<Cell>> createMapDefinitions(GameMap map) {
        Map<Character, Consumer<Cell>> mapDefinitions = new HashMap<>();

        mapDefinitions.put('#', cell -> cell.setType(CellType.WALL));
        mapDefinitions.put('.', cell -> cell.setType(CellType.FLOOR));
        mapDefinitions.put(' ', cell -> {
        });

        mapDefinitions.put('@', cell -> {
            cell.setType(CellType.FLOOR);
            Player player = new Player(cell);
            map.setPlayer(player);
            map.addActor(player);
        });

        mapDefinitions.put('s', cell -> {
            cell.setType(CellType.FLOOR);
            if (map.getBiome() == BiomeType.FOREST) {
                map.addActor(GameObjectFactory.createRandomForestEnemy(cell));
            } else {
                map.addActor(GameObjectFactory.createEnemy("Skeleton", cell));
            }
        });

        mapDefinitions.put('b', cell -> {
            cell.setType(CellType.FLOOR);
            map.addActor(GameObjectFactory.createEnemy("Bat", cell));
        });

        mapDefinitions.put('p', cell -> {
            cell.setType(CellType.FLOOR);
            cell.setItem(ItemFactory.createItem(ItemDescriptor.healthPotion()));
        });

        mapDefinitions.put('k', cell -> {
            cell.setType(CellType.FLOOR);
            cell.setItem(ItemFactory.createItem(ItemDescriptor.key(KeyType.BLUE)));
        });

        mapDefinitions.put('r', cell -> {
            cell.setType(CellType.FLOOR);
            cell.setItem(ItemFactory.createItem(ItemDescriptor.key(KeyType.RED)));
        });

        mapDefinitions.put('g', cell -> {
            cell.setType(CellType.FLOOR);
            cell.setItem(ItemFactory.createItem(ItemDescriptor.key(KeyType.GOLD)));
        });

        mapDefinitions.put('t', cell -> {
            cell.setType(CellType.FLOOR);
            cell.setItem(ItemFactory.createItem(ItemDescriptor.shield(1, "Wooden Shield")));
        });

        mapDefinitions.put('m', cell -> {
            cell.setType(CellType.FLOOR);
            cell.setItem(ItemFactory.createItem(ItemDescriptor.weapon(2, "Iron Sword")));
        });

        mapDefinitions.put('D', cell -> {
            cell.setType(CellType.DOOR);
            cell.setRequiredKey(KeyType.BLUE);
            cell.setExit(true);
        });

        mapDefinitions.put('R', cell -> {
            cell.setType(CellType.DOOR);
            cell.setRequiredKey(KeyType.RED);
            cell.setExit(false);
        });

        mapDefinitions.put('G', cell -> {
            cell.setType(CellType.DOOR);
            cell.setRequiredKey(KeyType.GOLD);
        });

        return mapDefinitions;
    }

}
