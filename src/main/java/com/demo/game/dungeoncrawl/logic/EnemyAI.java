package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.map.GameMap;

public interface EnemyAI {
    void update(Enemy enemy, long now, GameMap map);
}