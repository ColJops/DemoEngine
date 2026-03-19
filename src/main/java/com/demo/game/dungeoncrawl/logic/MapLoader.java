package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.*;

import java.io.InputStream;
import java.util.Scanner;

public class MapLoader {
    public static GameMap loadMap() {
        InputStream is = MapLoader.class.getResourceAsStream("/map.txt");
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
                        cell.setType(CellType.WALL);
                        break;

                    case 'p':
                        cell.setType(CellType.FLOOR);
                        new HealthPotion(cell);
                        break;

                    default:
                        throw new RuntimeException("Unrecognized character: '" + c + "'");
                }
            }
        }

        return map;
    }

}