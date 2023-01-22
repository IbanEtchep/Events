package fr.iban.events.listeners;

import fr.iban.events.games.Game;
import fr.iban.events.EventsPlugin;
import fr.iban.events.interfaces.MoveBlockListener;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private final EventsPlugin plugin;

    public PlayerMoveListener(EventsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        final Location from = e.getFrom();
        final Location to = e.getTo();

        int x = Math.abs(from.getBlockX() - to.getBlockX());
        int y = Math.abs(from.getBlockY() - to.getBlockY());
        int z = Math.abs(from.getBlockZ() - to.getBlockZ());

        if (x == 0 && y == 0 && z == 0) return;

        Game game = plugin.getEventManager().getPlayingGame(player);

        if (game instanceof MoveBlockListener) {
            ((MoveBlockListener) game).onMoveBlock(e);
        }
    }

}
