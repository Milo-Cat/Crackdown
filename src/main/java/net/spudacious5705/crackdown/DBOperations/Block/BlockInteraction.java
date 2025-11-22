package net.spudacious5705.crackdown.DBOperations.Block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.spudacious5705.crackdown.DBOperations.CommonOperations;
import net.spudacious5705.crackdown.DBOperations.TimestampedPositionalEntry;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.events.EventsUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BlockInteraction extends TimestampedPositionalEntry {
    final String source;
    final int playerID;
    final String action;
    final String block_new;
    final String state_new;
    final String block_old;
    final String state_old;
    final String nbt;

    protected BlockInteraction(BlockPos pos, String dimension, String source, int playerID, String action, String blockNew, String stateNew, String blockOld, String stateOld, String nbt) {
        super(pos, dimension);
        this.source = source;
        this.playerID = playerID;
        this.action = action;
        block_new = blockNew;
        state_new = stateNew;
        block_old = blockOld;
        state_old = stateOld;
        this.nbt = nbt;
    }


    public static void logPhysicsRemoved(BlockPos pos, BlockState state, String dimension){

    }

    private static String getValues(BlockState state){
        String[] array = state.toString().split("[}]");
        if(array.length==1){
            return null;
        }
        return array[1];
    }

    public static void logPlayerInteraction(BlockPos pos, String dimension, int playerID, BlockState newState, BlockState oldState, String action, String NBT){

        DatabaseManager.queueEntry(new BlockInteraction(
                pos,
                dimension,
                "player",
                playerID,
                action,
                EventsUtil.blockType(newState.getBlock()),
                getValues(newState),
                EventsUtil.blockType(oldState.getBlock()),
                getValues(oldState),
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
                EventsUtil.blockType(newState.getBlock()),
                getValues(newState),
                EventsUtil.blockType(oldState.getBlock()),
                getValues(oldState),
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
            int blockID = CommonOperations.getOrCreateId_Block(block_new,connection);
            stmt.setInt(9, blockID);//b new
            if(state_new != null){
                stmt.setInt(10, CommonOperations.getOrCreateId_State(state_new, blockID, connection));//s new
            } else {
                stmt.setNull(10, java.sql.Types.INTEGER);
            }

            blockID = CommonOperations.getOrCreateId_Block(block_old,connection);
            stmt.setInt(11, blockID);//b old
            if(state_old != null){
                stmt.setInt(12, CommonOperations.getOrCreateId_State(state_old, blockID, connection));//s old
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
