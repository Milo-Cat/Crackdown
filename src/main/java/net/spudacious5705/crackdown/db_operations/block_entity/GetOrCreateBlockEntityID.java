package net.spudacious5705.crackdown.db_operations.block_entity;

import net.minecraft.core.BlockPos;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.CommonOperations;
import net.spudacious5705.crackdown.db_operations.SQLOperation;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class GetOrCreateBlockEntityID extends SQLOperation {
    final String type;
    final String dimension;
    final BlockPos pos;
    final CompletableFuture<Integer> future;

    public GetOrCreateBlockEntityID(BlockPos pos, String dimension, String type, CompletableFuture<Integer> future) {
        this.type = type;
        this.dimension = dimension;
        this.pos = pos;
        this.future = future;
    }

    @Override
    public void accept(Connection connection) {

        try {

            int dimensionID = CommonOperations.getOrCreateId_Dimension(dimension, connection);

            // 1) Try to select existing record

            final int currentType = CommonOperations.getOrCreateId_BlockEntityType(type, connection);

            try (PreparedStatement statement = connection.prepareStatement(
                    """
                            SELECT id, type, destroyed_at, last_backup_check_at
                            FROM block_entity
                            WHERE dimension = ?
                              AND x = ?
                              AND y = ?
                              AND z = ?;
                            """)
            ) {
                statement.setInt(1, dimensionID);
                statement.setInt(2, pos.getX());
                statement.setInt(3, pos.getY());
                statement.setInt(4, pos.getZ());

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {

                        int id = rs.getInt("id");

                        int storedType = rs.getInt("type");

                        rs.getInt("destroyed_at");

                        if (!rs.wasNull()) {
                            continue;//Found block entity was destroyed
                        }

                        if (currentType != storedType) {
                            continue;//Found block entity was not of this type
                        }

                        //entity found

                        long lastBackupTime = rs.getLong("last_backup_check_at");

                        if (!rs.wasNull()) {
                            //backup immediately
                        } else if (lastBackupTime == 0) {//todo check backup time
                            //backup
                        }

                        future.complete(id);
                        return;
                    }
                }
            }

            // 3) Not found -> insert and return generated key
            String insertSql = """
                    INSERT INTO block_entity (x,y,z, dimension, type, created_at, last_backup_check_at)
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;
            try (PreparedStatement ins = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ins.setInt(1, pos.getX());
                ins.setInt(2, pos.getY());
                ins.setInt(3, pos.getZ());
                ins.setInt(4, dimensionID);
                ins.setInt(5, currentType);
                long time = DatabaseManager.timestamp();
                ins.setLong(6, time);
                ins.setLong(7, time);
                ins.executeUpdate();
                try (ResultSet keys = ins.getGeneratedKeys()) {
                    if (keys.next()) {
                        future.complete(keys.getInt(1));
                    } else {
                        future.completeExceptionally(new SQLException("No generated entity id returned from database"));
                    }
                }
            }
        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        future.completeExceptionally(new SQLException("No generated entity id returned from database"));
    }

}
