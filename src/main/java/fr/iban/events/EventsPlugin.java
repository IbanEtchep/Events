package fr.iban.events;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import fr.iban.events.commands.EventCMD;
import fr.iban.events.commands.HostCMD;
import fr.iban.events.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class EventsPlugin extends JavaPlugin {

    private static EventsPlugin instance;
    private GameManager eventManager;
    private WorldEditPlugin worldEditPlugin;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        eventManager = new GameManager(this);
        getCommand("event").setExecutor(new EventCMD(this));
        getCommand("host").setExecutor(new HostCMD(this));
        registerListeners(new PlayerMoveListener(this),
                new JoinQuitListeners(getEventManager()),
                new TeleportListener(eventManager),
                new DamageListeners(eventManager),
                new JoinQuitListeners(eventManager),
                new FoodListener()
        );

        Plugin worldEditPlugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if(worldEditPlugin instanceof WorldEditPlugin worldedit) {
            this.worldEditPlugin = worldedit;
            getLogger().info("Hooked into worldedit.");
        }
    }

    @Override
    public void onDisable() {
        saveConfig();
    }

    private void registerListeners(Listener... listeners) {

        PluginManager pm = Bukkit.getPluginManager();

        for (Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }

    }

    public GameManager getEventManager() {
        return eventManager;
    }

    public WorldEditPlugin getWorldEditPlugin() {
        return worldEditPlugin;
    }

    public static EventsPlugin getInstance() {
        return instance;
    }
}
