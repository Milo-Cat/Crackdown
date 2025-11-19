package net.spudacious5705.crackdown.DBOperations.Entity;

import net.spudacious5705.crackdown.DBOperations.CommonOperations;
import net.spudacious5705.crackdown.DBOperations.SQLOperation;
import net.spudacious5705.crackdown.database.DatabaseManager;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class GetOrCreateEntityID extends SQLOperation {
    final String type;
    final String uuid;
    final CompletableFuture<Integer> future;

    public GetOrCreateEntityID(String type, String uuid, CompletableFuture<Integer> future) {
        this.type = type;
        this.uuid = uuid;
        this.future = future;
    }

    @Override
    public void accept(Connection connection) {

        try {
            // 1) Try to select existing record
            String selectSql = """
                    SELECT id, type, killed_at
                    FROM entity
                    WHERE uuid = ?
                    """;

            final int currentType = CommonOperations.getOrCreateId_EntityType(type, connection);

            try (PreparedStatement statement = connection.prepareStatement(selectSql)) {
                statement.setString(1, uuid);

                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {

                        int id = rs.getInt("id");

                        int storedType = rs.getInt("type");

                        rs.getInt("killed_at");

                        if(!rs.wasNull()){
                            continue;//Found entity is Deceased
                        }

                        if(currentType != storedType){
                            continue;//Found entity is not of this type
                        }

                        //entity found

                        future.complete(id);
                        return;
                    }
                }
            }

            // 3) Not found -> insert and return generated key
            String insertSql = """
                    INSERT INTO entity (uuid, type, created_at, last_backup_check_at)
                    VALUES (?, ?, ?, ?)
                    """;
            try (PreparedStatement ins = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ins.setString(1, uuid);
                ins.setInt(2, currentType);
                long time = DatabaseManager.timestamp();
                ins.setLong(3, time);
                ins.setLong(4, time);
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
