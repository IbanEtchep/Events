package fr.iban.events.listeners;

import fr.iban.events.games.Game;
import fr.iban.events.GameManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinQuitListeners implements Listener {

    private final GameManager manager;

    public JoinQuitListeners(GameManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        if (!player.hasPermission("event.admin")) {
            player.setGameMode(GameMode.ADVENTURE);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        Game game = manager.getPlayingGame(player);
        if (game != null) {
            game.removePlayer(player, false);
        }
    }

}
