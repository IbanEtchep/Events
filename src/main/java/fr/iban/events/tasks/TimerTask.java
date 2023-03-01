package fr.iban.events.tasks;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

public class TimerTask extends BukkitRunnable {

    private int timer = 10;
    private final Consumer<Integer> consumer;

    public TimerTask(int seconds, Consumer<Integer> consumer) {
        this.timer = seconds;
        this.consumer = consumer;
    }

    @Override
    public void run() {

        consumer.accept(timer);

        if (timer == 0) {
            cancel();
        }

        timer--;
    }

    public void start(Plugin plugin) {
        this.runTaskTimer(plugin, 20L, 20L);
    }

}
