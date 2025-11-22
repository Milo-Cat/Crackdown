package net.spudacious5705.crackdown.db_operations;

import net.minecraft.core.BlockPos;

public abstract class TimestampedPositionalEntry extends TimestampedEntry {
    public final String dimension;
    public final BlockPos blockPos;

    protected TimestampedPositionalEntry(BlockPos pos, String dimension) {
        this.dimension = dimension;
        this.blockPos = pos;
    }

}
