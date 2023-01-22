package fr.iban.events.games;

import fr.iban.events.EventsPlugin;
import fr.iban.events.interfaces.VehicleExitListener;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.inventory.ItemStack;

public class PigRaceGame extends ParkourGame implements VehicleExitListener {


    public PigRaceGame(EventsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void handlePlayerGameJoin(Player player) {
        Pig pig = (Pig) player.getWorld().spawnEntity(player.getLocation(), EntityType.PIG);
        pig.setSaddle(true);
        pig.addPassenger(player);
        player.getInventory().addItem(new ItemStack(Material.CARROT_ON_A_STICK));
        player.getInventory().addItem(new ItemStack(Material.CARROT_ON_A_STICK));
        player.getInventory().addItem(new ItemStack(Material.CARROT_ON_A_STICK));
    }

    @Override
    public void onVehicleExit(VehicleExitEvent e) {
        e.setCancelled(true);
    }
}
