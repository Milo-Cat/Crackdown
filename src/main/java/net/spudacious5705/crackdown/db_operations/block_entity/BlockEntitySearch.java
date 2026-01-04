package net.spudacious5705.crackdown.db_operations.block_entity;

import net.minecraft.core.BlockPos;
import net.spudacious5705.crackdown.lookup.BlockEntitySearchResult;
import net.spudacious5705.crackdown.lookup.PositionalResult;
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
                    SELECT dim.name, blc.x, blc.y, blc.z, ier.timestamp, blc.id, src.type, ply.name, act.action, ier.info
                    FROM block_entity_interaction AS ier
                    LEFT JOIN block_entity AS blc ON ier.block_entity = blc.id
                    LEFT JOIN dimension AS dim ON blc.dimension = dim.id
                    LEFT JOIN source AS src ON ier.source = src.id
                    LEFT JOIN players AS ply ON ier.player = ply.id
                    LEFT JOIN block_entity_action_types AS act ON ier.action = act.id
                    
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
}
