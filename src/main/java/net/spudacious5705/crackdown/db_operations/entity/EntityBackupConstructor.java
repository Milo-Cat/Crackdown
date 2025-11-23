package net.spudacious5705.crackdown.db_operations.entity;


import net.minecraft.nbt.CompoundTag;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.events.EventsUtil;

import java.util.function.Consumer;

public class EntityBackupConstructor implements Runnable {
    protected final Consumer<String> logger;
    protected final CompoundTag newSnapshot;
    protected final CompoundTag oldSnapshot;

    protected EntityBackupConstructor(Consumer<String> logger, CompoundTag newSnapshot, CompoundTag oldSnapshot) {
        this.logger = logger;
        this.newSnapshot = newSnapshot;
        this.oldSnapshot = oldSnapshot;

    }

    public static void queue(Consumer<String> logger, CompoundTag newSnapshot, CompoundTag oldSnapshot) {
        DatabaseManager.queueWork(
                new EntityBackupConstructor(
                        logger,
                        newSnapshot,
                        oldSnapshot
                )
        );
    }



    @Override
    public void run() {
        CompoundTag diff = EventsUtil.findDifference(newSnapshot, oldSnapshot);

        logger.accept(diff.toString());

    }
}
