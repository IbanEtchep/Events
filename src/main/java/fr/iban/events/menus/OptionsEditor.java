package fr.iban.events.menus;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.bukkitcore.menu.Menu;
import fr.iban.bukkitcore.utils.ItemBuilder;
import fr.iban.events.GameManager;
import fr.iban.events.enums.GameType;
import fr.iban.events.options.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OptionsEditor extends Menu {

    private final GameManager manager;
    private final GameType type;
    private final String name;
    private final List<Option<?>> options;
    private final Map<Integer, Option<?>> optionAtSlot = new HashMap<>();

    public OptionsEditor(Player player, GameType type, String name, GameManager manager) {
        super(player);
        this.type = type;
        this.name = name;
        this.manager = manager;
        this.options = manager.getArenaOptions(type, name);
    }

    @Override
    public String getMenuName() {
        return "§2Edition d'arene";
    }

    @Override
    public int getRows() {
        return 1 + options.size() / 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        CoreBukkitPlugin core = CoreBukkitPlugin.getInstance();

        if (optionAtSlot.containsKey(e.getSlot())) {
            Option<?> option = optionAtSlot.get(e.getSlot());
            if (option instanceof IntOption intoption) {
                player.sendMessage("§e§lVeuillez entrer un nombre entier.");
                core.getTextInputs().put(player.getUniqueId(), texte -> {
                    try {
                        intoption.setValue(Integer.parseInt(texte));
                        manager.saveArenaOption(type, name, intoption);
                        player.sendMessage("Option sauvegardée.");
                        core.getTextInputs().remove(player.getUniqueId());
                        open();
                    } catch (NumberFormatException e1) {
                        player.sendMessage("§cVous devez entrer un nombre entier.");
                    }
                });
            } else if (option instanceof StringOption stroption) {
                core.getTextInputs().put(player.getUniqueId(), texte -> {
                    stroption.setValue(texte);
                    manager.saveArenaOption(type, name, stroption);
                    player.sendMessage("Option sauvegardée.");
                    open();
                });
            } else if (option instanceof LocationOption locoption) {
                locoption.setValue(player.getLocation());
                manager.saveArenaOption(type, name, locoption);
                player.sendMessage("Option sauvegardée.");
                open();
            } else if (option instanceof LocationListOption locationListOption) {
                new LocationListEditMenu(player, locationListOption.getName(), locationListOption.getValue(), updatedList -> {
                    locationListOption.setValue(updatedList);
                    open();
                }).open();
            } else if (option instanceof CuboidListOption cuboidListOption) {
                new CuboidListEditMenu(player, cuboidListOption.getName(), cuboidListOption.getValue(), updatedList -> {
                    cuboidListOption.setValue(updatedList);
                    open();
                }).open();
            }
        }
    }

    @Override
    public void setMenuItems() {
        for (int i = 0; i < options.size(); i++) {
            Option<?> option = options.get(i);
            inventory.setItem(i, buildItem(option));
            optionAtSlot.put(i, option);
        }
    }

    private ItemStack buildItem(Option<?> option) {
        ItemBuilder ib = new ItemBuilder(Material.PAPER).setName(option.getName());
        if (option instanceof IntOption intoption) {
            ib.addLore("" + intoption.getValue());
        } else if (option instanceof StringOption stringOption) {
            ib.addLore(stringOption.getValue());
        } else if (option instanceof LocationOption locationOption) {
            Location loc = locationOption.getValue();
            if (loc != null) {
                ib.addLore("Monde :" + loc.getWorld().getName());
                ib.addLore("X :" + loc.getX());
                ib.addLore("Y :" + loc.getY());
                ib.addLore("Z :" + loc.getZ());
            } else {
                ib.addLore("Non défini");
            }
        } else if (option instanceof CuboidListOption || option instanceof LocationListOption) {
            ib.addLore("Clic pour éditer la liste");
        }
        return ib.build();
    }

}
