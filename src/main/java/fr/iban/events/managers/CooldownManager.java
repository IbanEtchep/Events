package fr.iban.events.managers;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class CooldownManager {

    private final Map<Player, Integer> cooldowns = new HashMap<>();

    public void setCooldown(Player p, int time) {
        if(time<1) {
            cooldowns.remove(p);
        } else {
            cooldowns.put(p, time);
        }
    }

    public int getCooldown(Player p) {
        return cooldowns.getOrDefault(p, 0);
    }

}
