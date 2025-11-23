package net.spudacious5705.crackdown.db_operations.player;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.CommonOperations;
import net.spudacious5705.crackdown.db_operations.TimestampedPositionalEntry;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class PlayerActivity extends TimestampedPositionalEntry {
    private final int playerID;
    private final String action;
    private final String info;

    protected PlayerActivity(int playerID, String action, BlockPos pos, String dimension, String info) {
        super(pos,dimension);
        this.playerID = playerID;
        this.action = action;
        this.info = info;
    }

    public static void log(ServerPlayer player, String action, BlockPos pos, String dimension, String info){
        DatabaseManager.queueEntry(
                new PlayerActivity(
                        GetDatabaseIdFunc.getDatabaseID(player),
                        action,
                        pos,
                        dimension,
                        info
                )
        );
    }

    @Override
    public void accept(Connection connection) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO player_events (timestamp, player, action, x, y, z, dimension, info) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
            );
            stmt.setLong(1, timestamp);
            stmt.setInt(2, playerID);
            stmt.setInt(3, CommonOperations.getOrCreateId_PlayerAction(action,connection));
            stmt.setInt(4, blockPos.getX());
            stmt.setInt(5, blockPos.getY());
            stmt.setInt(6, blockPos.getZ());
            stmt.setInt(7, CommonOperations.getOrCreateId_Dimension(dimension, connection));
            if (info != null) {
                stmt.setString(8, info);//s new
            } else {
                stmt.setNull(8, Types.VARCHAR);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to prepare insert statement for player activity", e);
        }
    }
}
