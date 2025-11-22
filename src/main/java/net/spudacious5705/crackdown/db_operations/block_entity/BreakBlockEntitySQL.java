package net.spudacious5705.crackdown.db_operations.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.spudacious5705.crackdown.db_operations.block.BreakBlockSQL;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class BreakBlockEntitySQL extends BreakBlockSQL {

    protected BreakBlockEntitySQL(BlockPos pos, String dimension, @Nullable Player player) {
        super(pos, dimension, player);
    }

    @Override
    public void accept(Connection connection) {
        super.accept(connection);
        try {
            //todo FIND BLOCK ENTITY ID, UPDATE REMOVAL TIME
            //todo GIVE BLOCK ENTITIES, DB IDs like PLAYERS
            PreparedStatement stmt = connection.prepareStatement(
                    "INSERT INTO block_entity_interaction () VALUES (?, ?, ?, ?)"//todo CORRECT THIS
            );
            stmt.setLong(1, timestamp);
            //todo add data
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to prepare insert statement", e);
        }
    }
}
