package net.spudacious5705.crackdown.db_operations;

import net.spudacious5705.crackdown.database.DatabaseManager;


public abstract class TimestampedEntry extends SQLOperation {
    public final long timestamp;

    protected TimestampedEntry() {
        this.timestamp = DatabaseManager.timestamp();
    }
    protected TimestampedEntry(long timestamp) {
        this.timestamp = timestamp;
    }
}
