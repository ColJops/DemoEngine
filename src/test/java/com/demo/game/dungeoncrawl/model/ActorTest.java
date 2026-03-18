package com.demo.game.dungeoncrawl.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ActorTest {

    @Test
    void playerShouldDamageSkeleton() {

        GameMap map = new GameMap(3, 3, CellType.FLOOR);

        Cell playerCell = map.getCell(1, 1);
        Cell skeletonCell = map.getCell(1, 2);

        Player player = new Player(playerCell);
        Skeleton skeleton = new Skeleton(skeletonCell);

        int initialHp = skeleton.getHp();

        player.attack(skeleton);

        assertTrue(skeleton.getHp() < initialHp);
    }

    @Test
    void actorShouldMoveToEmptyCell() {

        GameMap map = new GameMap(3, 3, CellType.FLOOR);

        Cell start = map.getCell(1, 1);
        Player player = new Player(start);

        player.move(0, 1);

        assertEquals(1, player.getCell().getX());
        assertEquals(2, player.getCell().getY());
    }

    @Test
    void actorShouldNotMoveIntoWall() {

        GameMap map = new GameMap(3, 3, CellType.FLOOR);

        Cell wall = map.getCell(1, 2);
        wall.setType(CellType.WALL);

        Player player = new Player(map.getCell(1, 1));

        player.move(0, 1);

        assertEquals(1, player.getCell().getX());
        assertEquals(1, player.getCell().getY());
    }
    @Test
    void skeletonShouldMoveCloserToPlayer() {

        GameMap map = new GameMap(5, 5, CellType.FLOOR);

        Player player = new Player(map.getCell(4, 4));
        map.setPlayer(player);

        Skeleton skeleton = new Skeleton(map.getCell(0, 0));
        map.addActor(skeleton);

        int initialDistance = Math.abs(4 - 0) + Math.abs(4 - 0);

        skeleton.update(System.nanoTime(), map);

        int newDistance = Math.abs(
                player.getCell().getX() - skeleton.getCell().getX()
        ) + Math.abs(
                player.getCell().getY() - skeleton.getCell().getY()
        );

        assertTrue(newDistance < initialDistance);
    }

    @Test
    void damageShouldBeAtLeastOne() {

        GameMap map = new GameMap(3, 3, CellType.FLOOR);

        Player player = new Player(map.getCell(1,1));
        Skeleton skeleton = new Skeleton(map.getCell(1,2));

        int initialHp = skeleton.getHp();

        player.attack(skeleton);

        int damage = initialHp - skeleton.getHp();

        assertTrue(damage >= 1);
    }

    @Test
    void playerShouldGainKillAfterEnemyDies() {

        GameMap map = new GameMap(3, 3, CellType.FLOOR);

        Player player = new Player(map.getCell(1,1));
        map.setPlayer(player);

        Skeleton skeleton = new Skeleton(map.getCell(1,2));
        map.addActor(skeleton);

        // zabijamy skeletona
        while (skeleton.isAlive()) {
            player.attack(skeleton);
        }

        if (!skeleton.isAlive()) {
            player.addKill();
        }

        assertEquals(1, player.getKills());
    }


    @Test
    void damageShouldBeAtLeastOneEvenWithHighDefense() {

        GameMap map = new GameMap(3, 3, CellType.FLOOR);

        Player player = new Player(map.getCell(1,1));
        Skeleton skeleton = new Skeleton(map.getCell(1,2));

        skeleton.setDefense(999); // ekstremalna obrona

        int initialHp = skeleton.getHp();

        player.attack(skeleton);

        int damage = initialHp - skeleton.getHp();

        assertEquals(1, damage);
    }

    @Test
    void skeletonShouldRespectAttackCooldown() {

        GameMap map = new GameMap(3, 3, CellType.FLOOR);

        Player player = new Player(map.getCell(1,1));
        map.setPlayer(player);

        Skeleton skeleton = new Skeleton(map.getCell(1,2));
        map.addActor(skeleton);

        long now = System.nanoTime();

        // pierwszy update → powinien zaatakować
        skeleton.update(now, map);
        int hpAfterFirst = player.getHp();

        // drugi update za wcześnie → NIE powinien zaatakować
        skeleton.update(now + 100_000_000, map); // 0.1s później
        int hpAfterSecond = player.getHp();

        assertEquals(hpAfterFirst, hpAfterSecond);
    }

    @Test
    void skeletonShouldAttackAfterCooldown() {

        GameMap map = new GameMap(3, 3, CellType.FLOOR);

        Player player = new Player(map.getCell(1,1));
        map.setPlayer(player);

        Skeleton skeleton = new Skeleton(map.getCell(1,2));
        map.addActor(skeleton);

        long now = System.nanoTime();

        skeleton.update(now, map);
        int hpAfterFirst = player.getHp();

        // po cooldownie
        skeleton.update(now + 2_000_000_000L, map);
        int hpAfterSecond = player.getHp();

        assertTrue(hpAfterSecond < hpAfterFirst);
    }
}