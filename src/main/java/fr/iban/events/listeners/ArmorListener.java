package fr.iban.events.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import fr.iban.events.EventsPlugin;
import fr.iban.events.games.Game;
import fr.iban.events.interfaces.ArmorChangeListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArmorListener implements Listener {

    private EventsPlugin plugin;

    public ArmorListener(EventsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onArmorChange(PlayerArmorChangeEvent e) {
        Player p = e.getPlayer();
        Game game = plugin.getGameManager().getPlayingGame(p);
        if(game instanceof ArmorChangeListener armorChangeListener) {
            armorChangeListener.onArmorChange(e);
        }
    }


}
