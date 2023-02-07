package fr.iban.events.menus;

import fr.iban.bukkitcore.utils.ItemBuilder;
import fr.iban.events.utils.Cuboid;
import fr.iban.menuapi.menuitem.MenuItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

public class CuboidListEditMenu extends ListEditMenu<Cuboid> {


    public CuboidListEditMenu(Player player, String name, List<Cuboid> optionParamList, Consumer<List<Cuboid>> finishConsumer) {
        super(player, name, optionParamList, finishConsumer);
    }

    @Override
    public MenuItem getMenuItem(Cuboid cuboid) {
        Location pos1 = cuboid.getLowerNE();
        Location pos2 = cuboid.getUpperSW();
        ItemStack item = new ItemBuilder(Material.PAPER).setName("Position")
                .addLore(cuboid.toString())
                .build();
        return new MenuItem(-1, item).setClickCallback(click -> {
            if(click.getClick() == ClickType.RIGHT) {
                list.remove(cuboid);
            }else if(click.getClick() == ClickType.LEFT) {
                for (int i = 0; i < list.size(); i++) {
                    Cuboid cub = list.get(i);
                    if(cub.equals(cuboid)) {
                        Cuboid newCuboid = getNewElement();
                        if(newCuboid != null) {
                            list.set(i, newCuboid);
                        }else {
                            player.sendMessage("§cErreur, veuillez sélectionner 2 positions avec worldedit.");
                        }
                    }
                }
            }
        });
    }

    @Override
    protected Cuboid getNewElement() {
        return Cuboid.getFromWorldEdit(player);
    }
}
