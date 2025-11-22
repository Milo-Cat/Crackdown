package net.spudacious5705.crackdown.db_operations.player;

import net.spudacious5705.crackdown.db_operations.SQLOperation;

import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class GetOrCreatePlayerID extends SQLOperation {
    final String trueName;
    final String uuid;
    final CompletableFuture<Integer> future;

    public GetOrCreatePlayerID(String name, String uuid, CompletableFuture<Integer> future) {
        this.trueName = name;
        this.uuid = uuid;
        this.future = future;
    }

    @Override
    public void accept(Connection connection) {

        try {
            // 1) Try to select existing record
            String selectSql = """
                    SELECT id, name
                    FROM players
                    WHERE uuid = ?
                    """;
            try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
                ps.setString(1, uuid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        String existingName = rs.getString("name");

                        // 2) Update name if changed (do this before completing the future)
                        if (!trueName.equals(existingName)) {
                            String updateSql = """
                                    UPDATE players
                                    SET name = ?
                                    WHERE id = ?
                                    """;
                            try (PreparedStatement ups = connection.prepareStatement(updateSql)) {
                                ups.setString(1, trueName);
                                ups.setInt(2, id);
                                ups.executeUpdate();
                            }
                        }

                        future.complete(id);
                        return;
                    }
                }
            }

            // 3) Not found -> insert and return generated key
            String insertSql = """
                    INSERT INTO players (uuid, name)
                    VALUES (?, ?)
                    """;
            try (PreparedStatement ins = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ins.setString(1, uuid);
                ins.setString(2, trueName);
                ins.executeUpdate();
                try (ResultSet keys = ins.getGeneratedKeys()) {
                    if (keys.next()) {
                        future.complete(keys.getInt(1));
                    } else {
                        future.completeExceptionally(new SQLException("No generated player id returned from database"));
                    }
                }
            }
        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        future.completeExceptionally(new SQLException("No generated player id returned from database"));
    }

}
