package fr.iban.events;

import fr.iban.bukkitcore.utils.ItemBuilder;
import fr.iban.events.enums.EventType;
import fr.iban.events.interfaces.MoveBlockListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class SumotoriEvent extends LastToFallEvent implements MoveBlockListener {

    public SumotoriEvent(EventsPlugin plugin) {
        super(plugin);
        this.pvp = true;
    }

    @Override
    public void start() {
        super.start();

        for (UUID uuid : players) {
            Player player = Bukkit.getPlayer(uuid);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
            player.getInventory().addItem(new ItemBuilder(Material.STICK).setName("§").addEnchant(Enchantment.KNOCKBACK, 1).build());
            player.setMaximumNoDamageTicks(15);
        }
    }

    @Override
    public void removePlayer(UUID uuid) {
        super.removePlayer(uuid);
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setMaximumNoDamageTicks(20);
        }
    }

    @Override
    public EventType getType() {
        return EventType.SUMOTORI;
    }
}
