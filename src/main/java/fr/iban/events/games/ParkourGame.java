package fr.iban.events.games;

import fr.iban.bukkitcore.rewards.Reward;
import fr.iban.bukkitcore.rewards.RewardsDAO;
import fr.iban.common.teleport.SLocation;
import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.GameType;
import fr.iban.events.enums.GameState;
import fr.iban.events.interfaces.MoveBlockListener;
import fr.iban.events.interfaces.PlayerDamageListener;
import fr.iban.events.options.LocationOption;
import fr.iban.events.options.Option;
import org.bukkit.*;
import org.bukkit.Note.Tone;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class ParkourGame extends Game implements MoveBlockListener, PlayerDamageListener {

    private final Map<UUID, Location> checkpoints = new HashMap<>();
    private boolean finished = false;

    public ParkourGame(EventsPlugin plugin) {
        super(plugin);
    }

    public static List<Option<?>> getArenaOptions() {
        List<Option<?>> list = new ArrayList<>();
        list.add(new LocationOption("waiting-location"));
        list.add(new LocationOption("game-start-location"));
        list.add(new LocationOption("game-end-location"));
        return list;
    }

    @Override
    public void handlePlayerGameJoin(Player player) {
        player.teleport(getStartPoint());
        player.sendTitle("§l§2Bonne chance ! ", "§aQue le meilleur gagné !", 10, 70, 20);
        player.playNote(player.getLocation(), Instrument.BASS_DRUM, Note.flat(1, Tone.A));
    }

    @Override
    public Location getWaitingSpawnPoint() {
        LocationOption locopt = (LocationOption) manager.getArenaOptions(getType(), getArena()).get(0);
        return locopt.getValue();
    }

    public Location getStartPoint() {
        LocationOption locopt = (LocationOption) manager.getArenaOptions(getType(), getArena()).get(1);
        return locopt.getValue();
    }

    public Location getEndPoint() {
        LocationOption locopt = (LocationOption) manager.getArenaOptions(getType(), getArena()).get(2);
        return locopt.getValue();
    }

    @Override
    public boolean isNotFinished() {
        return !finished;
    }

    @Override
    public GameType getType() {
        return GameType.JUMP;
    }

    @Override
    public void onMoveBlock(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location to = e.getTo();
        Block toBlock = to.getBlock();

        if (toBlock.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            if (!getCheckPoint(player).getBlock().getLocation().equals(toBlock.getLocation())) {
                checkpoints.put(player.getUniqueId(), toBlock.getLocation());
                player.sendMessage("§aVous avez atteint un checkpoint.");
            }
        }

        if (to.distanceSquared(getEndPoint()) <= 1) {

            if (isNotFinished()) {
                Reward winReward = gameConfig.getWinReward();
                if (winReward != null) {
                    RewardsDAO.addRewardAsync(player.getUniqueId().toString(), winReward.getName(), winReward.getServer(), winReward.getCommand());
                    player.sendMessage("§aVous avez reçu une récompense pour votre victoire ! (/recompenses)");
                }
                winners.add(player.getUniqueId());
                finished = true;
            }

            for (Player p : getViewers(250)) {
                p.sendMessage("§2§l" + player.getName() + " a atteint l'arrivée !");
            }
            removePlayer(player);
            if (players.isEmpty()) {
                finish();
            }

        }

        if (player.getFallDistance() >= 10) {
            player.teleport(getCheckPoint(player));
        }
    }

    @Override
    public void finish() {
        setGameState(GameState.FINISHED);
        manager.killEvent(this);
    }

    private Location getCheckPoint(Player player) {
        return checkpoints.getOrDefault(player.getUniqueId(), getStartPoint());
    }

    @Override
    public void onPlayerDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

}
