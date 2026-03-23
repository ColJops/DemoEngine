package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.GameMap;

import java.util.*;

public class ChaseAI implements EnemyAI {

    private long lastMove = 0;
    private long lastAttack = 0;
    private static final long ATTACK_COOLDOWN = 1_000_000_000;

    @Override
    public void update(Enemy enemy, long now, GameMap map) {

        if (now - lastMove < 500_000_000) return;
        lastMove = now;

        Player player = map.getPlayer();

        Cell start = enemy.getCell();
        Cell goal = player.getCell();

        int[][] directions = {
                {0,-1},{0,1},{-1,0},{1,0}
        };

        Map<Cell, Cell> cameFrom = new HashMap<>();
        Queue<Cell> queue = new LinkedList<>();

        queue.add(start);
        cameFrom.put(start, null);

        while (!queue.isEmpty()) {

            Cell current = queue.poll();

            if (current == goal) break;

            for (int[] d : directions) {

                Cell next = current.getNeighbor(d[0], d[1]);

                if (next == null) continue;
                if (!next.isWalkable()) continue;

                if (next.getActor() != null && !(next.getActor() instanceof Player))
                    continue;

                if (cameFrom.containsKey(next)) continue;

                queue.add(next);
                cameFrom.put(next, current);
            }
        }

        if (!cameFrom.containsKey(goal)) return;

        Cell step = goal;

        while (cameFrom.get(step) != start) {
            step = cameFrom.get(step);
        }

        int dx = step.getX() - start.getX();
        int dy = step.getY() - start.getY();

        Actor target = step.getActor();

        if (target instanceof Player) {

            if (now - lastAttack >= ATTACK_COOLDOWN) {
                enemy.attack(target);
                lastAttack = now;
            }

        } else {
            enemy.move(dx, dy);
        }
    }
}