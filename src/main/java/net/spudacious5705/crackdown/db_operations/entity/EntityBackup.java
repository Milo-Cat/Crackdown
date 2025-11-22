package net.spudacious5705.crackdown.db_operations.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.entity.Entity;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.BackupUtil;
import net.spudacious5705.crackdown.db_operations.CommonOperations;
import net.spudacious5705.crackdown.db_operations.TimestampedEntry;
import net.spudacious5705.crackdown.events.EventsUtil;
import org.jetbrains.annotations.NotNull;

import javax.sql.rowset.serial.SerialBlob;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.*;
import java.util.UUID;

public class EntityBackup extends TimestampedEntry {
    final String entityUUID;
    final String entityType;
    final CompoundTag data;
    final boolean forceBackup;

    protected EntityBackup(UUID entityUUID, String entityType, CompoundTag data, boolean forceBackup) {
        this.entityUUID = entityUUID.toString();
        this.entityType = entityType;
        this.data = data;
        this.forceBackup = forceBackup;
    }


    public static void save(Entity entity, @NotNull CompoundTag data, boolean force) {
        DatabaseManager.queueEntry(new EntityBackup(
                entity.getUUID(),
                EventsUtil.entityType(entity),
                data,
                force
        ));
    }


    @Override
    public void accept(Connection connection) {
        long lastBackup;
        int lastBackupID;
        int entityID = CommonOperations.GetOrCreateEntityID(connection,entityUUID,entityType,false);
        try {

            PreparedStatement stmt = connection.prepareStatement(
                    """
                            SELECT last_backup_check_at, last_backup_id
                            FROM entity
                            WHERE id=?
                            """
            );
            stmt.setInt(1, entityID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                lastBackup = rs.getLong("last_backup_check_at");
                lastBackupID = rs.getInt("last_backup_id");
            } else {
                throw new SQLException("[CRACKDOWN] Failed to find entity by ID");
            }

        } catch (SQLException e) {
            throw new RuntimeException("[CRACKDOWN] Failed to find entity by ID", e);
        }

        if (!forceBackup && lastBackup + 3600 > timestamp) {
            return;//too soon for hourly backup
        }

        byte[] oldChecksum;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    """
                            SELECT checksum
                            FROM entity_backup_record
                            WHERE id=?
                            """
            );
            stmt.setInt(1, lastBackupID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                oldChecksum = rs.getBytes("checksum");
            } else {
                throw new SQLException("[CRACKDOWN] Failed to find entity by ID");
            }

        } catch (SQLException e) {
            throw new RuntimeException("[CRACKDOWN] Failed to find entity by ID", e);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        try {
            NbtIo.writeCompressed(data, stream); // writes in GZIP format
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        byte[] raw = stream.toByteArray();
        byte[] checksum = BackupUtil.checksum(raw);

        if (BackupUtil.compareChecksums(oldChecksum, checksum)) {
            //checksum matches. Update lastCheckedTime
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        """
                                UPDATE entity
                                SET last_backup_check_at = ?
                                WHERE id = ?
                                """
                );
                stmt.setLong(1, timestamp);
                stmt.setInt(2, entityID);
                stmt.executeUpdate();

            } catch (SQLException e) {
                throw new RuntimeException("[CRACKDOWN] Failed to update last backup time for entity", e);
            }
            return;
        }

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
                            INSERT INTO entity_backup_record(
                            entity,
                            created_at,
                            compression,
                            checksum
                            ) VALUES (?, ?, ?, ?)
                            """
            );
            stmt.setInt(1, entityID);
            stmt.setLong(2, timestamp);
            stmt.setInt(3, CommonOperations.getOrCreateId_Compression("DEFAULT", "0", connection));
            stmt.setBytes(4, checksum);
            stmt.executeUpdate();
            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    recordID = keys.getInt(1);
                } else {
                    throw new SQLException("[CRACKDOWN] Entity backup record key not generated/found");
                }
            }
            stmt = connection.prepareStatement(
                    """
                            INSERT INTO entity_nbt_blob(
                            record,
                            data
                            ) VALUES (?, ?)
                            """
            );
            stmt.setInt(1, recordID);
            stmt.setBlob(2, blob);
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("[CRACKDOWN] Failed to make backup for entity", e);
        }
    }
}
