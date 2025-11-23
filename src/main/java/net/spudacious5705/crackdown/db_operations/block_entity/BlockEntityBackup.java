package net.spudacious5705.crackdown.db_operations.block_entity;

import net.minecraft.nbt.CompoundTag;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.BackupUtil;
import net.spudacious5705.crackdown.db_operations.CommonOperations;
import net.spudacious5705.crackdown.db_operations.TimestampedEntry;
import org.jetbrains.annotations.NotNull;

import javax.sql.rowset.serial.SerialBlob;
import java.sql.*;

public class BlockEntityBackup extends TimestampedEntry {
    final int thisID;
    final CompoundTag data;
    final boolean forceBackup;

    protected BlockEntityBackup(int thisID, CompoundTag data, boolean forceBackup) {
        this.thisID = thisID;
        this.data = data;
        this.forceBackup = forceBackup;
    }


    public static void save(int ID, @NotNull CompoundTag data, boolean force) {
        DatabaseManager.queueEntry(new BlockEntityBackup(
                ID,
                data,
                force
        ));
    }


    @Override
    public void accept(Connection connection) {
        long lastBackup;
        int lastBackupID;
        boolean backupFound;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    """
                            SELECT last_backup_check_at, last_backup_id
                            FROM block_entity
                            WHERE id=?
                            """
            );
            stmt.setInt(1, thisID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lastBackup = rs.getLong("last_backup_check_at");
                lastBackupID = rs.getInt("last_backup_id");
                backupFound = !rs.wasNull();
            } else {
                throw new SQLException("[CRACKDOWN] Failed to find block entity by ID");
            }

        } catch (SQLException e) {
            throw new RuntimeException("[CRACKDOWN] Failed to find block entity by ID", e);
        }

        //serialise new data

        byte[] raw = BackupUtil.write(data);
        byte[] checksum = BackupUtil.checksum(raw);


        if(backupFound) {
            if (!forceBackup && lastBackup + 3600 > timestamp) {
                return;//too soon for hourly backup
            }

            byte[] oldChecksum;

            try {
                PreparedStatement stmt = connection.prepareStatement(
                        """
                                SELECT checksum
                                FROM block_backup_record
                                WHERE id=?
                                """
                );
                stmt.setInt(1, lastBackupID);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    oldChecksum = rs.getBytes("checksum");
                } else {
                    throw new SQLException("[CRACKDOWN] Failed to find block entity by ID");
                }

            } catch (SQLException e) {
                throw new RuntimeException("[CRACKDOWN] Failed to find block entity by ID", e);
            }

            if (BackupUtil.compareChecksums(oldChecksum, checksum)) {
                //checksum matches. Update lastCheckedTime and finish
                try {
                    PreparedStatement stmt = connection.prepareStatement(
                            """
                                    UPDATE block_entity
                                    SET last_backup_check_at = ?
                                    WHERE id = ?
                                    """
                    );
                    stmt.setLong(1, timestamp);
                    stmt.setInt(2, thisID);
                    stmt.executeUpdate();

                } catch (SQLException e) {
                    throw new RuntimeException("[CRACKDOWN] Failed to update last backup time for block entity", e);
                }
                return;
            }
        }


        //saving new backup
        Blob blob;
        try {
            blob = new SerialBlob(raw);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        int recordID;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    """
                            INSERT INTO block_backup_record(
                            entity,
                            created_at,
                            compression,
                            checksum
                            ) VALUES (?, ?, ?, ?)
                            """
            );
            stmt.setInt(1, thisID);
            stmt.setLong(2, timestamp);
            stmt.setInt(3, CommonOperations.getOrCreateId_Compression("DEFAULT", "0", connection));
            stmt.setBytes(4, checksum);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    recordID = keys.getInt(1);
                } else {
                    throw new SQLException("[CRACKDOWN] Block entity backup record key not generated/found");
                }
            }
            stmt = connection.prepareStatement(
                    """
                            INSERT INTO block_nbt_blob(
                            record,
                            data
                            ) VALUES (?, ?)
                            """
            );
            stmt.setInt(1, recordID);
            stmt.setBlob(2, blob);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("[CRACKDOWN] Failed to make backup for block entity", e);
        }
    }

}
