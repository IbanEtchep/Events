package fr.iban.events.games;

import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.GameType;
import fr.iban.events.enums.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TNTRunGame extends LastToFallGame {

    private final Map<Location, Material> brokenBlocks = new HashMap<>();

    public TNTRunGame(EventsPlugin plugin) {
        super(plugin);
    }

    @Override
    public GameType getType() {
        return GameType.TNTRUN;
    }

    @Override
    public void start() {
        super.start();

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (getGameState() == GameState.RUNNING) {
                for (UUID uuid : getPlayers()) {
                    Player player = Bukkit.getPlayer(uuid);
                    if (player == null) {
                        continue;
                    }

                    Location loc = getPlayerStandOnBlockLocation(player.getLocation().clone().add(0, -1, 0));
                    Block blockTo = loc.getBlock();
                    Block blockToLessOne = loc.clone().add(0, -1, 0).getBlock();
                    Material material = blockTo.getType();

                    if ((material == Material.SAND || material == Material.GRAVEL)
                            && blockToLessOne.getType() == Material.TNT) {
                        Bukkit.getScheduler().runTaskLater(plugin, () -> {
                            brokenBlocks.putIfAbsent(blockTo.getLocation(), blockTo.getType());
                            brokenBlocks.putIfAbsent(blockToLessOne.getLocation(), blockToLessOne.getType());
                            blockTo.setType(Material.AIR);
                            blockToLessOne.setType(Material.AIR);
                        }, 10L);
                    }
                }
            }
        }, 100L, 1L);
    }

    @Override
    public void finish() {
        super.finish();
        Bukkit.getScheduler().runTaskLater(plugin, this::regenArena, 60L);
    }

    private void regenArena() {
        for (Map.Entry<Location, Material> entry : brokenBlocks.entrySet()) {
            Material material = entry.getValue();
            if (material == Material.TNT) {
                entry.getKey().getBlock().setType(material);
            }
        }
        for (Map.Entry<Location, Material> entry : brokenBlocks.entrySet()) {
            Material material = entry.getValue();
            if (material != Material.TNT) {
                entry.getKey().getBlock().setType(material);
            }
        }
    }

    private Location getPlayerStandOnBlockLocation(Location locationUnderPlayer) {
        Location b11 = locationUnderPlayer.clone().add(0.3, 0, -0.3);
        if (b11.getBlock().getType() != Material.AIR) {
            return b11;
        }
        Location b12 = locationUnderPlayer.clone().add(-0.3, 0, -0.3);
        if (b12.getBlock().getType() != Material.AIR) {
            return b12;
        }
        Location b21 = locationUnderPlayer.clone().add(0.3, 0, 0.3);
        if (b21.getBlock().getType() != Material.AIR) {
            return b21;
        }
        Location b22 = locationUnderPlayer.clone().add(-0.3, 0, +0.3);
        if (b22.getBlock().getType() != Material.AIR) {
            return b22;
        }
        return locationUnderPlayer;
    }
}