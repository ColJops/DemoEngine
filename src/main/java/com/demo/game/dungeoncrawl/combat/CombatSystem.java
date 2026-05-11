package com.demo.game.dungeoncrawl.combat;

import com.demo.game.dungeoncrawl.model.Actor;
import com.demo.game.dungeoncrawl.model.Enemy;
import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import com.demo.game.dungeoncrawl.ui.Main;

public class CombatSystem {

    public static void attack(Actor attacker, Actor target, GameMap map) {

        if (attacker == null || target == null) {
            return;
        }

        if (!attacker.isAlive() || !target.isAlive()) {
            return;
        }

        int damage = Math.max(1, attacker.getAttack() - target.getDefense());

        target.takeDamage(damage);

        Main.log(attacker.getClass().getSimpleName()
                + " attacks "
                + target.getClass().getSimpleName()
                + " for "
                + damage
                + " damage.");

        if (!target.isAlive()) {
            handleDeath(attacker, target, map);
        }
    }

    private static void handleDeath(Actor attacker, Actor target, GameMap map) {

        // =========================
        // PLAYER DEATH
        // =========================
        if (target instanceof Player) {
            Main.log("You have been slain...");
            Main.gameOver();
            return;
        }

        // =========================
        // ENEMY DEATH
        // =========================
        if (target instanceof Enemy) {
            target.getCell().setActor(null);
            map.removeActor(target);

            if (attacker instanceof Player player) {
                player.addKill();
                Main.log("You killed a " + target.getClass().getSimpleName() + "!");
            } else {
                Main.log(target.getClass().getSimpleName() + " died.");
            }
        }
    }
}