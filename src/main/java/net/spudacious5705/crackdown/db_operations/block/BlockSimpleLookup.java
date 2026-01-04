package net.spudacious5705.crackdown.db_operations.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.CommonOperations;
import net.spudacious5705.crackdown.db_operations.SQLOperation;
import net.spudacious5705.crackdown.lookup.BlockEntitySearchResult;
import net.spudacious5705.crackdown.lookup.BlockInspectResult;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static net.spudacious5705.crackdown.database.DatabaseManager.HumanReadableTimestamp;

public class BlockSimpleLookup extends SQLOperation {

    private final WeakReference<ServerPlayer> playerRef;

    public final BlockPos blockPos;
    public final String dimension;


    public BlockSimpleLookup(String dimension, BlockPos blockPos, ServerPlayer player) {
        this.blockPos = blockPos;
        this.dimension = dimension;
        this.playerRef = new WeakReference<>(player);
    }

    @Override
    public void accept(Connection connection) {
        try {
            PreparedStatement stmt = connection.prepareStatement("""
                    SELECT record.timestamp, source.type, player.name, action.action, blockold.name, stateold.state, blocknew.name, statenew.state
                    FROM block_interaction AS record
                    
                    LEFT JOIN block AS blockold ON record.block_old = blockold.id
                    LEFT JOIN block AS blocknew ON record.block_new = blocknew.id
                    
                    LEFT JOIN block_state AS stateold ON record.state_old = stateold.id
                    LEFT JOIN block_state AS statenew ON record.state_new = statenew.id
                    
                    LEFT JOIN source AS source ON record.source = source.id
                    LEFT JOIN players AS player ON record.player = player.id
                    LEFT JOIN block_action_types AS action ON record.action = action.id
                    
                    WHERE record.dimension = ?
                      AND record.x = ?
                      AND record.y = ?
                      AND record.z = ?
                    
                    ORDER BY record.timestamp DESC
                    
                    """);

            stmt.setInt(1, CommonOperations.getOrCreateId_Dimension(dimension, connection));
            stmt.setInt(2, blockPos.getX());
            stmt.setInt(3, blockPos.getY());
            stmt.setInt(4, blockPos.getZ());

            ResultSet rs = stmt.executeQuery();

            List<String> responses = new ArrayList<>();

            int resultCount = 0;

            while (rs.next()) {

                resultCount++;

                long timestamp = rs.getLong(1);
                String sourceType = rs.getString(2);
                String playerName = rs.getString(3);
                String actionName = rs.getString(4);
                String oldBlock = rs.getString(5);
                String oldState = rs.getString(6);
                String newBlock = rs.getString(7);
                String newState = rs.getString(8);

                String response =
                        String.join("  ",

                        "§e",
                        HumanReadableTimestamp(timestamp),
                        sourceType,
                        playerName,
                        "\n§f",
                        actionName,
                        oldBlock,
                        "§6→§f",
                        newBlock,
                        "\n§0--------------------"
                );

                responses.add(response);

            }

            DatabaseManager.searchQueue.add(

                new BlockInspectResult(resultCount, responses, playerRef)

            );

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
