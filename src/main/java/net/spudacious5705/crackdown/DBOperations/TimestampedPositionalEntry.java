package net.spudacious5705.crackdown.DBOperations;

import net.minecraft.core.BlockPos;
import net.spudacious5705.crackdown.database.DatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class TimestampedPositionalEntry extends TimestampedEntry {
    public final String dimension;
    public final BlockPos blockPos;

    protected TimestampedPositionalEntry(BlockPos pos, String dimension) {
        this.dimension = dimension;
        this.blockPos = pos;
    }

}
