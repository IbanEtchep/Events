package fr.iban.events.listeners;

import fr.iban.events.games.Game;
import fr.iban.events.GameManager;
import fr.iban.events.interfaces.VehicleExitListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class VehicleListener implements Listener {

    private final GameManager manager;

    public VehicleListener(GameManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onVehicleExit(VehicleExitEvent e) {
        if(e.getExited() instanceof Player player) {
            Game game = manager.getPlayingGame(player);

            if (game instanceof VehicleExitListener listener) {
                listener.onVehicleExit(e);
            }
        }
    }

}
