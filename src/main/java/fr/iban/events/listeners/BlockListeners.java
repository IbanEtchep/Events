package fr.iban.events.listeners;

import fr.iban.events.EventsPlugin;
import fr.iban.events.games.Game;
import fr.iban.events.interfaces.BlockBreakListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockListeners implements Listener {

    private final EventsPlugin plugin;

    public BlockListeners(EventsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        Game game = plugin.getGameManager().getPlayingGame(player);

        if (game instanceof BlockBreakListener breakListener) {
            e.setCancelled(false);
            breakListener.onBlockBreak(e);
        }
    }

}
