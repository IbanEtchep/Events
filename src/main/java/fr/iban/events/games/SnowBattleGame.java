package fr.iban.events.games;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import fr.iban.bukkitcore.utils.Head;
import fr.iban.common.teleport.SLocation;
import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.GameState;
import fr.iban.events.enums.GameType;
import fr.iban.events.interfaces.ArmorChangeListener;
import fr.iban.events.interfaces.ProjectileListener;
import fr.iban.events.managers.CooldownManager;
import fr.iban.events.options.LocationOption;
import fr.iban.events.options.Option;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class SnowBattleGame extends Game implements ProjectileListener, ArmorChangeListener {

    private final HashMap<Player, Integer> lifes = new HashMap<>();
    private final CooldownManager cooldownManager = new CooldownManager();
    private boolean finished = false;
    private boolean throwable = false;

    public SnowBattleGame(EventsPlugin plugin) {
        super(plugin);
        getConfig().setDamage(true);
    }

    @Override
    public boolean isFinished() {
        return finished;
    }

    @Override
    public void start() {
        super.start();

        new BukkitRunnable() {
            private int timer = 10;

            @Override
            public void run() {
                if (timer == 0) {
                    throwable = true;
                    cancel();
                }

                if (timer % 5 == 0 || timer < 5) {
                    Bukkit.broadcastMessage("§aActivation des dégats dans " + timer + "s");
                }

                timer--;
            }
        }.runTaskTimer(plugin, 0, 20);
    }

    @Override
    public void handlePlayerGameJoin(Player player) {
        super.handlePlayerGameJoin(player);
        player.getInventory().clear();
        ItemStack head5 = Head.getByID(String.valueOf(9158));
        player.getInventory().setHelmet(head5);
        for (int i = 0; i < 9; i++) {
            player.getInventory().addItem(new ItemStack(Material.SNOWBALL, 64));
        }
        lifes.put(player, 5);
        player.setSaturatedRegenRate(0);
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
        player.teleport(getStartPoint());
        player.sendTitle("§l§2Bonne chance ! ", "§aQue le meilleur gagne !", 10, 70, 20);
        player.playNote(player.getLocation(), Instrument.BASS_DRUM, Note.flat(1, Note.Tone.A));
    }

    @Override
    public void removePlayer(Player player, boolean reward) {
        super.removePlayer(player, reward);
        player.setSaturatedRegenRate(10);
    }

    public static List<Option<?>> getArenaOptions() {
        List<Option<?>> list = new ArrayList<>();
        list.add(new LocationOption("waiting-location"));
        list.add(new LocationOption("game-start-location"));
        return list;
    }

    @Override
    public Location getWaitingSpawnPoint() {
        LocationOption locopt = (LocationOption) manager.getArenaOptions(getType(), getArena()).get(0);
        return locopt.getValue();
    }

    @Override
    public Location getStartPoint() {
        LocationOption locopt = (LocationOption) manager.getArenaOptions(getType(), getArena()).get(1);
        return locopt.getValue();
    }

    @Override
    public SLocation getWaitSLocation() {
        Location loc = getWaitingSpawnPoint();
        return new SLocation("Events", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
    }

    @Override
    public GameType getType() {
        return GameType.SNOWBATTLE;
    }

    @Override
    public void finish() {
        super.finish();
        state = GameState.FINISHED;
        if (winners.isEmpty()) return;
        UUID winner = winners.stream().findFirst().get();
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendMessage("§2§lLa partie est terminée, " + Objects.requireNonNull(Bukkit.getPlayer(winner)).getName() + " a gagné !");
        }
    }

    @Override
    public void onProjectileEvent(ProjectileHitEvent e) {
        Player p = (Player) e.getHitEntity();
        Player thrower = (Player) e.getEntity().getShooter();
        int v = lifes.get(p);
        lifes.replace(p, v - 1);
        if (lifes.get(p) != 0) {
            int vies = lifes.get(p);
            ItemStack head = Head.getByID(String.valueOf(9158 + 5 - vies));
            p.getInventory().setHelmet(head);
            p.sendMessage("§4§lTOUCHÉ: §4" + lifes.get(p) + " vies restante(s)");
            p.playSound(p.getLocation(), Sound.BLOCK_ANVIL_PLACE, 0.7F, 0.7F);
            thrower.playSound(p.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.7F, 0.7F);
        }
        if (lifes.get(p) == 0) {
            removePlayer(p);
            broadCastMessage("§7" + p.getName() + " a été éliminé par " + thrower.getName() + " - " + players.size() + " joueur(s) restant(s)");
            Bukkit.getPlayer(p.getUniqueId()).getInventory().setHelmet(null);
            if (lifes.get(thrower) < 5) {
                lifes.replace(thrower, lifes.get(thrower) + 1);
                int vies = lifes.get(thrower);
                ItemStack head = Head.getByID(String.valueOf(9158 + 5 - vies));
                thrower.getInventory().setHelmet(head);
                thrower.sendMessage("§aTu as gagné une vie pour avoir tué §c" + p.getName());
            }
        }
        if (players.size() == 1) {
            winners.add(players.stream().findFirst().get());
            finish();
        }
    }

    @Override
    public void onThrow(ProjectileLaunchEvent e) {
        if (!throwable) {
            e.setCancelled(true);
            return;
        }
        if (e.getEntity().getShooter() instanceof Player player) {
            int timeLeft = cooldownManager.getCooldown(player);
            if (timeLeft == 0) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 3 * 20, 0, false));
                cooldownManager.setCooldown(player, 2);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        int timeLeft = cooldownManager.getCooldown(player);
                        cooldownManager.setCooldown(player, --timeLeft);
                        if (timeLeft == 0) {
                            this.cancel();
                        }
                    }
                }.runTaskTimerAsynchronously(plugin, 0, 20);
            } else {
                e.setCancelled(true);
            }
        }
    }

    @Override
    public void onArmorChange(PlayerArmorChangeEvent e) {
        Player p = e.getPlayer();
        if (e.getSlotType() == PlayerArmorChangeEvent.SlotType.HEAD) {
            int vies = lifes.get(p);
            ItemStack head = Head.getByID(String.valueOf(9158 + 5 - vies));
            p.getInventory().setHelmet(head);
        }
    }
}
