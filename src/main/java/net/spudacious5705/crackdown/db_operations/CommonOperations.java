package net.spudacious5705.crackdown.db_operations;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.block_entity.GetOrCreateBlockEntityID;
import net.spudacious5705.crackdown.db_operations.player.GetOrCreatePlayerID;
import net.spudacious5705.crackdown.events.EventsUtil;

import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class CommonOperations {

    // Caches
    private static final Map<String, Integer> DIMENSION_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Integer> BLOCK_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Integer> SOURCE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Integer> BLOCK_ACTION_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Integer> BLOCK_ENTITY_TYPE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Integer> BLOCK_ENTITY_ACTION_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Integer> ENTITY_ACTION_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Integer> ENTITY_TYPE_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, Integer> ENTITY_ID_CACHE = new ConcurrentHashMap<>();

    // Dimension
    public static int getOrCreateId_Dimension(String name, Connection connection) {
        return DIMENSION_CACHE.computeIfAbsent(name,
                n -> getOrCreateResourceId("dimension", "name", n, connection));
    }

    // block
    public static int getOrCreateId_Block(String name, Connection connection) {
        return BLOCK_CACHE.computeIfAbsent(name,
                n -> getOrCreateResourceId("block", "name", n, connection));
    }

    // Source
    public static int getOrCreateId_Source(String name, Connection connection) {
        return SOURCE_CACHE.computeIfAbsent(name,
                n -> getOrCreateResourceId("source", "type", n, connection));
    }

    // block Action
    public static int getOrCreateId_BlockAction(String name, Connection connection) {
        return BLOCK_ACTION_CACHE.computeIfAbsent(name,
                n -> getOrCreateResourceId("block_action_types", "action", n, connection));
    }

    // block entity
    public static int getOrCreateId_BlockEntityType(String name, Connection connection) {
        return BLOCK_ENTITY_TYPE_CACHE.computeIfAbsent(name,
                n -> getOrCreateResourceId("block_entity_type", "name", n, connection));
    }

    // block entity Action
    public static int getOrCreateId_BlockEntityAction(String name, Connection connection) {
        return BLOCK_ENTITY_ACTION_CACHE.computeIfAbsent(name,
                n -> getOrCreateResourceId("block_entity_action_types", "action", n, connection));
    }

    // entity Action
    public static int getOrCreateId_EntityAction(String name, Connection connection) {
        return ENTITY_ACTION_CACHE.computeIfAbsent(name,
                n -> getOrCreateResourceId("entity_action_types", "action", n, connection));
    }

    // entity
    public static int getOrCreateId_EntityType(String name, Connection connection) {
        return ENTITY_TYPE_CACHE.computeIfAbsent(name,
                n -> getOrCreateResourceId("entity_type", "name", n, connection));
    }

    // Generic DB call
    private static int getOrCreateResourceId(String table, String column_name, String resource_name, Connection connection) {

        String selectSql = "SELECT id FROM " + table + " WHERE " + column_name + " = ?";
        try (PreparedStatement select = connection.prepareStatement(selectSql)) {
            select.setString(1, resource_name);
            try (ResultSet rs = select.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // ITEM NOT FOUND, GENERATING NEW ENTRY
        String insertSql = "INSERT INTO " + table + "(" + column_name + ") VALUES (?)";
        try (PreparedStatement insert = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, resource_name);
            insert.executeUpdate();
            try (ResultSet keys = insert.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return -1;
    }


    public static int getOrCreateId_State(String state, int blockID, Connection connection) {

        String selectSql = "SELECT id FROM block_state WHERE block = ? AND state = ?";
        try (PreparedStatement select = connection.prepareStatement(selectSql)) {
            select.setInt(1, blockID);
            select.setString(2, state);
            try (ResultSet rs = select.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // ITEM NOT FOUND, GENERATING NEW ENTRY
        String insertSql = "INSERT INTO block_state(block, state) VALUES (?, ?)";
        try (PreparedStatement insert = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setInt(1, blockID);
            insert.setString(2, state);
            insert.executeUpdate();
            try (ResultSet keys = insert.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return -1;
    }

    public static int getOrCreateId_Compression(String name, String version, Connection connection) {

        String selectSql = "SELECT id FROM compression_type WHERE name = ? AND version = ?";
        try (PreparedStatement select = connection.prepareStatement(selectSql)) {
            select.setString(1, name);
            select.setString(2, version);
            try (ResultSet rs = select.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // ITEM NOT FOUND, GENERATING NEW ENTRY
        String insertSql = "INSERT INTO compression_type(name, version) VALUES (?, ?)";
        try (PreparedStatement insert = connection.prepareStatement(insertSql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            insert.setString(1, name);
            insert.setString(2, version);
            insert.executeUpdate();
            try (ResultSet keys = insert.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return -1;
    }


    public static int GetOrCreateEntityID(Connection connection, String uuid, String type, boolean findDeceased) {
        return ENTITY_ID_CACHE.computeIfAbsent(type + uuid,
                n -> {

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

                                    if (!rs.wasNull() || findDeceased) {
                                        continue;//Found entity is Deceased
                                    }

                                    if (currentType != storedType) {
                                        continue;//Found entity is not of this type
                                    }

                                    //entity found

                                    return id;
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
                                    return keys.getInt(1);
                                }
                            }
                        }
                        throw new SQLException("No generated entity id returned from database");
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    Crackdown.report("[CRACKDOWN] No generated entity id returned from database");
                    return -1;
                });
    }

    // Clear caches on server lifecycle
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        clearCaches();
    }

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        clearCaches();
    }

    private static void clearCaches() {
        DIMENSION_CACHE.clear();
        BLOCK_CACHE.clear();
        SOURCE_CACHE.clear();
        BLOCK_ACTION_CACHE.clear();
        BLOCK_ENTITY_TYPE_CACHE.clear();
        BLOCK_ENTITY_ACTION_CACHE.clear();
        ENTITY_ACTION_CACHE.clear();
        ENTITY_TYPE_CACHE.clear();
        ENTITY_ID_CACHE.clear();
    }

    public static int getOrCreateId_Player(ServerPlayer serverPlayer) {

        // Capture values on the main thread
        final UUID uuid = serverPlayer.getUUID();
        final String trueName = serverPlayer.getName().getString();
        final CompletableFuture<Integer> future = new CompletableFuture<>();

        DatabaseManager.priorityQueueEntry(new GetOrCreatePlayerID(trueName, uuid.toString(), future));

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static int getOrCreateId_BlockEntity(BlockEntity blockEntity) {

        // Capture values on the main thread
        if (blockEntity.getLevel() == null) return -1;
        final String dimension = blockEntity.getLevel().dimension().location().toString();

        final BlockPos pos = blockEntity.getBlockPos();
        final String type = EventsUtil.blockEntityType(blockEntity);
        final CompletableFuture<Integer> future = new CompletableFuture<>();

        DatabaseManager.priorityQueueEntry(new GetOrCreateBlockEntityID(pos, dimension, type, future));

        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
