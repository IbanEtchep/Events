package fr.iban.events.listeners;

import fr.iban.events.GameManager;
import fr.iban.events.enums.GameState;
import fr.iban.events.games.Game;
import fr.iban.events.interfaces.ProjectileListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileDamageListener implements Listener {

    private final GameManager manager;

    public ProjectileDamageListener(GameManager manager) {
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent e) {
        if(e.getHitEntity() instanceof Player player) {
            if(manager.getPlayingGame(player) != null) {
                Game game = manager.getPlayingGame(player);
                if(game instanceof ProjectileListener && game.getGameState().equals(GameState.RUNNING)) {
                    ((ProjectileListener) game).onProjectileEvent(e);
                }
            }
        }
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent e) {
        Player p = (Player) e.getEntity().getShooter();
        if(manager.getPlayingGame(p) != null) {
            Game game = manager.getPlayingGame(p);
            if(game instanceof ProjectileListener && game.getGameState().equals(GameState.RUNNING)) {
                ((ProjectileListener) game).onThrow(e);
            }
        }
    }

}
