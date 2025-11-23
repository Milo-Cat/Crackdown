package net.spudacious5705.crackdown.database;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.spudacious5705.crackdown.db_operations.SQLOperation;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static net.spudacious5705.crackdown.Crackdown.MODID;


public class DatabaseManager {
    static Logger LOGGER;

    private static DatabaseWorker worker;
    private static SQLConstructionWorker constructor;

    public static void init(ServerStartingEvent event, Logger logger) {
        LOGGER = logger;
        MinecraftServer server = event.getServer();
        Path worldPath = server.getWorldPath(LevelResource.ROOT);
        Path modFolder = worldPath.resolve(MODID);

        try {
            Files.createDirectories(modFolder);
        }catch (IOException e){
            LOGGER.error("[CRACKDOWN] Failed to create mod folder at {}", modFolder.toAbsolutePath());
            event.getServer().stopServer();
            return;
        }
        File dbFile = modFolder.resolve("crackdown.db").toFile();

        Connection connection;
        try {
            String url = "jdbc:sqlite:"+dbFile.getPath();
            connection = DriverManager.getConnection(url);
            LOGGER.info("[CRACKDOWN] database connected at{}", url);

        } catch (SQLException e) {
            LOGGER.error("[CRACKDOWN] Failed to connect to database. Stopping Server.");
            e.printStackTrace();
            event.getServer().stopServer();
            return;
        }

        if (connection != null) {
            for (Tables table : Tables.values()) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate(table.SQL);
                } catch (SQLException e) {
                    LOGGER.error("[CRACKDOWN] Failed to load table {}.", table.NAME, e);
                    event.getServer().stopServer();
                }
            }
            for (Indexes index : Indexes.values()) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate(index.SQL);
                } catch (SQLException e) {
                    LOGGER.error("[CRACKDOWN] Failed to load index {}.", index.NAME, e);
                    event.getServer().stopServer();
                }
            }

            worker = new DatabaseWorker(connection);
            constructor = new SQLConstructionWorker();
        }
    }

    public static boolean isConnected() {
        return worker != null && worker.isConnected();
    }


    /**
     * Returns the current Unix timestamp.
     */
    public static long timestamp() {
        return Instant.now().getEpochSecond();
    }

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    /**
     * Converts a Unix timestamp to a formatted date/time string.
     */
    public static String HumanReadableTimestamp(long timestamp) {
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
        return dateTime.format(TIME_FORMATTER);
    }

    public static <E extends SQLOperation> void queueEntry(E entry) {
        if (isConnected()) {
            worker.queue.add(entry);
        }
    }

    public static <E extends Runnable> void queueWork(E entry) {
        if (isConnected()) {
            constructor.queue.add(entry);
        }
    }

    public static <E extends SQLOperation> void priorityQueueEntry(E entry) {
        if (isConnected()) {
            worker.priorityQueue.add(entry);
        }
    }

    public static void serverStopping() {
        //todo pretty much nothing. Event handlers should send on shutdown database writes.
        // CANCEL ANY QUERIES
    }

    public static void serverStopped() {
        //todo tell worker to shutdown when it comes to a wait (no more operations)
        // forcibly shutdown after some time if process is frozen
        shutdown();
    }

    public static void shutdown() {
        constructor.shutdown();
        try {
            if (isConnected()) {
                worker.shutdown();
                System.out.println("[CRACKDOWN] SQLite connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[CRACKDOWN] Error closing database:");
            e.printStackTrace();
        }
    }

}