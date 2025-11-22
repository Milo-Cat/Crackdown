package net.spudacious5705.crackdown.db_operations.player;

import net.minecraft.world.entity.player.Player;
import net.spudacious5705.crackdown.db_operations.TimestampedEntry;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerConnectSQL extends TimestampedEntry {
    private final int playerID;
    private final boolean joined;

    public PlayerConnectSQL(Player player, boolean joined) {
        this.playerID = ((GetDatabaseIdFunc) player).crackdown$getDatabaseID();
        this.joined = joined;
    }

    @Override
    public void accept(Connection connection) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO connections (timestamp, player, joined) VALUES (?, ?, ?)"
            );
            stmt.setLong(1, timestamp);
            stmt.setInt(2, playerID);
            stmt.setBoolean(3, joined);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to prepare insert statement", e);
        }
    }
}
