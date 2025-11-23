package net.spudacious5705.crackdown.database;


import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static com.mojang.text2speech.Narrator.LOGGER;

public class DatabaseWorker extends Thread {

    final BlockingQueue<Consumer<Connection>> queue = new LinkedBlockingQueue<>();

    final BlockingQueue<Consumer<Connection>> priorityQueue = new LinkedBlockingQueue<>();

    private volatile boolean running = true;

    private final Connection connection;

    public DatabaseWorker(Connection connection) {
        this.connection = connection;
        this.start();
    }


    @Override
    public void run() {
        while (running) {
            try {
                Consumer<Connection> task = priorityQueue.poll();
                if (task == null) {
                    task = queue.poll(50, TimeUnit.MILLISECONDS);
                }
                if (task != null) {
                    task.accept(connection);
                }
            } catch (InterruptedException e) {
                LOGGER.warn("[CRACKDOWN] DatabaseWorker interrupted", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // catch-all so a single bad task never kills the worker
                LOGGER.error("[CRACKDOWN] Unhandled error in DatabaseWorker task", e);
            }
        }
        LOGGER.info("[CRACKDOWN] DatabaseWorker thread stopped.");
    }

    public void shutdown() throws SQLException {
        running = false;
        connection.close();
        this.interrupt();
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
