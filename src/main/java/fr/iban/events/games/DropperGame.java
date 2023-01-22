package fr.iban.events.games;

import fr.iban.common.teleport.SLocation;
import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.GameType;
import fr.iban.events.interfaces.MoveBlockListener;
import fr.iban.events.interfaces.PlayerDamageListener;
import fr.iban.events.options.LocationOption;
import fr.iban.events.options.Option;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.*;

public class DropperGame extends Game implements MoveBlockListener, PlayerDamageListener {

    private final Map<UUID, Location> checkpoints = new HashMap<>();
    private final Map<UUID, Integer> maps = new HashMap<>();
    private boolean finished = false;

    public DropperGame(EventsPlugin plugin) {
        super(plugin);
        getConfig().setPvp(true);
    }

    @Override
    public void start() {
        super.start();

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            player.setCollidable(false);
            player.teleport(getStartPoint());
            player.sendTitle("§l§2Bonne chance ! ", "§aQue le meilleur gagné !", 10, 70, 20);
            player.playNote(player.getLocation(), Instrument.BASS_DRUM, Note.flat(1, Note.Tone.A));
            maps.put(player.getUniqueId(), 1);
        }
    }

    public static List<Option> getArenaOptions() {
        List<Option> list = new ArrayList<>();
        list.add(new LocationOption("waiting-location"));
        list.add(new LocationOption("game-start-location"));
        list.add(new LocationOption("map1"));
        list.add(new LocationOption("map2"));
        list.add(new LocationOption("map3"));
        list.add(new LocationOption("game-end-location"));
        return list;
    }

    @Override
    public boolean isNotFinished() {
        return !finished;
    }

    public Location getMapLocation(int i) {
        LocationOption locopt = (LocationOption) manager.getArenaOptions(getType(), getArena()).get(1+i);
        return locopt.getLocationValue();
    }

    @Override
    public Location getWaitingSpawnPoint() {
        LocationOption locopt = (LocationOption) manager.getArenaOptions(getType(), getArena()).get(0);
        return locopt.getLocationValue();
    }

    @Override
    public Location getStartPoint() {
        LocationOption locopt = (LocationOption) manager.getArenaOptions(getType(), getArena()).get(1);
        return locopt.getLocationValue();
    }

    public Location getEndPoint() {
        LocationOption locopt = (LocationOption) manager.getArenaOptions(getType(), getArena()).get(5);
        return locopt.getLocationValue();
    }

    @Override
    public SLocation getWaitSLocation() {
        Location loc = getWaitingSpawnPoint();
        return new SLocation("Events", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
    }

    @Override
    public GameType getType() {
        return GameType.DROPPER;
    }

    @Override
    public void onMoveBlock(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        Location to = e.getTo();
        Block toBlock = to.getBlock();

        if (toBlock.getType() == Material.LIGHT_WEIGHTED_PRESSURE_PLATE) {
            if (!getCheckPoint(player).getBlock().getLocation().equals(toBlock.getLocation())) {
                checkpoints.put(player.getUniqueId(), toBlock.getLocation());
                int map = maps.get(player.getUniqueId());
                maps.replace(player.getUniqueId(), map, map+1);
                player.sendMessage("§aVous avez atteint un checkpoint.");
            }
        }

        if(toBlock.getLocation().getY() <= 10) {
            int map = maps.get(player.getUniqueId());
            Location loc = getMapLocation(map+1);
            player.teleport(loc);
        }

    }

    @Override
    public void onPlayerDamage(EntityDamageEvent e) {
        Player p = (Player) e.getEntity();
        if(e.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            int map = maps.get(p.getUniqueId());
            Location loc = getMapLocation(map);
            p.teleport(loc);
        }
    }

    private Location getCheckPoint(Player player) {
        return checkpoints.getOrDefault(player.getUniqueId(), getStartPoint());
    }
}
