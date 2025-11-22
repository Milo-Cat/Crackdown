package net.spudacious5705.crackdown.db_operations.block;

import net.spudacious5705.crackdown.db_operations.CommonOperations;
import net.spudacious5705.crackdown.db_operations.SQLOperation;
import net.spudacious5705.crackdown.database.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlocksExploded extends SQLOperation {

    public final long timestamp;
    public final String dimension;

    final BlockDBHelper.AffectedBlock[] affectedBlocks;

    public BlocksExploded(long timestamp, String dimension, BlockDBHelper.AffectedBlock[] affectedBlocks) {
        this.timestamp = timestamp;
        this.dimension = dimension;
        this.affectedBlocks = affectedBlocks;
        DatabaseManager.queueEntry(this);
    }

    @Override
    public void accept(Connection connection) {
        for (BlockDBHelper.AffectedBlock block: affectedBlocks) {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        """
                                INSERT INTO block_interaction(
                                timestamp,
                                x,
                                y,
                                z,
                                dimension,
                                source,
                                action,
                                block_old,
                                state_old
                                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                                """
                );
                stmt.setLong(1, timestamp);
                stmt.setInt(2, block.x());
                stmt.setInt(3, block.y());
                stmt.setInt(4, block.z());
                stmt.setInt(5, CommonOperations.getOrCreateId_Dimension(dimension, connection));//dimension
                stmt.setInt(6, CommonOperations.getOrCreateId_Source("explosion", connection));//source
                stmt.setInt(7, CommonOperations.getOrCreateId_BlockAction("EXPLOSION", connection));//action
                int blockID = CommonOperations.getOrCreateId_Block(block.block(), connection);
                stmt.setInt(8, blockID);//block ID
                if (block.state() != null) {
                    stmt.setInt(9, CommonOperations.getOrCreateId_State(block.state(), blockID, connection));//state ID
                } else {
                    stmt.setNull(9, java.sql.Types.INTEGER);
                }
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("[CRACKDOWN] Failed to prepare insert statement for entity_interaction", e);
            }
        }
    }
}
