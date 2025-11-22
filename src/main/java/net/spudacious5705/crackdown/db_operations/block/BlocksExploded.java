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
    public final int count;
    public final String[] blocks;
    public final String[] states;
    final int[] x;
    final int[] y;
    final int[] z;

    public BlocksExploded(long timestamp, String dimension, int size, String[] blocks, String[] states, int[] x, int[] y, int[] z) {
        this.timestamp = timestamp;
        this.dimension = dimension;
        this.count = size;
        this.blocks = blocks;
        this.states = states;
        this.x = x;
        this.y = y;
        this.z = z;

        DatabaseManager.queueEntry(this);
    }


    @Override
    public void accept(Connection connection) {
        for (int i = 0; i < count; i++) {
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
                stmt.setInt(2, x[i]);
                stmt.setInt(3, y[i]);
                stmt.setInt(4, z[i]);
                stmt.setInt(5, CommonOperations.getOrCreateId_Dimension(dimension, connection));//dimension
                stmt.setInt(6, CommonOperations.getOrCreateId_Source("explosion", connection));//source
                stmt.setInt(7, CommonOperations.getOrCreateId_BlockAction("EXPLOSION", connection));//action
                int blockID = CommonOperations.getOrCreateId_Block(blocks[i], connection);
                stmt.setInt(8, blockID);//block ID
                if (states[i] != null) {
                    stmt.setInt(9, CommonOperations.getOrCreateId_State(states[i], blockID, connection));//state ID
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
