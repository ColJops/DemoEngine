package com.demo.game.dungeoncrawl.ui;

import com.demo.game.dungeoncrawl.dto.EnemyData;
import com.demo.game.dungeoncrawl.dto.SaveData;
import com.demo.game.dungeoncrawl.logic.GameObjectFactory;
import com.demo.game.dungeoncrawl.model.Actor;
import com.demo.game.dungeoncrawl.model.Enemy;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.GameMap;

import java.util.List;

final class EnemyRestorer {

    private EnemyRestorer() {
    }

    static void clearEnemies(GameMap map) {
        List<Actor> enemies = map.getActors().stream()
                .filter(actor -> actor instanceof Enemy)
                .toList();

        for (Actor enemy : enemies) {
            enemy.getCell().setActor(null);
        }

        map.getActors().removeIf(actor -> actor instanceof Enemy);
    }

    static void restore(SaveData data, GameMap map) {
        if (data.enemies == null) {
            return;
        }

        for (EnemyData enemyData : data.enemies) {
            Cell cell = map.getCell(enemyData.x, enemyData.y);
            if (cell == null) {
                continue;
            }

            Enemy enemy = GameObjectFactory.createEnemy(enemyData.type, cell);
            if (enemy != null) {
                enemy.setHp(enemyData.hp);
                map.addActor(enemy);
            }
        }
    }
}
