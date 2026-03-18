package com.demo.game.dungeoncrawl.model;

import com.demo.game.dungeoncrawl.logic.Drawable;

import java.util.*;


public class Skeleton extends Actor implements Drawable {

    private long lastMove = 0;
    private long lastAttack = 0;
    private static final long ATTACK_COOLDOWN = 1_000_000_000; // 1 sekunda

    public Skeleton(Cell cell) {
        super(cell, 10, 4, 1);
    }

    @Override
    public String getTileName() {
        return "skeleton";
    }

    //Prosty ruch Skeletona
    private static final int[][] DIRECTIONS = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0}
    };

    public void moveRandom() {

        int[][] dirs = {
                {0, -1}, {0, 1}, {-1, 0}, {1, 0}
        };

        int[] dir = dirs[new Random().nextInt(dirs.length)];

        Cell next = cell.getNeighbor(dir[0], dir[1]);

        if (next == null) return;

        Actor target = next.getActor();

        if (target instanceof Player) {
            attack(target);
        } else {
            move(dir[0], dir[1]);
        }
    }

    public void update(long now, GameMap map) {

        if (now - lastMove < 500_000_000) return;
        lastMove = now;

        Player player = map.getPlayer();

        Cell start = getCell();
        Cell goal = player.getCell();

        int[][] directions = {
                {0,-1},{0,1},{-1,0},{1,0}
        };

        Map<Cell, Cell> cameFrom = new HashMap<>();
        Queue<Cell> queue = new LinkedList<>();

        queue.add(start);
        cameFrom.put(start, null);

        while(!queue.isEmpty()) {

            Cell current = queue.poll();

            if(current == goal) break;

            for(int[] d : directions) {

                Cell next = current.getNeighbor(d[0], d[1]);

                if(next == null) continue;
                if(!next.isWalkable()) continue;

                if(next.getActor() != null && !(next.getActor() instanceof Player))
                    continue;

                if(cameFrom.containsKey(next)) continue;

                queue.add(next);
                cameFrom.put(next, current);
            }
        }

        if(!cameFrom.containsKey(goal)) return;

        Cell step = goal;

        while(cameFrom.get(step) != start) {
            step = cameFrom.get(step);
        }

        int dx = step.getX() - start.getX();
        int dy = step.getY() - start.getY();

        Actor target = step.getActor();

        if (target instanceof Player) {

            if (now - lastAttack >= ATTACK_COOLDOWN) {
                attack(target);
                lastAttack = now;
            }

        } else {
            move(dx, dy);
        }
    }



    private void tryMove(int dx, int dy) {

        Cell nextCell = getCell().getNeighbor(dx, dy);

        if (nextCell != null && nextCell.isWalkable()) {
            move(dx, dy);
        }
    }

}