package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.Enemy;
import com.demo.game.dungeoncrawl.model.enemy.Bat;
import com.demo.game.dungeoncrawl.model.enemy.Scorpion;
import com.demo.game.dungeoncrawl.model.enemy.Skeleton;
import com.demo.game.dungeoncrawl.model.enemy.Spider;
import com.demo.game.dungeoncrawl.model.enemy.Wasp;
import com.demo.game.dungeoncrawl.model.map.Cell;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;

public final class GameObjectFactory {
    private static final Map<String, Function<Cell, Enemy>> ENEMY_REGISTRY = createEnemyRegistry();

    private GameObjectFactory() {
    }

    public static Enemy createEnemy(String type, Cell cell) {
        Function<Cell, Enemy> enemyFactory = ENEMY_REGISTRY.get(type);
        return enemyFactory != null ? enemyFactory.apply(cell) : null;
    }

    public static Enemy createRandomForestEnemy(Cell cell) {
        double roll = ThreadLocalRandom.current().nextDouble();

        if (roll < 0.5) {
            return new Spider(cell);
        }

        if (roll < 0.8) {
            return new Wasp(cell);
        }

        return new Scorpion(cell);
    }

    private static Map<String, Function<Cell, Enemy>> createEnemyRegistry() {
        Map<String, Function<Cell, Enemy>> enemyRegistry = new HashMap<>();
        enemyRegistry.put("Skeleton", Skeleton::new);
        enemyRegistry.put("Spider", Spider::new);
        enemyRegistry.put("Scorpion", Scorpion::new);
        enemyRegistry.put("Wasp", Wasp::new);
        enemyRegistry.put("Bat", Bat::new);
        return enemyRegistry;
    }
}
