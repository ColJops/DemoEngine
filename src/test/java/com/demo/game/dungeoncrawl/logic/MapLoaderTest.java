package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.Enemy;
import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.item.HealthPotion;
import com.demo.game.dungeoncrawl.model.item.Key;
import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.map.BiomeType;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.CellType;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapLoaderTest {

    @Test
    void map1ShouldLoadDungeonWithPlayerAndActors() {
        GameMap map = MapLoader.loadMap("map1.txt");

        assertEquals(BiomeType.DUNGEON, map.getBiome());
        assertNotNull(map.getPlayer());
        assertTrue(map.getActors().contains(map.getPlayer()));
        assertTrue(map.getActors().stream().anyMatch(actor -> actor instanceof Enemy));
    }

    @Test
    void map10ShouldLoadForestBiome() {
        GameMap map = MapLoader.loadMap("map10.txt");

        assertEquals(BiomeType.FOREST, map.getBiome());
        assertNotNull(map.getPlayer());
    }

    @Test
    void loadedMapShouldWireActorsToTheirCells() {
        GameMap map = MapLoader.loadMap("map1.txt");

        for (var actor : map.getActors()) {
            assertSame(actor, actor.getCell().getActor());
        }
    }

    @Test
    void loadedMapShouldContainExpectedItemTypes() {
        GameMap map = MapLoader.loadMap("map1.txt");

        assertTrue(hasItem(map, HealthPotion.class));
        assertTrue(hasKey(map, KeyType.BLUE));
    }

    @Test
    void loadedMapShouldContainAnExitDoor() {
        GameMap map = MapLoader.loadMap("map1.txt");

        boolean hasExitDoor = false;
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Cell cell = map.getCell(x, y);
                if (cell.getType() == CellType.DOOR && cell.isExit()) {
                    hasExitDoor = true;
                }
            }
        }

        assertTrue(hasExitDoor);
    }

    private boolean hasItem(GameMap map, Class<?> itemClass) {
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Cell cell = map.getCell(x, y);
                if (itemClass.isInstance(cell.getItem())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasKey(GameMap map, KeyType keyType) {
        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Cell cell = map.getCell(x, y);
                if (cell.getItem() instanceof Key key && key.getKeyType() == keyType) {
                    return true;
                }
            }
        }
        return false;
    }
}
