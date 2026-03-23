package com.demo.game.dungeoncrawl.logic;

import com.demo.game.dungeoncrawl.model.*;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.GameMap;

import java.util.List;

public class PatrolAggroAI implements EnemyAI {

    private final List<int[]> patrolPoints;
    private int currentTarget = 0;

    private final int aggroRadius;
    private boolean chasing = false;

    private final ChaseAI chaseAI = new ChaseAI();

    public PatrolAggroAI(List<int[]> patrolPoints, int aggroRadius) {
        this.patrolPoints = patrolPoints;
        this.aggroRadius = aggroRadius;
    }

    @Override
    public void update(Enemy enemy, long now, GameMap map) {

        Player player = map.getPlayer();

        int dist = Math.abs(player.getCell().getX() - enemy.getCell().getX())
                + Math.abs(player.getCell().getY() - enemy.getCell().getY());

        // 🔴 AGGRO
        if (dist <= aggroRadius) {
            chasing = true;
        }

        // 😴 LOSE AGGRO
        if (dist > aggroRadius + 2) {
            chasing = false;
        }

        // 🔥 CHASE MODE
        if (chasing) {
            chaseAI.update(enemy, now, map);
            return;
        }

        // 🟢 PATROL MODE
        patrol(enemy);
    }

    private void patrol(Enemy enemy) {

        int[] target = patrolPoints.get(currentTarget);

        int dx = Integer.compare(target[0], enemy.getCell().getX());
        int dy = Integer.compare(target[1], enemy.getCell().getY());

        Cell next = enemy.getCell().getNeighbor(dx, dy);

        if (next != null && next.isWalkable() && next.getActor() == null) {
            enemy.move(dx, dy);
        }

        // osiągnięto punkt patrolu
        if (enemy.getCell().getX() == target[0]
                && enemy.getCell().getY() == target[1]) {

            currentTarget = (currentTarget + 1) % patrolPoints.size();
        }
    }
}