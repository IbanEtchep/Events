package fr.iban.events.games;

import com.google.gson.GsonBuilder;
import fr.iban.bukkitcore.rewards.Reward;
import fr.iban.bukkitcore.rewards.RewardsDAO;
import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.GameType;
import fr.iban.events.options.*;
import fr.iban.icerace.IceRace;
import fr.iban.icerace.IceRacePlugin;
import fr.iban.icerace.Track;
import fr.iban.icerace.enums.GameState;
import fr.iban.icerace.event.PlayerRaceFinishEvent;
import fr.iban.icerace.event.RaceFinishEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class IceRaceGame extends Game implements Listener {

    private IceRace race;

    public IceRaceGame(EventsPlugin plugin) {
        super(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void handlePlayerGameJoin(Player player) {
        if (!getPlayers().contains(player.getUniqueId())) {
            getPlayers().add(player.getUniqueId());
            race.addPlayer(player);
        }

    }

    @Override
    public void start() {
        super.start();
        race.start();
    }

    @Override
    public void finish() {
        HandlerList.unregisterAll(this);
        manager.killEvent(this);
    }

    @Override
    public void prepare(Player player, String arena) {
        this.arena = arena;
        IceRacePlugin racePlugin = plugin.getIceRacePlugin();
        Track track = racePlugin.getTrackManager().getTrackByName(arena);
        int laps = ((IntOption)getOption("laps")).getValue();
        plugin.getIceRacePlugin().getGameManager().initRace(player, track, laps);
        race = plugin.getIceRacePlugin().getGameManager().getCurrentRace(track);
        super.prepare(player, arena);
    }

    public static List<Option<?>> getArenaOptions() {
        List<Option<?>> list = new ArrayList<>();
        list.add(new IntOption("laps", 1));
        return list;
    }

    @Override
    public boolean isFinished() {
        return race.getState() == GameState.FINISHED;
    }

    @Override
    public Location getWaitingSpawnPoint() {
        return race.getTrack().getWaitingLocation();
    }

    @Override
    public Location getStartPoint() {
        return race.getTrack().getSpawnPoints().get(0);
    }


    @Override
    public GameType getType() {
        return GameType.ICERACE;
    }

    @EventHandler
    public void onPlayerFinish(PlayerRaceFinishEvent e) {
        Player player = Objects.requireNonNull(e.getRacePlayer().getPlayer());
        UUID uuid = player.getUniqueId();
        if (e.getPosition() == 1) {
            Reward winReward = gameConfig.getWinReward();
            if (winReward != null) {
                RewardsDAO.addRewardAsync(uuid.toString(), winReward.getName(), winReward.getServer(), winReward.getCommand());
                Objects.requireNonNull(Bukkit.getPlayer(uuid)).sendMessage("§aVous avez reçu une récompense pour votre victoire ! (/recompenses)");
            }
        } else {
            Reward participationReward = getConfig().getParticipationReward();
            if (participationReward != null) {
                RewardsDAO.addRewardAsync(uuid.toString(), participationReward.getName(), participationReward.getServer(), participationReward.getCommand());
                player.sendMessage("§aVous avez reçu une récompense pour votre participation ! (/recompenses)");
            }
        }
    }

    @EventHandler
    public void onFinish(RaceFinishEvent e) {
        finish();
    }
}
