package fr.iban.events.menus;

import fr.iban.bukkitcore.CoreBukkitPlugin;
import fr.iban.bukkitcore.menu.Menu;
import fr.iban.bukkitcore.menu.RewardSelectMenu;
import fr.iban.bukkitcore.rewards.RewardsDAO;
import fr.iban.bukkitcore.utils.Head;
import fr.iban.bukkitcore.utils.ItemBuilder;
import fr.iban.common.messaging.message.EventAnnounce;
import fr.iban.events.games.Game;
import fr.iban.events.tasks.StartTask;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class ConfigMenu extends Menu {

    private final Game game;

    public ConfigMenu(Player player, Game game) {
        super(player);
        this.game = game;
    }

    @Override
    public String getMenuName() {
        return "§2Event §8> §a" + game.getName();
    }

    @Override
    public int getRows() {
        return 1;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        CoreBukkitPlugin core = CoreBukkitPlugin.getInstance();

        if (displayNameEquals(item, "§2§lRécompense")) {
            RewardsDAO.getTemplateRewardsAsync().thenAccept(rewards -> {
                Bukkit.getScheduler().runTask(core, () -> new RewardSelectMenu(player, rewards, reward -> {
                    game.getConfig().setWinReward(reward);
                    open();
                }).open());
            });
        } else if (displayNameEquals(item, "§2§lRécompense de participation")) {
            RewardsDAO.getTemplateRewardsAsync().thenAccept(rewards -> {
                Bukkit.getScheduler().runTask(core, () -> new RewardSelectMenu(player, rewards, reward -> {
                    game.getConfig().setParticipationReward(reward);
                    open();
                }).open());
            });
        } else if (displayNameEquals(item, "§6§lAnnoncer")) {
            core.getMessagingManager().sendMessage("EventAnnounce", new EventAnnounce(game.getName(), game.getArena(), game.getType().getDesc(), game.getWaitSLocation(), player.getName()));
        } else if (displayNameEquals(item, "§2§lLancer !")) {
            new StartTask(game).runTaskTimer(core, 0L, 20L);
        }
    }

    @Override
    public void setMenuItems() {
        setFillerGlass();
        if (game.getConfig().getWinReward() == null) {
            inventory.setItem(1, new ItemBuilder(Head.CHEST.get()).setName("§2§lRécompense").addLore("§aPermet de choisir une récompense.").build());
        } else {
            inventory.setItem(1, new ItemBuilder(Head.CHEST.get()).setName("§2§lRécompense").addLore("§aRécompense choisie : §2" + game.getConfig().getWinReward().getName()).build());
        }

        if (game.getConfig().getParticipationReward() == null) {
            inventory.setItem(2, new ItemBuilder(Head.CHEST.get()).setName("§2§lRécompense de participation").addLore("§aPermet de choisir une récompense.").build());
        } else {
            inventory.setItem(2, new ItemBuilder(Head.CHEST.get()).setName("§2§lRécompense de participation").addLore("§aRécompense choisie : §2" + game.getConfig().getParticipationReward().getName()).build());
        }
        inventory.setItem(7, new ItemBuilder(Material.PAPER).setName("§6§lAnnoncer").addLore("§aAnnonce l'event sur tout le serveur (1 toutes les 1 minutes maximum)").build());
        inventory.setItem(8, new ItemBuilder(Material.LIME_DYE).setName("§2§lLancer !").addLore("§aLance le jeu !").build());
    }

}
