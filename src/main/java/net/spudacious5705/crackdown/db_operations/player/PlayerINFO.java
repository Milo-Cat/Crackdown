package net.spudacious5705.crackdown.db_operations.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.BackupUtil;
import net.spudacious5705.crackdown.db_operations.SQLOperation;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;
import net.spudacious5705.crackdown.helper.PlayerInfoFuc;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerINFO extends SQLOperation {
    private final int playerID;
    private final CompoundTag info;

    protected PlayerINFO(int playerID, CompoundTag info) {
        this.playerID = playerID;
        this.info = info;
    }

    public static void update(ServerPlayer player, CompoundTag info) {
        if(info != null) {
            DatabaseManager.priorityQueueEntry(
                    new PlayerINFO(
                            PlayerInfoFuc.getDatabaseID(player),
                            info
                    )
            );
        }
    }

    @Override
    public void accept(Connection connection) {
        byte[] raw = BackupUtil.write(info);

        try {
            PreparedStatement stmt = connection.prepareStatement(
                    """
                            UPDATE players
                            SET info = ?
                            WHERE id = ?
                            """
            );
            stmt.setBytes(1, raw);
            stmt.setInt(2, playerID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[CRACKDOWN] failed to update player info", e);
        }
    }
}
