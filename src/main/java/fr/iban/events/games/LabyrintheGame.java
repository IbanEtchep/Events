package fr.iban.events.games;

import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.GameType;

public class LabyrintheGame extends ParkourGame {

    public LabyrintheGame(EventsPlugin plugin) {
        super(plugin);
    }

    @Override
    public GameType getType() {
        return GameType.LABYRINTHE;
    }
}
