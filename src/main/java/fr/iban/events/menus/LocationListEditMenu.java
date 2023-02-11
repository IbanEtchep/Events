package fr.iban.events.menus;

import fr.iban.bukkitcore.utils.ItemBuilder;
import fr.iban.menuapi.menuitem.MenuItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class LocationListEditMenu extends ListEditMenu<Location> {


    public LocationListEditMenu(Player player, String name, List<Location> optionParamList, Consumer<List<Location>> finishConsumer) {
        super(player, name, optionParamList, finishConsumer);
    }

    @Override
    public MenuItem getMenuItem(Location loc) {
        ItemStack item = new ItemBuilder(Material.PAPER).setName("Position")
                .addLore("Monde :" + loc.getWorld().getName())
                .addLore("X :" + loc.getX())
                .addLore("Y :" + loc.getY())
                .addLore("Z :" + loc.getZ())
                .addLore("§cClic droit pour supprimer")
                .build();
        return new MenuItem(-1, item).setClickCallback(click -> {
            if(click.getClick() == ClickType.RIGHT) {
                list.remove(loc);
            }else if(click.getClick() == ClickType.LEFT) {
                for (int i = 0; i < list.size(); i++) {
                    Location location = list.get(i);
                    if(location.equals(loc)) {
                        list.set(i, getNewElement());
                    }
                }
            }
        });
    }

    @Override
    protected Location getNewElement() {
        return player.getLocation();
    }

}
