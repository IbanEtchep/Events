package fr.iban.events;

import fr.iban.events.enums.GameType;
import fr.iban.events.games.*;
import fr.iban.events.options.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GameManager {


    private final EventsPlugin plugin;
    private final FileConfiguration config;
    private final List<Game> runningGames = new ArrayList<>();

    public GameManager(EventsPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        registerGameHandlers();
    }

    private void registerGameHandlers() {
        GameType.JUMP.registerHandler(() -> new JumpGame(plugin));
        GameType.DROPPER.registerHandler(() -> new DropperGame(plugin));
        GameType.TNTRUN.registerHandler(() -> new TNTRunGame(plugin));
        GameType.SUMOTORI.registerHandler(() -> new SumotoriGame(plugin));
        GameType.SNOWBATTLE.registerHandler(() -> new SnowBattleGame(plugin));
        GameType.PITCHOUT.registerHandler(() -> new PitchOutGame(plugin));
        if(plugin.getIceRacePlugin() != null) {
            GameType.ICERACE.registerHandler(() -> new IceRaceGame(plugin));
        }
    }

    public List<Game> getRunningEvents() {
        return runningGames;
    }

    /**
     * Vérifie si un event est en cours à l'arène donnée
     *
     * @param type  - type d'event
     * @param arena - nom de l'arene
     * @return - si l'event est en cours.
     */
    public boolean isRunning(GameType type, String arena) {
        return !runningGames.isEmpty() && !getAvalaibleArenas(type).contains(arena);
    }

    /**
     * Débuter un event
     *
     * @param type   - type d'evenement.
     * @param player - joueur qui hoste la partie.
     */
    public void runEvent(GameType type, Player player, String arena) {
        Game game = type.getNewHandler();
        if (game != null) {
            runningGames.add(game);
            game.prepare(player, arena);
        } else {
            player.sendMessage("§cNo game handler found.");
        }
    }

    public void killEvent(Game game) {
        getRunningEvents().remove(game);
    }

    /**
     * Vérifie si un joueur est en train de jouer à un event.
     *
     * @return - event
     */
    public boolean isPlaying(Player player) {
        return getPlayingGame(player) != null;
    }


    /**
     * Renvois l'event auquel le joueur joue actuellement.
     *
     * @return - event ou null si non trouvé.
     */
    @Nullable
    public Game getPlayingGame(Player player) {
        for (Game game : runningGames) {
            if(game.getPlayers().contains(player.getUniqueId())) {
                return game;
            }
        }
        return null;
    }

    @Nullable
    public Game getNearestEvent(Player player) {
        Game game = null;
        for (Game ev : runningGames) {
            double evDistance = ev.getWaitingSpawnPoint().distanceSquared(player.getLocation());
            if (evDistance < 10000 &&
                    (game == null || evDistance < game.getWaitingSpawnPoint().distanceSquared(player.getLocation()))) {
                game = ev;
            }
        }
        return game;
    }

    public List<String> getArenaNames(GameType type) {
        List<String> list = new ArrayList<>();
        ConfigurationSection section = config.getConfigurationSection(type.toString().toLowerCase());
        if (section != null) {
            list.addAll(section.getKeys(false));
        }
        return list;
    }

    public List<String> getAvalaibleArenas(GameType type) {
        List<String> list = getArenaNames(type);
        //On enlève les arènes en cours d'utilisation.
        for (Game game : runningGames.stream().filter(e -> e.getType() == type).toList()) {
            list.remove(game.getArena());
        }
        return list;
    }

    public List<Option<?>> getArenaOptions(GameType event, String arenaName) {
        List<Option<?>> list = new ArrayList<>();
        String path = event.toString().toLowerCase() + "." + arenaName + ".";
        for (Option<?> option : event.getArenaOptions()) {
            list.add(option);
            if (option instanceof IntOption intOption) {
                intOption.setValue(config.getInt(path + option.getName()));
            } else if (option instanceof StringOption stringOption) {
                stringOption.setValue(config.getString(path + option.getName()));
            } else if (option instanceof LocationOption locationOption) {
                locationOption.setValue(config.getLocation(path + option.getName()));
            }
        }
        return list;
    }

    public void saveArenaOption(GameType type, String arenaName, Option<?> option) {
        String path = type.toString().toLowerCase() + "." + arenaName + "." + option.getName();
        if (option instanceof IntOption intOption) {
            config.set(path, intOption.getValue());
        } else if (option instanceof StringOption stringOption) {
            config.set(path, stringOption.getValue());
        } else if (option instanceof LocationOption locationOption) {
            config.set(path, locationOption.getValue());
        } else if (option instanceof LocationListOption locationListOption) {
            config.set(path, locationListOption.getValue());
        } else if (option instanceof CuboidListOption cuboidListOption) {
            config.set(path, cuboidListOption.getValue());
        }
    }

    public void addArena(GameType type, String arenaName) {
        String path = type.toString().toLowerCase() + "." + arenaName;
        config.createSection(path);
    }

    public void deleteArena(GameType type, String arenaName) {
        String path = type.toString().toLowerCase() + "." + arenaName;
        config.set(path, null);
    }

}
