package fr.iban.events.managers;

import fr.iban.common.data.sql.DbAccess;
import fr.iban.events.EventsPlugin;
import fr.iban.events.enums.GameType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class DatabaseManager {
    private final DataSource ds = DbAccess.getDataSource();
    private EventsPlugin plugin;

    public DatabaseManager(EventsPlugin plugin) {
        this.plugin = plugin;
        init();
    }

    private void init() {
        String[] createStatements = new String[]{
                "CREATE TABLE IF NOT EXISTS event_winners(" +
                        "   id INT PRIMARY KEY AUTO_INCREMENT," +
                        "   player_uuid UUID," +
                        "   reward VARCHAR(255)," +
                        "   game_type VARCHAR(50)," +
                        "   created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                        "   INDEX (player_uuid)," +
                        "   INDEX (game_type)" +
                        ");"
        };

        try (Connection connection = ds.getConnection()) {
            for (String createStatement : createStatements) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(createStatement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addVictory(UUID winner, GameType gameType, String reward) {
        String sql = "INSERT INTO event_winners (player_uuid, reward, game_type) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = ds.getConnection().prepareStatement(sql)) {
            preparedStatement.setString(1, winner.toString());
            preparedStatement.setString(2, reward);
            preparedStatement.setString(3, gameType.toString());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}