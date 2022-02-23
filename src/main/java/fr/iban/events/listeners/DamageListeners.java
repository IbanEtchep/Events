package fr.iban.events.listeners;

import fr.iban.events.Event;
import fr.iban.events.EventManager;
import fr.iban.events.enums.GameState;
import fr.iban.events.interfaces.PlayerDamageListener;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListeners implements Listener {

    private final EventManager manager;

    public DamageListeners(EventManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player) {
            Player player = (Player) e.getEntity();
            Event event = manager.getPlayingEvent(player);
            if (event == null || event.getGameState() == GameState.WAITING) {
                e.setCancelled(true);
            } else {
                if (event instanceof PlayerDamageListener) {
                    ((PlayerDamageListener) event).onPlayerDamage(e);
                }

                if (!event.isDamage()) {
                    e.setDamage(0);
                }

                if (e instanceof EntityDamageByEntityEvent
                        && !event.isPvp()
                        && getPlayerDamager((EntityDamageByEntityEvent) e) != null) {
                    e.setCancelled(true);
                }
            }
        }
    }

    private Player getPlayerDamager(EntityDamageByEntityEvent event) {
        Player player = null;
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE && event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                player = (Player) projectile.getShooter();
            }
        }
        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        }
        return player;
    }


}
