package fr.iban.events;

import fr.iban.common.teleport.SLocation;
import fr.iban.events.Event;
import fr.iban.events.enums.EventType;
import fr.iban.events.interfaces.PlayerDamageListener;
import fr.iban.events.interfaces.ProjectileListener;
import fr.iban.events.options.LocationOption;
import fr.iban.events.options.Option;
import org.bukkit.Bukkit;
import org.bukkit.Instrument;
import org.bukkit.Location;
import org.bukkit.Note;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SnowEvent extends Event implements ProjectileListener,PlayerDamageListener {

    private HashMap<Player, Integer> lifes = new HashMap<Player, Integer>();
    private boolean finished = false;

    public SnowEvent(EventsPlugin plugin) {
        super(plugin);
    }

    @Override
    public void start() {
        super.start();

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            player.setCollidable(false);
            player.teleport(getStartPoint());
            player.sendTitle("§l§2Bonne chance ! ", "§aQue le meilleur gagne !", 10, 70, 20);
            player.playNote(player.getLocation(), Instrument.BASS_DRUM, Note.flat(1, Note.Tone.A));
        }
    }

    public static List<Option> getArenaOptions() {
        List<Option> list = new ArrayList<>();
        list.add(new LocationOption("waiting-location"));
        list.add(new LocationOption("game-start-location"));
        return list;
    }

    @Override
    public boolean isFinished() {
        return finished;
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

    @Override
    public SLocation getWaitSLocation() {
        Location loc = getWaitingSpawnPoint();
        return new SLocation("Events", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
    }

    @Override
    public EventType getType() {
        return EventType.SNOWBATTLE;
    }

    @Override
    public void onProjectileEvent(ProjectileHitEvent e) {
        Player p = (Player) e.getHitEntity();
        Projectile projectile = (Projectile) e.getEntity();
        if(projectile.getType().equals(EntityType.SNOWBALL)) {
            p.damage(5);
        }
    }

    @Override
    public void onPlayerDamage(EntityDamageEvent e) {
        Player p = (Player) e.getEntity();
        if(p.isDead()) {
            removePlayer(p.getUniqueId());
            if(getPlayers().size() == 1) {
                winners.add(getPlayers().get(0));
                finish();
            }
        }
    }
}
