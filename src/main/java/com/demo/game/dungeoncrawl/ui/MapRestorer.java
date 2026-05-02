package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.dto.DoorData;
import com.demo.game.dungeoncrawl.dto.ItemData;
import com.demo.game.dungeoncrawl.dto.SaveData;
import com.demo.game.dungeoncrawl.logic.ItemFactory;
import com.demo.game.dungeoncrawl.logic.ItemParser;
import com.demo.game.dungeoncrawl.logic.MapLoader;
import com.demo.game.dungeoncrawl.model.Item;
import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.CellType;
import com.demo.game.dungeoncrawl.model.map.GameMap;

import java.util.ArrayList;
import java.util.List;

final class MapRestorer {

    private MapRestorer() {
    }

    static GameMap restore(SaveData data) {
        if (data == null || data.player == null) {
            return null;
        }

        GameMap map = MapLoader.loadMap("map" + data.level + ".txt");
        List<RestoredItem> restoredItems = createRestoredItems(data, map);

        EnemyRestorer.clearEnemies(map);
        clearItems(map);
        PlayerRestorer.restore(data, map);
        EnemyRestorer.restore(data, map);
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

            Item item = ItemFactory.createItem(ItemParser.parse(itemData.type, cell.getItem()));
            if (item != null) {
                restoredItems.add(new RestoredItem(cell, item));
            }
        }

        return restoredItems;
    }

    private static void clearItems(GameMap map) {
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                map.getCell(x, y).setItem(null);
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

            KeyType requiredKey = doorData.requiredKey != null
                    ? doorData.requiredKey
                    : cell.getRequiredKey();

            cell.setRequiredKey(requiredKey);
            cell.setExit(doorData.isExit || cell.isExit());
            cell.setType(doorData.open ? CellType.FLOOR : CellType.DOOR);
        }
    }

    private record RestoredItem(Cell cell, Item item) {
    }
}
