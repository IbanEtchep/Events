package fr.iban.events.listeners;

import fr.iban.events.games.Game;
import fr.iban.events.GameManager;
import fr.iban.events.enums.GameState;
import fr.iban.events.interfaces.PlayerDamageListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageListeners implements Listener {

    private final GameManager manager;

    public DamageListeners(GameManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player player) {
            Game game = manager.getPlayingGame(player);
            if (game == null || game.getGameState() == GameState.WAITING) {
                e.setCancelled(true);
            } else {
                if (game instanceof PlayerDamageListener) {
                    ((PlayerDamageListener) game).onPlayerDamage(e);
                }

                if (!game.getConfig().isDamage()) {
                    e.setDamage(0);
                }

                if (e instanceof EntityDamageByEntityEvent
                        && !game.getConfig().isPvp()
                        && getPlayerDamager((EntityDamageByEntityEvent) e) != null) {
                    e.setCancelled(true);
                }
            }
        }
    }

    private Player getPlayerDamager(EntityDamageByEntityEvent event) {
        Player player = null;
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE && event.getDamager() instanceof Projectile projectile) {
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
