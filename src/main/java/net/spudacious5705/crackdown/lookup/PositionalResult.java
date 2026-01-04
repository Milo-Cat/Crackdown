package net.spudacious5705.crackdown.lookup;

import net.minecraft.core.BlockPos;


public abstract class PositionalResult extends SQLSearchResult {
    public final String[] dimension;
    public final BlockPos[] blockPos;

    protected PositionalResult(String[] dimension, BlockPos[] blockPos, int resultCount) {
        super(resultCount);
        this.dimension = dimension;
        this.blockPos = blockPos;
    }
}
