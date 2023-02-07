package fr.iban.events.options;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationOption extends Option<Location> {

    public LocationOption(String name) {
        super(name);
        value = Bukkit.getWorlds().get(0).getSpawnLocation();
    }

}
