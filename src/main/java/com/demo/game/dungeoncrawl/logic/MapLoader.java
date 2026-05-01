package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.item.*;
import com.demo.game.dungeoncrawl.model.map.BiomeType;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.CellType;
import com.demo.game.dungeoncrawl.model.map.GameMap;

import java.io.InputStream;
import java.util.Scanner;

public class MapLoader {
    public static GameMap loadMap(String mapName) {
        InputStream is = MapLoader.class.getResourceAsStream("/" + mapName);
        Scanner scanner = new Scanner(is);

        int width = scanner.nextInt();
        int height = scanner.nextInt();
        scanner.nextLine();

        GameMap map = new GameMap(width, height, CellType.EMPTY);
        if (mapName.matches("map([1-9])\\.txt")) {
            map.setBiome(BiomeType.DUNGEON);
        } else {
            map.setBiome(BiomeType.FOREST);
        }
        for (int y = 0; y < height; y++) {
            String line = scanner.nextLine();

            for (int x = 0; x < width; x++) {

                Cell cell = map.getCell(x, y);

                char c = (x < line.length()) ? line.charAt(x) : '#';

                switch (c) {
                    case '#':
                        cell.setType(CellType.WALL);
                        break;

                    case '.':
                        cell.setType(CellType.FLOOR);
                        break;

                    case '@':
                        cell.setType(CellType.FLOOR);
                        Player player = new Player(cell);
                        map.setPlayer(player);
                        map.addActor(player);
                        break;

                    case 's':
                        cell.setType(CellType.FLOOR);
                        if (map.getBiome() == BiomeType.FOREST) {
                            map.addActor(GameObjectFactory.createRandomForestEnemy(cell));
                        } else {
                            map.addActor(GameObjectFactory.createEnemy("Skeleton", cell));
                        }
                        break;

                    case ' ':
                        //cell.setType(CellType.WALL);
                        break;

                    case 'p':
                        cell.setType(CellType.FLOOR);
                        cell.setItem(GameObjectFactory.createItem("HealthPotion"));
                        break;

                    case 'k':
                        cell.setType(CellType.FLOOR);
                        cell.setItem(GameObjectFactory.createItem("Key:BLUE"));
                        break;

                    case 'r':
                        cell.setType(CellType.FLOOR);
                        cell.setItem(GameObjectFactory.createItem("Key:RED"));
                        break;

                    case 'g':
                        cell.setType(CellType.FLOOR);
                        cell.setItem(GameObjectFactory.createItem("Key:GOLD"));
                        break;

                    case 'D':
                        cell.setType(CellType.DOOR);
                        cell.setRequiredKey(KeyType.BLUE);
                        cell.setExit(true);
                        break;

                    case 'R':
                        cell.setType(CellType.DOOR);
                        cell.setRequiredKey(KeyType.RED);
                        cell.setExit(false);
                        break;

                    case 'G':
                        cell.setType(CellType.DOOR);
                        cell.setRequiredKey(KeyType.GOLD);
                        break;

                    case 'b':
                        cell.setType(CellType.FLOOR);
                        map.addActor(GameObjectFactory.createEnemy("Bat", cell));
                        break;

                    case 't':
                        cell.setType(CellType.FLOOR);
                        cell.setItem(GameObjectFactory.createItem("Shield:1:Wooden Shield"));
                        break;

                    case 'm':
                        cell.setType(CellType.FLOOR);
                        cell.setItem(GameObjectFactory.createItem("Weapon:2:Iron Sword"));
                        break;

                    default:
                        throw new RuntimeException("Unrecognized character: '" + c + "'");
                }
            }
        }
        return map;
    }

}
