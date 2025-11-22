package net.spudacious5705.crackdown.db_operations.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.spudacious5705.crackdown.db_operations.TimestampedPositionalEntry;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BreakBlockSQL extends TimestampedPositionalEntry {

    final int playerID;

    protected BreakBlockSQL(BlockPos pos, String dimension, @Nullable Player player) {
        super(pos, dimension);
        if(player != null){
            playerID = ((GetDatabaseIdFunc)player).crackdown$getDatabaseID();
        } else {
            playerID = -1;
        }
    }

    @Override
    public void accept(Connection connection) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    """
                    INSERT INTO block_interaction (
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
