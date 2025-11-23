package net.spudacious5705.crackdown.db_operations.player;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.BackupUtil;
import net.spudacious5705.crackdown.db_operations.SQLOperation;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

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
            DatabaseManager.queueEntry(
                    new PlayerINFO(
                            GetDatabaseIdFunc.getDatabaseID(player),
                            info
                    )
            );
        }
    }

    @Override
    public void accept(Connection connection) {
        Blob blob;
        try {
            blob = new SerialBlob(BackupUtil.write(info));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    """
                            UPDATE players
                            SET info = ?
                            WHERE id = ?
                            """
            );
            stmt.setBlob(1, blob);
            stmt.setInt(2, playerID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[CRACKDOWN] failed to update player info", e);
        }
    }
}
