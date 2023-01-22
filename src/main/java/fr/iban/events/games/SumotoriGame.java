package fr.iban.events.games;

import fr.iban.bukkitcore.utils.ItemBuilder;
import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.GameType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SumotoriGame extends LastToFallGame {

    public SumotoriGame(EventsPlugin plugin) {
        super(plugin);
        this.getConfig().setPvp(true);
    }

    @Override
    public void handlePlayerGameJoin(Player player) {
        super.handlePlayerGameJoin(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
        player.getInventory().addItem(new ItemBuilder(Material.STICK).setName("§").addEnchant(Enchantment.KNOCKBACK, 1).build());
        player.setMaximumNoDamageTicks(15);
    }

    public void removePlayer(Player player) {
        super.removePlayer(player);
        if (player != null) {
            player.setMaximumNoDamageTicks(20);
        }
    }

    @Override
    public GameType getType() {
        return GameType.SUMOTORI;
    }
}
