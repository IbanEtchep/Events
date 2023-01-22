package fr.iban.events.listeners;

import fr.iban.events.GameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class TeleportListener implements Listener {

    private final GameManager manager;

    public TeleportListener(GameManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
//		if(e.getCause() == TeleportCause.PLUGIN || e.getCause() == TeleportCause.COMMAND) {
//			Player player = e.getPlayer();
//			if(manager.getPlayingEvent(player) != null) return;
//			
//			Event event = manager.getNearestEvent(player);
//			
//			manager.joinEvent(player, event);
//		}
    }

}
