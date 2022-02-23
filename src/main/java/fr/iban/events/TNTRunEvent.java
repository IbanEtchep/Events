package fr.iban.events;

import fr.iban.events.enums.EventType;
import fr.iban.events.enums.GameState;
import fr.iban.events.interfaces.MoveBlockListener;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashMap;
import java.util.Map;

public class TNTRunEvent extends LastToFallEvent implements MoveBlockListener {

    private final Map<Location, Material> brokenBlocks = new HashMap<>();
    private boolean blockFallStarted = false;

    public TNTRunEvent(EventsPlugin plugin) {
        super(plugin);
    }

    @Override
    public EventType getType() {
        return EventType.TNTRUN;
    }

    @Override
    public void start() {
        super.start();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            blockFallStarted = true;
        }, 100L);
    }

    @Override
    public void finish() {
        super.finish();
        regenArena();
    }

    private void regenArena() {
        for (Map.Entry<Location, Material> entry : brokenBlocks.entrySet()) {
            entry.getKey().getBlock().setType(entry.getValue());
        }
    }

    @Override
    public void onMoveBlock(PlayerMoveEvent e) {
        super.onMoveBlock(e);
        if (getGameState() == GameState.RUNNING && blockFallStarted) {
            Location to = e.getTo();
            Block blockTo = to.clone().add(0, -1, 0).getBlock();
            Block blockToLessOne = to.clone().add(0, -2, 0).getBlock();
            Material material = blockTo.getType();

            if ((material == Material.SAND || material == Material.GRAVEL) &&
                    blockToLessOne.getType() == Material.TNT) {
                Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    brokenBlocks.put(blockTo.getLocation(), blockTo.getType());
                    brokenBlocks.put(blockToLessOne.getLocation(), blockToLessOne.getType());
                    blockTo.setType(Material.AIR);
                    blockToLessOne.setType(Material.AIR);
                }, 10L);
            }
        }
    }
}