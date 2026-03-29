package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.enemy.Bat;
import com.demo.game.dungeoncrawl.model.enemy.Skeleton;
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
                        Skeleton skeleton = new Skeleton(cell);
                        map.addActor(skeleton);
                        break;

                    case ' ':
                        //cell.setType(CellType.WALL);
                        break;

                    case 'p':
                        cell.setType(CellType.FLOOR);
                        cell.setItem(new HealthPotion());
                        break;

                    case 'k':
                        cell.setType(CellType.FLOOR);
                        cell.setItem(new Key(KeyType.BLUE));
                        break;

                    case 'r':
                        cell.setType(CellType.FLOOR);
                        cell.setItem(new Key(KeyType.RED));
                        break;

                    case 'g':
                        cell.setType(CellType.FLOOR);
                        cell.setItem(new Key(KeyType.GOLD));
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
                        Bat bat = new Bat(cell);
                        map.addActor(bat);
                        break;

                    case 't':
                        cell.setType(CellType.FLOOR);
                        cell.setItem(new Shield("Wooden Shield", 1));
                        break;

                    case 'm':
                        cell.setType(CellType.FLOOR);
                        cell.setItem(new Weapon("Iron Sword", 2));
                        break;

                    default:
                        throw new RuntimeException("Unrecognized character: '" + c + "'");
                }
            }
        }
        if (mapName.matches("map([1-9])\\.txt")) {
            map.setBiome(BiomeType.DUNGEON);
        } else {
            map.setBiome(BiomeType.FOREST);
        }

        return map;
    }

}