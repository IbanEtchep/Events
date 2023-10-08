package fr.iban.events;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import fr.iban.events.commands.EventCMD;
import fr.iban.events.commands.HostCMD;
import fr.iban.events.listeners.*;
import fr.iban.events.managers.DatabaseManager;
import fr.iban.icerace.IceRacePlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class EventsPlugin extends JavaPlugin {

    private static EventsPlugin instance;
    private GameManager gameManager;
    private DatabaseManager databaseManager;
    private WorldEditPlugin worldEditPlugin;
    private IceRacePlugin iceRacePlugin;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        Plugin worldEditPlugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (worldEditPlugin instanceof WorldEditPlugin worldedit) {
            this.worldEditPlugin = worldedit;
            getLogger().info("Hooked into worldedit.");
        }

        Plugin iceRacePlugin = Bukkit.getPluginManager().getPlugin("IceRace");
        if (iceRacePlugin instanceof IceRacePlugin iceRace) {
            this.iceRacePlugin = iceRace;
            getLogger().info("Hooked into IceRace.");
        }

        gameManager = new GameManager(this);
        databaseManager = new DatabaseManager(this);

        getCommand("event").setExecutor(new EventCMD(this));
        getCommand("host").setExecutor(new HostCMD(this));

        registerListeners(new PlayerMoveListener(this),
                new JoinQuitListeners(getGameManager()),
                new BlockListeners(this),
                new TeleportListener(gameManager),
                new DamageListeners(gameManager),
                new JoinQuitListeners(gameManager),
                new FoodListener(),
                new ProjectileDamageListener(gameManager),
                new ArmorListener(this)
        );
    }

    private void registerListeners(Listener... listeners) {

        PluginManager pm = Bukkit.getPluginManager();

        for (Listener listener : listeners) {
            pm.registerEvents(listener, this);
        }

    }

    public GameManager getGameManager() {
        return gameManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public WorldEditPlugin getWorldEditPlugin() {
        return worldEditPlugin;
    }

    public IceRacePlugin getIceRacePlugin() {
        return iceRacePlugin;
    }

    public static EventsPlugin getInstance() {
        return instance;
    }
}
