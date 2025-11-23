package net.spudacious5705.crackdown.database;


import net.spudacious5705.crackdown.Crackdown;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class DatabaseWorker extends Thread {

    final BlockingQueue<Consumer<Connection>> queue = new LinkedBlockingQueue<>();

    final BlockingQueue<Consumer<Connection>> priorityQueue = new LinkedBlockingQueue<>();

    private volatile boolean running = true;
    private volatile boolean shutdownRequested = false;

    private CompletableFuture<Boolean> hasShutdown = null;
    public void requestShutdown(CompletableFuture<Boolean> hasShutdown) {
        shutdownRequested = true;
        this.hasShutdown = hasShutdown;
    }

    public boolean isRunning() {
        return running;
    }

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
                    task = queue.poll();
                }
                if (task != null) {
                    task.accept(connection);
                } else if(shutdownRequested){
                    safeShutdown();
                    return;
                } else {
                    sleep(50);
                }
            } catch (InterruptedException e) {
                Crackdown.report("DatabaseWorker interrupted");
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // catch-all so a single bad task never kills the worker
                Crackdown.reportError("Unhandled error in DatabaseWorker task");
            }
        }
        Crackdown.report("DatabaseWorker thread stopped abruptly.");
        try {
            connection.close();
        } catch (SQLException ignored) {}
    }

    private void safeShutdown(){
        running = false;
        try {
            connection.commit();
            connection.close();
        } catch (SQLException ignored) {}
        Crackdown.report("DatabaseWorker thread safely stopped.");
        if(hasShutdown != null){
            hasShutdown.complete(true);
        }
    }

    public void forceShutdown() throws SQLException {
        this.interrupt();
        running = false;
        try {
            connection.commit();
        } catch (SQLException ignored) {}
        connection.close();
    }

    public boolean isConnected() {
        try {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
