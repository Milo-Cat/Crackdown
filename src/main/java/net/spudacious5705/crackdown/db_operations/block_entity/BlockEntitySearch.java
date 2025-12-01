package net.spudacious5705.crackdown.db_operations.block_entity;

import net.minecraft.core.BlockPos;
import net.spudacious5705.crackdown.db_operations.PositionalResult;
import net.spudacious5705.crackdown.db_operations.SQLOperation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Consumer;

public class BlockEntitySearch extends SQLOperation {

    final String selection_statement;

    final Consumer<PreparedStatement> valueInputter;

    final Consumer<BlockEntitySearchResult> complete_action;

    public BlockEntitySearch(String selectionStatement, Consumer<PreparedStatement> valueInputter, Consumer<BlockEntitySearchResult> completeAction) {
        this.selection_statement = selectionStatement;
        this.valueInputter = valueInputter;
        this.complete_action = completeAction;
    }

    @Override
    public void accept(Connection connection) {
        try {
            PreparedStatement stmt = connection.prepareStatement("""
                    SELECT d.name, b.x, b.y, b.z, i.timestamp, b.id, s.type, p.name, a.action, i.info
                    FROM block_entity_interaction AS i
                    LEFT JOIN block_entity AS b ON i.block_entity = b.id
                    LEFT JOIN dimension AS d ON b.dimension = d.id
                    LEFT JOIN source AS s ON i.source = s.id
                    LEFT JOIN players AS p ON i.player = p.id
                    LEFT JOIN block_entity_action_types AS a ON i.action = a.id
                    
                    """ + selection_statement);

            //stmt.set ....
            ResultSet rs = stmt.executeQuery();

            //while(rs.next()){
                //rs.getString();
            //}


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static class BlockEntitySearchResult extends PositionalResult {

        final int[] id;
        final String[] source;
        final String[] player_name;
        final String[] action;
        final String[] info;

        final Consumer<BlockEntitySearchResult> complete_action;

        protected BlockEntitySearchResult(
                String[] dimension,
                BlockPos[] blockPos,
                int resultCount,
                long[] timestamp,
                int[] id,
                String[] source,
                String[] playerName,
                String[] action,
                String[] info,
                Consumer<BlockEntitySearchResult> completeAction
        ) {
            super(dimension, blockPos, resultCount, timestamp);
            this.id = id;
            this.source = source;
            player_name = playerName;
            this.action = action;
            this.info = info;
            complete_action = completeAction;
        }

        @Override
        public void complete() {
            complete_action.accept(this);
        }
    }
}
