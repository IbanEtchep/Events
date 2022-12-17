package fr.iban.events.listeners;

import fr.iban.events.Event;
import fr.iban.events.EventManager;
import fr.iban.events.enums.GameState;
import fr.iban.events.interfaces.ProjectileListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileDamageListener implements Listener {

    private final EventManager manager;

    public ProjectileDamageListener(EventManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent e) {
        if(e.getHitEntity() instanceof Player) {
            Player p = (Player) e.getHitEntity();
            if(manager.getPlayingEvent(p) != null) {
                Event ev = manager.getPlayingEvent(p);
                if(ev.getGameState().equals(GameState.RUNNING) && ev instanceof ProjectileListener) {
                    ((ProjectileListener) ev).onProjectileEvent(e);
                }
            }
        }
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent e) {
        Player p = (Player) e.getEntity().getShooter();
        if(manager.getPlayingEvent(p) != null) {
            Event ev = manager.getPlayingEvent(p);
            if(ev.getGameState().equals(GameState.RUNNING) && ev instanceof ProjectileListener) {
                ((ProjectileListener) ev).onThrow(e);
            }
        }
    }

}
