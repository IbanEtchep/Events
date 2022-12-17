package fr.iban.events;

import fr.iban.common.teleport.SLocation;
import fr.iban.events.cooldown.CooldownManager;
import fr.iban.events.enums.EventType;
import fr.iban.events.enums.GameState;
import fr.iban.events.interfaces.ProjectileListener;
import fr.iban.events.options.LocationOption;
import fr.iban.events.options.Option;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class SnowEvent extends Event implements ProjectileListener {

    private HashMap<Player, Integer> lifes = new HashMap<Player, Integer>();
    private final CooldownManager cooldownManager = new CooldownManager();
    private boolean finished = false;
    private boolean throwable = false;

    public SnowEvent(EventsPlugin plugin) {
        super(plugin);
        this.damage = true;
    }

    @Override
    public void start() {
        super.start();

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            player.getInventory().clear();
            for(int i = 0; i<9; i++) {
                player.getInventory().addItem(new ItemStack(Material.SNOWBALL, 64));
            }
            lifes.put(player, 5);
            player.setSaturatedRegenRate(0);
            player.teleport(getStartPoint());
            player.sendTitle("§l§2Bonne chance ! ", "§aQue le meilleur gagne !", 10, 70, 20);
            player.playNote(player.getLocation(), Instrument.BASS_DRUM, Note.flat(1, Note.Tone.A));
        }

        new BukkitRunnable() {
            private int timer = 5;
            @Override
            public void run() {
                Bukkit.broadcastMessage("§aActivation des dégats dans " + timer);
                if(timer == 0) {
                    throwable = true;
                    cancel();
                }
                timer--;
            }
        }.runTaskTimer(plugin, 0, 20);
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
    public void finish() {
        super.finish();
        state = GameState.FINISHED;
        UUID winner = winners.get(0);
        for(Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("§2§lLa partie est terminée, " + Bukkit.getPlayer(winner).getName() + " a gagné !");
        }
    }

    @Override
    public void onProjectileEvent(ProjectileHitEvent e) {
        Player p = (Player) e.getHitEntity();
        int v = lifes.get(p);
        lifes.replace(p, v-1);
        if(lifes.get(p) != 0) {
            p.sendMessage("§4§lTOUCHÉ: §4" + lifes.get(p) + " vies restante(s)");
        }
        if(lifes.get(p) == 0) {
            removePlayer(p.getUniqueId());
        }
        if(players.size() == 1) {
            winners.add(players.get(0));
            finish();
        }
    }

    @Override
    public void onThrow(ProjectileLaunchEvent e) {
        if(!throwable) {
            e.setCancelled(true);
        }
        Player p = (Player) e.getEntity().getShooter();
        int timeLeft = cooldownManager.getCooldown(p);
        if(timeLeft == 0) {
            cooldownManager.setCooldown(p, 2);
            new BukkitRunnable() {
                @Override
                public void run() {
                    int timeLeft = cooldownManager.getCooldown(p);
                    cooldownManager.setCooldown(p, --timeLeft);
                    if(timeLeft == 0) {
                        this.cancel();
                    }
                }
            }.runTaskTimer(plugin, 0, 20);
        } else {
            e.setCancelled(true);
        }
    }

}
