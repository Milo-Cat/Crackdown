package net.spudacious5705.crackdown.db_operations.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.CommonOperations;
import net.spudacious5705.crackdown.db_operations.TimestampedPositionalEntry;
import net.spudacious5705.crackdown.logging.ItemStackChangeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;
import java.util.UUID;

public class EntityInteraction extends TimestampedPositionalEntry {
    final UUID entityUUID;
    final String entityType;
    final String source;
    final int playerID;
    final String action;
    final String info;

    protected EntityInteraction(@NotNull BlockPos pos, @NotNull String dimension, UUID entityUUID, String entityType, @NotNull String source, int playerID, @NotNull String action, @Nullable String info) {
        super(pos, dimension);
        this.entityUUID = entityUUID;
        this.entityType = entityType;
        this.source = source;
        this.playerID = playerID;
        this.action = action;
        this.info = info;
    }

    protected EntityInteraction(long timestamp, @NotNull BlockPos pos, @NotNull String dimension, UUID entityUUID, String entityType, @NotNull String source, int playerID, @NotNull String action, @Nullable String info) {
        super(timestamp, pos, dimension);
        this.entityUUID = entityUUID;
        this.entityType = entityType;
        this.source = source;
        this.playerID = playerID;
        this.action = action;
        this.info = info;
    }

    public static void log(long timestamp, @NotNull BlockPos pos, @NotNull String dimension, UUID entityUUID, String entityType, @NotNull String source, int playerID, @NotNull String action, @Nullable String info) {
        if (Objects.equals(entityType, "minecraft:item")) return;
        DatabaseManager.queueEntry(
                new EntityInteraction(
                        timestamp,
                        pos,
                        dimension,
                        entityUUID,
                        entityType,
                        source,
                        playerID,
                        action,
                        info
                )
        );
    }

    public static void log(@NotNull BlockPos pos, @NotNull String dimension, UUID entityUUID, String entityType, @NotNull String source, int playerID, @NotNull String action, @Nullable String info) {
        if (Objects.equals(entityType, "minecraft:item")) return;
        DatabaseManager.queueEntry(
                new EntityInteraction(
                        pos,
                        dimension,
                        entityUUID,
                        entityType,
                        source,
                        playerID,
                        action,
                        info
                )
        );
    }

    public static void log(@NotNull BlockPos pos, @NotNull String dimension, UUID entityUUID, String entityType, @NotNull String source, @NotNull String action, @Nullable String info) {
        log(pos, dimension, entityUUID, entityType, source, -1, action, info);
    }

    public static void logItemCountChange(int slotIndex, String interaction, int count, UUID entityUUID, String entityType, int playerID, ItemStack stack, BlockPos blockPos, String dimension) {
        String item = stack.getItem().toString();
        String nbt = "";
        if (stack.hasTag()) {
            nbt = ", \"item_nbt\": \"" + stack.getTag() + "\"";
        }
        DatabaseManager.queueEntry(new EntityInteraction(
                blockPos,
                dimension,
                entityUUID,
                entityType,
                "player",
                playerID,
                interaction,
                "{\"slot\": " + slotIndex + ", \"item\": \"" + item + "\", \"count\": " + count + nbt + "}"
        ));
    }

    public static void logItemSwap(int slotIndex, UUID entityUUID, String entityType, int playerID, ItemStack stackNew, ItemStack stackOld, BlockPos blockPos, String dimension) {
        String itemOld = stackOld.getItem().toString();
        String nbtOld = "";
        if (stackOld.hasTag()) {
            nbtOld = ", \"item_nbt\": \"" + stackOld.getTag() + "\"";
        }
        String nbtNew = "";
        if (stackNew.hasTag()) {
            nbtNew = ", \"item_nbt\": \"" + stackNew.getTag() + "\"";
        }
        String itemNew = stackNew.getItem().toString();
        DatabaseManager.queueEntry(new EntityInteraction(
                blockPos,
                dimension,
                entityUUID,
                entityType,
                "player",
                playerID,
                ItemStackChangeType.SWAPPED.name(),
                "{\"slot\": " + slotIndex + ", \"old\":{\"item\": \"" + itemOld + "\", \"count\": " + stackOld.getCount() + nbtOld + "}, \"new\":{\"item\": \"" + itemNew + "\", \"count\": " + stackNew.getCount() + nbtNew + "}}"
        ));
    }


    @Override
    public void accept(Connection connection) {
        int entityID;
        entityID = CommonOperations.GetOrCreateEntityID(connection, entityUUID.toString(), entityType, false);
        if (entityID < 0) return;
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    """
                            INSERT INTO entity_interaction(
                            timestamp,
                            x,
                            y,
                            z,
                            dimension,
                            entity,
                            source,
                            player,
                            action,
                            info
                            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                            """
            );
            stmt.setLong(1, timestamp);
            stmt.setInt(2, blockPos.getX());
            stmt.setInt(3, blockPos.getY());
            stmt.setInt(4, blockPos.getZ());
            stmt.setInt(5, CommonOperations.getOrCreateId_Dimension(dimension, connection));//dimension
            stmt.setInt(6, entityID);//entity
            stmt.setInt(7, CommonOperations.getOrCreateId_Source(source, connection));//source
            if (playerID > 0) {
                stmt.setInt(8, playerID);
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);
            }
            stmt.setInt(9, CommonOperations.getOrCreateId_EntityAction(action, connection));//action
            if (playerID > 0) {
                stmt.setString(10, info);//info
            } else {
                stmt.setNull(10, Types.VARCHAR);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[CRACKDOWN] Failed to prepare insert statement for entity_interaction", e);
        }
        if (Objects.equals(action, "KILLED")) {
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        """
                                UPDATE entity
                                SET killed_at = ?
                                WHERE id = ?
                                """
                );
                stmt.setLong(1, timestamp);
                stmt.setInt(2, entityID);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("[CRACKDOWN] Failed to prepare update statement for entity", e);
            }
        }
    }
}
