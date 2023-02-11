package fr.iban.events.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import fr.iban.events.Event;
import fr.iban.events.EventsPlugin;
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
        if(plugin.getEventManager().getPlayingEvent(p) != null) {
            Event ev = plugin.getEventManager().getPlayingEvent(p);
            ((ArmorChangeListener) ev).onArmorChange(e);
        }
    }


}
