package net.spudacious5705.crackdown.DBOperations.Entity;

import net.minecraft.core.BlockPos;
import net.spudacious5705.crackdown.DBOperations.TimestampedPositionalEntry;
import net.spudacious5705.crackdown.database.DatabaseManager;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EntityInteraction extends TimestampedPositionalEntry {
    final int entityID;
    final String source;
    final int playerID;
    final String action;
    final String info;

    public static void log(@NotNull BlockPos pos, @NotNull String dimension, int entityID, @NotNull String source, int playerID, @NotNull String action, @Nullable String info){
        DatabaseManager.queueEntry(
                new EntityInteraction(
                        pos,
                        dimension,
                        entityID,
                        source,
                        playerID,
                        action,
                        info
                )
        );
    }

    public static void log(@NotNull BlockPos pos, @NotNull String dimension, int entityID, @NotNull String source, @NotNull String action, @Nullable String info){
        log(pos,dimension,entityID,source,-1,action,info);
    }

    protected EntityInteraction(@NotNull BlockPos pos, @NotNull String dimension, int entityID, @NotNull String source, int playerID, @NotNull String action, @Nullable String info) {
        super(pos, dimension);
        this.entityID = entityID;
        this.source = source;
        this.playerID = playerID;
        this.action = action;
        this.info = info;
    }

    @Override
    public void accept(Connection connection) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    """
                    INSERT INTO entity_interaction (
                    timestamp,
                    x,
                    y,
                    z,
                    dimension,
                    source,
                    player,
                    action,
                    block_new,
                    state_new,
                    block_old,
                    state_old,
                    nbt
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """
            );
            stmt.setLong(1, timestamp);
            stmt.setInt(2, blockPos.getX());
            stmt.setInt(3, blockPos.getY());
            stmt.setInt(4, blockPos.getZ());
            stmt.setInt(5, 1);//todo get dimension and send that
            stmt.setInt(6, 1);//source
            stmt.setInt(7, playerID);//player
            stmt.setInt(8, 1);//action
            stmt.setInt(9, 1);//block_new
            stmt.setInt(10, 1);//state_new
            stmt.setInt(11, 1);//block_old
            stmt.setInt(12, 1);//state_old
            stmt.setString(13, null);//nbt
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to prepare insert statement", e);
        }
    }
}
