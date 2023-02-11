package fr.iban.events;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import fr.iban.bukkitcore.utils.Head;
import fr.iban.bukkitcore.utils.ItemBuilder;
import fr.iban.events.enums.GameType;
import fr.iban.events.games.LastToFallGame;
import fr.iban.events.interfaces.ArmorChangeListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class PitchOutGame extends LastToFallGame implements ArmorChangeListener {

    private HashMap<Player, Integer> lifes = new HashMap<Player, Integer>();

    public PitchOutGame(EventsPlugin plugin) {
        super(plugin);
        getConfig().setPvp(true);
    }

    @Override
    public GameType getType() {
        return GameType.PITCHOUT;
    }

    @Override
    public void start() {
        super.start();

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            player.getInventory().clear();
            ItemStack head5 = Head.getByID(String.valueOf(9160));
            player.getInventory().setHelmet(head5);
            lifes.put(player, 3);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            player.getInventory().addItem(new ItemBuilder(Material.BLAZE_ROD).setName("§").addEnchant(Enchantment.KNOCKBACK, 5).build());
            player.getInventory().addItem(new ItemBuilder(Material.BOW).setName("§").addEnchant(Enchantment.ARROW_KNOCKBACK, 4).addEnchant(Enchantment.ARROW_INFINITE, 1).build());
            player.getInventory().addItem(new ItemStack(Material.ARROW));
            player.setMaximumNoDamageTicks(15);
        }

    }

    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        player.getInventory().clear();
        player.setMaximumNoDamageTicks(20);
    }

    @Override
    public void onMoveBlock(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (e.getTo().getY() < getDeathHeight()) {
            int vies = lifes.get(player);
            if (vies == 1) {
                removePlayer(player);
            } else {
                lifes.replace(player, vies - 1);
                int v = lifes.get(player);
                ItemStack head = Head.getByID(String.valueOf(9160 + 3 - vies));
                player.getInventory().setHelmet(head);
                player.teleport(getStartPoint());
                player.sendMessage("§cPlus que " + lifes.get(player) + " vie(s) restante(s) !");
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
