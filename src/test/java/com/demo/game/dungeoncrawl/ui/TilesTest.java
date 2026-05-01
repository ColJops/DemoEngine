package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.model.item.KeyType;
import com.demo.game.dungeoncrawl.model.item.Key;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.CellType;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TilesTest {

    @Test
    void coloredDoorsShouldUseCorrectAtlasCoordinates() {
        assertTile("door_blue", 0, 9);
        assertTile("door_red", 3, 9);
    }

    @Test
    void doorCellShouldSelectTileNameByRequiredKey() {
        GameMap map = new GameMap(2, 1, CellType.FLOOR);

        Cell blueDoor = map.getCell(0, 0);
        blueDoor.setType(CellType.DOOR);
        blueDoor.setRequiredKey(KeyType.BLUE);

        Cell redDoor = map.getCell(1, 0);
        redDoor.setType(CellType.DOOR);
        redDoor.setRequiredKey(KeyType.RED);

        assertEquals("door_blue", blueDoor.getTileName());
        assertEquals("door_red", redDoor.getTileName());
    }

    @Test
    void coloredKeysShouldResolveToExistingTiles() {
        Key blueKey = new Key(KeyType.BLUE);
        Key redKey = new Key(KeyType.RED);

        assertEquals("key_blue", blueKey.getTileName());
        assertEquals("key_red", redKey.getTileName());
        assertNotNull(Tiles.getTile(blueKey.getTileName()));
        assertNotNull(Tiles.getTile(redKey.getTileName()));
    }

    private void assertTile(String name, int tileX, int tileY) {
        Tiles.Tile tile = Tiles.getTile(name);

        assertEquals(tileX * (Tiles.TILE_WIDTH + 2), tile.x);
        assertEquals(tileY * (Tiles.TILE_HEIGHT + 2), tile.y);
        assertEquals(Tiles.TILE_WIDTH, tile.w);
        assertEquals(Tiles.TILE_HEIGHT, tile.h);
    }
}
