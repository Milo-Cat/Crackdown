package net.spudacious5705.crackdown.database;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.mojang.text2speech.Narrator.LOGGER;

class SQLConstructionWorker extends Thread {

    final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    private volatile boolean running = true;

    @Override
    public void run() {
        while (running) {
            try {
                Runnable task = queue.poll(50, TimeUnit.MILLISECONDS);
                if (task != null) {
                    task.run();
                }
            } catch (InterruptedException e) {
                LOGGER.warn("[CRACKDOWN] Construction Worker interrupted", e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                // catch-all so a single bad task never kills the worker
                LOGGER.error("[CRACKDOWN] Unhandled error in Construction Worker task", e);
            }
        }
        LOGGER.info("[CRACKDOWN] Construction worker thread stopped.");
    }

    void shutdown(){
        this.running = false;
        this.interrupt();
    }
}
