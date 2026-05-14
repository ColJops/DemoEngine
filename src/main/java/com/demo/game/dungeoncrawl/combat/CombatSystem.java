package com.demo.game.dungeoncrawl.combat;

import com.demo.game.dungeoncrawl.engine.CombatResult;
import com.demo.game.dungeoncrawl.model.Actor;
import com.demo.game.dungeoncrawl.model.Enemy;
import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.map.Cell;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import com.demo.game.dungeoncrawl.ui.Main;

public class CombatSystem {

    private CombatSystem() {
    }

    public static CombatResult attack(Actor attacker, Actor target, GameMap map) {
        if (attacker == null || target == null || map == null) {
            return CombatResult.HIT;
        }

        int damage = Math.max(1, attacker.getAttack() - target.getDefense());
        target.takeDamage(damage);

        Main.log(actorName(attacker) + " hits " + actorName(target) + " for " + damage + " damage.");

        if (target.isAlive()) {
            return CombatResult.HIT;
        }

        if (target instanceof Player) {
            Main.log("Player died.");
            return CombatResult.PLAYER_KILLED;
        }

        if (target instanceof Enemy enemy) {
            handleEnemyDeath(attacker, enemy, map);
        }

        return CombatResult.TARGET_KILLED;
    }

    private static void handleEnemyDeath(Actor attacker, Enemy enemy, GameMap map) {
        Main.log(actorName(enemy) + " died.");

        if (attacker instanceof Player player) {
            player.addKill();
        }

        Cell cell = enemy.getCell();

        if (cell != null) {
            cell.setActor(null);
        }

        map.removeActor(enemy);
    }

    private static String actorName(Actor actor) {
        if (actor instanceof Player) {
            return "Player";
        }

        return actor.getClass().getSimpleName();
    }
}