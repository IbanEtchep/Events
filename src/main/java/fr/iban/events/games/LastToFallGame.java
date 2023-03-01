package fr.iban.events.games;

import fr.iban.common.teleport.SLocation;
import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.GameState;
import fr.iban.events.interfaces.MoveBlockListener;
import fr.iban.events.options.IntOption;
import fr.iban.events.options.LocationOption;
import fr.iban.events.options.Option;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class LastToFallGame extends Game implements MoveBlockListener {

    public LastToFallGame(EventsPlugin plugin) {
        super(plugin);
    }

    public static List<Option<?>> getArenaOptions() {
        List<Option<?>> list = new ArrayList<>();
        list.add(new LocationOption("game-start-location"));
        list.add(new LocationOption("waiting-location"));
        list.add(new IntOption("death-height", 50));
        return list;
    }

    @Override
    public void handlePlayerGameJoin(Player player) {
        super.handlePlayerGameJoin(player);
        player.teleport(getStartPoint());
        player.sendTitle("§l§2Bonne chance ! ", "§aQue le meilleur gagné !", 10, 70, 20);
        player.playNote(player.getLocation(), Instrument.BASS_DRUM, Note.flat(1, Note.Tone.A));
        player.setGameMode(GameMode.ADVENTURE);
    }

    @Override
    public void finish() {
        state = GameState.FINISHED;
        if (!getPlayers().isEmpty()) {
            UUID winner = getPlayers().stream().findFirst().orElseThrow();
            Player winnerPlayer = Objects.requireNonNull(Bukkit.getPlayer(winner));
            winners.add(winner);
            broadCastMessage("§2§lLa partie est terminée, " + winnerPlayer.getName() + " a gagné !");

        }
        super.finish();
    }

    @Override
    public Location getWaitingSpawnPoint() {
        LocationOption locopt = (LocationOption) manager.getArenaOptions(getType(), getArena()).get(1);
        return locopt.getValue();
    }

    @Override
    public SLocation getWaitSLocation() {
        Location loc = getWaitingSpawnPoint();
        return new SLocation("Events", loc.getWorld().getName(), loc.getX(), loc.getY(), loc.getZ(), loc.getPitch(), loc.getYaw());
    }

    public Location getStartPoint() {
        LocationOption locopt = (LocationOption) manager.getArenaOptions(getType(), getArena()).get(0);
        return locopt.getValue();
    }

    public int getDeathHeight() {
        return ((IntOption) manager.getArenaOptions(getType(), getArena()).get(2)).getValue();
    }

    @Override
    public void onMoveBlock(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        if (e.getTo().getY() < getDeathHeight()) {
            removePlayer(player);
        }
    }

    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
        if (state == GameState.RUNNING) {
            if (isFinished()) {
                finish();
            } else {
                broadCastMessage("§7" + player.getName() + " est éliminé ! Plus que " + getPlayers().size() + " joueurs restants.");
            }
        }
    }

    @Override
    public boolean isFinished() {
        return getPlayers().size() <= 1;
    }

}
