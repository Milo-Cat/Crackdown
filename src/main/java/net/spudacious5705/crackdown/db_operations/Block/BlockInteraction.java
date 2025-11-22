package net.spudacious5705.crackdown.db_operations.Block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.spudacious5705.crackdown.db_operations.CommonOperations;
import net.spudacious5705.crackdown.db_operations.TimestampedPositionalEntry;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.Block.BlockDBHelper.BlockStateString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockInteraction extends TimestampedPositionalEntry {
    final String source;
    final int playerID;
    final String action;
    final BlockStateString _new;
    final BlockStateString _old;
    final String nbt;

    protected BlockInteraction(BlockPos pos, String dimension, String source, int playerID, String action, BlockState old, BlockState now, String nbt) {
        super(pos, dimension);
        this.source = source;
        this.playerID = playerID;
        this.action = action;
        this._new = BlockDBHelper.getBlockStateAsString(now);
        this._old = BlockDBHelper.getBlockStateAsString(old);
        this.nbt = nbt;
    }


    public static void logPhysicsRemoved(BlockPos pos, BlockState state, String dimension){

    }

    public static void logPlayerInteraction(BlockPos pos, String dimension, int playerID, BlockState newState, BlockState oldState, String action, String NBT){

        DatabaseManager.queueEntry(new BlockInteraction(
                pos,
                dimension,
                "player",
                playerID,
                action,
                oldState,
                newState,
                NBT
        ));
    }

    public static void logInteraction(BlockPos pos, String dimension, String source, int playerID, BlockState newState, BlockState oldState, String action, String NBT){

        DatabaseManager.queueEntry(new BlockInteraction(
                pos,
                dimension,
                source,
                playerID,
                action,
                oldState,
                newState,
                NBT
        ));
    }

    @Override
    public void accept(Connection connection) {
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
            stmt.setInt(5, CommonOperations.getOrCreateId_Dimension(dimension, connection));//dimension
            stmt.setInt(6, CommonOperations.getOrCreateId_Source(source,connection));//source
            if (playerID > 0) {
                stmt.setInt(7, playerID);
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }//player
            stmt.setInt(8, CommonOperations.getOrCreateId_EntityAction(action,connection));//action
            int blockID = CommonOperations.getOrCreateId_Block(_new.block(), connection);
            stmt.setInt(9, blockID);//b new
            if(_new.state() != null){
                stmt.setInt(10, CommonOperations.getOrCreateId_State(_new.state(), blockID, connection));//s new
            } else {
                stmt.setNull(10, java.sql.Types.INTEGER);
            }

            blockID = CommonOperations.getOrCreateId_Block(_old.block(),connection);
            stmt.setInt(11, blockID);//b old
            if(_old.state() != null){
                stmt.setInt(12, CommonOperations.getOrCreateId_State(_old.state(), blockID, connection));//s old
            } else {
                stmt.setNull(12, java.sql.Types.INTEGER);
            }

            stmt.setString(13, nbt);//nbt
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[CRACKDOWN] Failed to prepare insert statement for entity_interaction", e);
        }
    }
}
