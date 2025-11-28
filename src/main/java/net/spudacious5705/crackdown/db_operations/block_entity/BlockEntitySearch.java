package net.spudacious5705.crackdown.db_operations.block_entity;

import net.minecraft.core.BlockPos;
import net.spudacious5705.crackdown.db_operations.PositionalResult;
import net.spudacious5705.crackdown.db_operations.SQLOperation;

import java.sql.Connection;
import java.util.function.Consumer;

public class BlockEntitySearch extends SQLOperation {

    final Consumer<BlockEntitySearchResult> complete_action;

    public BlockEntitySearch(Consumer<BlockEntitySearchResult> completeAction) {
        complete_action = completeAction;
    }

    @Override
    public void accept(Connection connection) {

    }

    public static class BlockEntitySearchResult extends PositionalResult {

        final int[] id;
        final String[] source;
        final String[] player_name;
        final String[] actions;
        final String[] info;

        final Consumer<BlockEntitySearchResult> complete_action;

        protected BlockEntitySearchResult(
                String[] dimension,
                BlockPos[] blockPos,
                int resultCount,
                long[] timestamp,
                int[] id,
                String[] source,
                String[] playerName,
                String[] actions,
                String[] info,
                Consumer<BlockEntitySearchResult> completeAction
        ) {
            super(dimension, blockPos, resultCount, timestamp);
            this.id = id;
            this.source = source;
            player_name = playerName;
            this.actions = actions;
            this.info = info;
            complete_action = completeAction;
        }

        @Override
        public void complete() {
            complete_action.accept(this);
        }
    }
}
