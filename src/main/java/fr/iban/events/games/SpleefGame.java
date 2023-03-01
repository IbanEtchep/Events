package fr.iban.events.games;

import fr.iban.bukkitcore.utils.ItemBuilder;
import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.GameState;
import fr.iban.events.enums.GameType;
import fr.iban.events.interfaces.BlockBreakListener;
import fr.iban.icerace.util.TimerTask;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpleefGame extends LastToFallGame implements BlockBreakListener {

    private final Map<Location, Material> brokenBlocks = new HashMap<>();
    private boolean allowBlockBreak = false;

    public SpleefGame(EventsPlugin plugin) {
        super(plugin);
    }

    @Override
    public GameType getType() {
        return GameType.SPEEF;
    }

    @Override
    public void start() {
        super.start();
        new TimerTask(6, second -> {
            if(second == 6) {
                broadCastMessage("§c§lVous pourrez casser les blocs dans...");
            }
            if (second == 5 || second == 4 || second == 3 || second == 2 || second == 1) {
                broadCastMessage("§c§l"+second);
            }
            if (second == 0) {
                broadCastMessage("§4§lGO !");
                allowBlockBreak = true;
            }
        }).start(plugin);
    }

    @Override
    public void handlePlayerGameJoin(Player player) {
        super.handlePlayerGameJoin(player);
        player.getInventory().addItem(new ItemBuilder(Material.GOLDEN_SHOVEL).setName("§").addEnchant(Enchantment.DURABILITY, 100).build());
        player.setGameMode(GameMode.SURVIVAL);
    }

    @Override
    public void finish() {
        super.finish();
        Bukkit.getScheduler().runTaskLater(plugin, this::regenArena, 60L);
    }

    private void regenArena() {
        for (Map.Entry<Location, Material> entry : brokenBlocks.entrySet()) {
            Material material = entry.getValue();
            entry.getKey().getBlock().setType(material);
        }
    }


    @Override
    public void onBlockBreak(BlockBreakEvent e) {
        if(!allowBlockBreak) {
            e.setCancelled(true);
            return;
        }

        Block block = e.getBlock();
        e.setDropItems(false);
        brokenBlocks.put(block.getLocation(), block.getType());
    }
}