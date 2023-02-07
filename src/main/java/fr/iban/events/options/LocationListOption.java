package fr.iban.events.options;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationListOption extends Option<List<Location>> {

    public LocationListOption(String name) {
        super(name);
        value.add(Bukkit.getWorlds().get(0).getSpawnLocation());
    }

}
