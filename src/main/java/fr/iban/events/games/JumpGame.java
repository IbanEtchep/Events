package fr.iban.events.games;

import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.GameType;

public class JumpGame extends ParkourGame {

    public JumpGame(EventsPlugin plugin) {
        super(plugin);
    }

    @Override
    public GameType getType() {
        return GameType.JUMP;
    }
}
