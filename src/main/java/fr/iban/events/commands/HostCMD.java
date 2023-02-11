package fr.iban.events.commands;

import fr.iban.events.games.Game;
import fr.iban.events.GameManager;
import fr.iban.events.EventsPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HostCMD implements CommandExecutor {

    private final GameManager manager;

    //private EventsPlugin plugin;

    public HostCMD(EventsPlugin plugin) {
        //this.plugin = plugin;
        this.manager = plugin.getGameManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player player) {
            if (args.length == 0 && !manager.getRunningEvents().isEmpty()) {
                for (Game game : manager.getRunningEvents()) {
                    if (game.getHost().equals(player.getUniqueId())) {
                        game.getConfigMenu().open();
                        break;
                    }
                }
            }
        }
        return false;
    }

}
