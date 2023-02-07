package fr.iban.events.menus;

import fr.iban.menuapi.menu.LazyPaginatedMenu;
import fr.iban.menuapi.menuitem.MenuItem;
import fr.iban.menuapi.utils.Head;
import fr.iban.menuapi.utils.ItemBuilder;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public abstract class ListEditMenu<T> extends LazyPaginatedMenu<T> {

    protected final Consumer<List<T>> finishConsumer;
    protected final List<T> list;
    private final String name;

    public ListEditMenu(Player player, String name, List<T> optionParamList, Consumer<List<T>> finishConsumer) {
        super(player);
        this.name = name;
        this.list = Objects.requireNonNullElseGet(optionParamList, ArrayList::new);
        this.finishConsumer = finishConsumer;

    }

    @Override
    protected int[] getFillableSlots() {
        return new int[]{
                0, 1, 2, 3, 4, 5, 6, 7, 8,
                9, 10, 11, 12, 13, 14, 15, 16, 17
        };
    }

    abstract public MenuItem getMenuItem(T object);

    @Override
    protected List<T> getLazyObjectList() {
        return list;
    }

    @Override
    public String getMenuName() {
        return "Edition liste : " + name;
    }

    @Override
    public int getRows() {
        return 3;
    }

    @Override
    public void setMenuItems() {
        addMenuItem(new MenuItem(26,
                new ItemBuilder(Head.PLUS.get()).setName("§aAjouter").build())
                .setClickCallback(click -> {
                    list.add(getNewElement());
                    reload();
                }));
    }

    @Override
    public void handleMenuClose(InventoryCloseEvent e) {
        finishConsumer.accept(list);
    }

    protected abstract T getNewElement();
}
