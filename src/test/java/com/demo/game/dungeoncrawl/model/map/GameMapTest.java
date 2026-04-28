package com.demo.game.dungeoncrawl.model.map;

import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.enemy.Skeleton;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameMapTest {

    @Test
    void constructorShouldCreateCellsWithDefaultType() {
        GameMap map = new GameMap(4, 3, CellType.FLOOR);

        assertEquals(4, map.getWidth());
        assertEquals(3, map.getHeight());

        for (int y = 0; y < map.getHeight(); y++) {
            for (int x = 0; x < map.getWidth(); x++) {
                Cell cell = map.getCell(x, y);

                assertNotNull(cell);
                assertEquals(x, cell.getX());
                assertEquals(y, cell.getY());
                assertEquals(CellType.FLOOR, cell.getType());
            }
        }
    }

    @Test
    void getCellShouldReturnNullOutsideBounds() {
        GameMap map = new GameMap(2, 2, CellType.WALL);

        assertNull(map.getCell(-1, 0));
        assertNull(map.getCell(0, -1));
        assertNull(map.getCell(2, 0));
        assertNull(map.getCell(0, 2));
    }

    @Test
    void cellNeighborShouldRespectMapBounds() {
        GameMap map = new GameMap(3, 3, CellType.FLOOR);
        Cell center = map.getCell(1, 1);
        Cell corner = map.getCell(0, 0);

        assertSame(map.getCell(2, 1), center.getNeighbor(1, 0));
        assertSame(map.getCell(1, 2), center.getNeighbor(0, 1));
        assertNull(corner.getNeighbor(-1, 0));
        assertNull(corner.getNeighbor(0, -1));
    }

    @Test
    void actorsCanBeAddedAndRemovedFromMap() {
        GameMap map = new GameMap(3, 3, CellType.FLOOR);
        Player player = new Player(map.getCell(1, 1));
        Skeleton skeleton = new Skeleton(map.getCell(1, 2));

        map.setPlayer(player);
        map.addActor(player);
        map.addActor(skeleton);

        assertSame(player, map.getPlayer());
        assertTrue(map.getActors().contains(player));
        assertTrue(map.getActors().contains(skeleton));

        map.removeActor(skeleton);

        assertFalse(map.getActors().contains(skeleton));
    }
}
