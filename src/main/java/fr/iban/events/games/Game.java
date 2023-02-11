package fr.iban.events.games;

import fr.iban.bukkitcore.menu.Menu;
import fr.iban.bukkitcore.rewards.Reward;
import fr.iban.bukkitcore.rewards.RewardsDAO;
import fr.iban.common.teleport.SLocation;
import fr.iban.events.EventsPlugin;
import fr.iban.events.GameConfig;
import fr.iban.events.GameManager;
import fr.iban.events.enums.GameType;
import fr.iban.events.enums.GameState;
import fr.iban.events.menus.ConfigMenu;
import fr.iban.events.options.IntOption;
import fr.iban.events.options.Option;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.*;

public abstract class Game {

    protected UUID host;
    protected HashMap<String, Option<?>> cachedArenaOptions;
    protected Set<UUID> players = new HashSet<>();
    protected Set<UUID> winners = new HashSet<>();
    protected Menu menu;
    protected GameState state = GameState.WAITING;
    protected GameManager manager;
    protected EventsPlugin plugin;
    protected GameConfig gameConfig = new GameConfig("default");
    protected String arena;


    public Game(EventsPlugin plugin) {
        this.plugin = plugin;
        this.manager = plugin.getGameManager();
    }


    public abstract boolean isFinished();

    public abstract Location getWaitingSpawnPoint();

    public abstract Location getStartPoint();

    public abstract GameType getType();

    public void start() {
        for (Player player : getViewers(getWaitingSpawnPoint(), 100)) {
            if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
                continue;
            handlePlayerGameJoin(player);
        }
        setGameState(GameState.RUNNING);
    }

    public void handlePlayerGameJoin(Player player) {
        if (!getPlayers().contains(player.getUniqueId())) {
            broadCastMessage("§7" + player.getName() + " a rejoint la partie !");
            getPlayers().add(player.getUniqueId());
        }
    }

    public void finish() {
        setGameState(GameState.FINISHED);

        Reward winReward = gameConfig.getWinReward();
        if (winReward != null) {
            for (UUID uuid : winners) {
                RewardsDAO.addRewardAsync(uuid.toString(), winReward.getName(), winReward.getServer(), winReward.getCommand());
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage("§aVous avez reçu une récompense pour votre victoire ! (/recompenses)");
            }
        }

        for (UUID uuid : winners) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                removePlayer(player);
            }
        }

        manager.killEvent(this);
    }

    public void removePlayer(Player player, boolean reward) {
        UUID uuid = player.getUniqueId();
        if (state == GameState.WAITING) {
            broadCastMessage("§7" + player.getName() + " a quitté la partie !");
        } else {
            Objects.requireNonNull(player.getAttribute(Attribute.GENERIC_ATTACK_SPEED)).setBaseValue(4);
            player.teleport(getWaitingSpawnPoint());
            player.getInventory().clear();
            if (!player.getActivePotionEffects().isEmpty()) {
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
            }

            Reward participationReward = getConfig().getParticipationReward();
            if (participationReward != null && !winners.contains(uuid)) {
                RewardsDAO.addRewardAsync(uuid.toString(), participationReward.getName(), participationReward.getServer(), participationReward.getCommand());
                player.sendMessage("§aVous avez reçu une récompense pour votre participation ! (/recompenses)");
            }
        }
        getPlayers().remove(uuid);
    }

    public void removePlayer(Player player) {
        removePlayer(player, true);
    }

    public void prepare(Player player, String arena) {
        host = player.getUniqueId();
        this.arena = arena;
        if (menu == null) {
            menu = new ConfigMenu(player, this);
        }
        menu.open();
        player.teleport(getWaitingSpawnPoint());
    }

    public Set<UUID> getPlayers() {
        return players;
    }

    public String getName() {
        return getType().getName();
    }

    public UUID getHost() {
        return host;
    }

    public Menu getConfigMenu() {
        return menu;
    }

    public String getArena() {
        return arena;
    }

    public GameState getGameState() {
        return state;
    }

    public void setGameState(GameState state) {
        this.state = state;
    }

    public Collection<Player> getViewers(int distance) {
        return getStartPoint().getNearbyPlayers(distance);
    }

    public Collection<Player> getViewers(Location loc, int distance) {
        return loc.getNearbyPlayers(distance);
    }

    public GameConfig getConfig() {
        return gameConfig;
    }

    public void broadCastMessage(String message) {
        for (Player p : getViewers(50)) {
            p.sendMessage(message);
        }
    }

    public SLocation getWaitSLocation() {
        Location loc = getWaitingSpawnPoint();
        return new SLocation("Events", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
    }

    protected Option<?> getOption(String name) {
        if(cachedArenaOptions == null) {
            cachedArenaOptions = new HashMap<>();
            for (Option<?> arenaOption : manager.getArenaOptions(getType(), getArena())) {
                cachedArenaOptions.put(arenaOption.getName(), arenaOption);
            }
        }
        return cachedArenaOptions.get(name);
    }

}