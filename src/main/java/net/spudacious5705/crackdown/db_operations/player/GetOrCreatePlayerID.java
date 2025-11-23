package net.spudacious5705.crackdown.db_operations.player;

import net.minecraft.nbt.CompoundTag;
import net.spudacious5705.crackdown.db_operations.BackupUtil;
import net.spudacious5705.crackdown.db_operations.SQLOperation;

import java.io.InputStream;
import java.sql.*;
import java.util.concurrent.CompletableFuture;

public class GetOrCreatePlayerID extends SQLOperation {
    final String trueName;
    final String uuid;
    final CompletableFuture<Integer> futureID;
    final CompletableFuture<CompoundTag> futureINFO;

    public GetOrCreatePlayerID(String name, String uuid, CompletableFuture<Integer> futureID, CompletableFuture<CompoundTag> futureINFO) {
        this.trueName = name;
        this.uuid = uuid;
        this.futureID = futureID;
        this.futureINFO = futureINFO;
    }

    @Override
    public void accept(Connection connection) {

        try {
            // 1) Try to select existing record
            String selectSql = """
                    SELECT id, name, info
                    FROM players
                    WHERE uuid = ?
                    """;
            try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
                ps.setString(1, uuid);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        String existingName = rs.getString("name");
                        InputStream info = rs.getBlob("info").getBinaryStream();
                        futureINFO.complete(BackupUtil.read(info));

                        futureID.complete(id);

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
                        return;
                    }
                }
            }

            // 3) Not found -> insert and return generated key
            futureINFO.complete(null);
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
                        futureID.complete(keys.getInt(1));
                    } else {
                        futureID.completeExceptionally(new SQLException("[CRACKDOWN] No generated player id returned from database"));
                    }
                }
            }
        } catch (Exception e) {
            futureID.completeExceptionally(e);
        }
    }

}
