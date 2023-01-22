package fr.iban.events.tasks;

import fr.iban.events.games.Game;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class StartTask extends BukkitRunnable {

    private int timer = 10;
    private final Game game;

    public StartTask(Game game) {
        this.game = game;
    }

    @Override
    public void run() {

        if (timer == 10 || timer == 5 || timer == 5 || timer == 4 || timer == 3 || timer == 2 || timer == 1) {
            game.getViewers(game.getWaitingSpawnPoint(), 100).forEach(p -> {
                p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_FLUTE, 1f, 1f);
                p.sendMessage("§aL'event va commencer dans " + timer + " secondes !");
            });
        }

        if (timer == 0) {
            game.start();
            cancel();
        }

        timer--;
    }

}
