package com.demo.game.dungeoncrawl.combat;

import com.demo.game.dungeoncrawl.model.Actor;
import com.demo.game.dungeoncrawl.model.Enemy;
import com.demo.game.dungeoncrawl.model.Player;
import com.demo.game.dungeoncrawl.model.map.GameMap;
import com.demo.game.dungeoncrawl.ui.Main;

public class CombatSystem {

    public static void attack(Actor attacker, Actor target, GameMap map) {

        int damage = Math.max(1, attacker.getAttack() - target.getDefense());

        target.takeDamage(damage);

        Main.log(attacker.getClass().getSimpleName()
                + " attacks "
                + target.getClass().getSimpleName()
                + " for "
                + damage
                + " damage.");

        if (!target.isAlive()) {
            kill(attacker, target, map);
        }
    }

    private static void kill(Actor attacker, Actor target, GameMap map) {

        target.getCell().setActor(null);
        map.removeActor(target);

        Main.log("You killed a " + target.getClass().getSimpleName() + "!");

        if (attacker instanceof Player player && target instanceof Enemy) {
            player.addKill();
        }
    }
}
