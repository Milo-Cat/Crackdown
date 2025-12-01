package net.spudacious5705.crackdown.db_operations.player;

import com.mojang.datafixers.util.Pair;

import net.spudacious5705.crackdown.db_operations.SQLOperation;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static net.spudacious5705.crackdown.Crackdown.reportError;

public class GetListOfSavedPlayers extends SQLOperation {
    final CompletableFuture<List<Pair<String, Integer>>> future;

    public GetListOfSavedPlayers(CompletableFuture<List<Pair<String, Integer>>> future) {
        this.future = future;
    }

    @Override
    public void accept(Connection connection) {

        List<Pair<String, Integer>> returnList = new ArrayList<>();
        // 1) Try to select existing record
        String selectSql = """
                SELECT name, id
                FROM players
                """;
        try (PreparedStatement ps = connection.prepareStatement(selectSql)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    try {
                        String name = rs.getString("name");
                        int id = rs.getInt("id");
                        returnList.add(new Pair<>(name,id));

                    } catch (SQLException e) {
                        reportError("Error reading player ID");
                    }

                }
            } catch (SQLException e) {
                reportError("Error retrieving all player IDs");
            }
        } catch (SQLException e) {
            reportError("Failed to prepare statement for collecting all player IDs");
        }

        future.complete(returnList);
    }
}
