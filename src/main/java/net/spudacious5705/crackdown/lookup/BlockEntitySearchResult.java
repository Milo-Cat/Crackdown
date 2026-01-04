package net.spudacious5705.crackdown.lookup;

import net.minecraft.core.BlockPos;

import java.util.function.Consumer;

public class BlockEntitySearchResult extends PositionalResult {

        final int[] id;
        final String[] source;
        final String[] player_name;
        final String[] action;
        final String[] info;

        final Consumer<BlockEntitySearchResult> complete_action;

        protected BlockEntitySearchResult(
                String[] dimension,
                BlockPos[] blockPos,
                int resultCount,
                int[] id,
                String[] source,
                String[] playerName,
                String[] action,
                String[] info,
                Consumer<BlockEntitySearchResult> completeAction
        ) {
            super(dimension, blockPos, resultCount);
            this.id = id;
            this.source = source;
            player_name = playerName;
            this.action = action;
            this.info = info;
            complete_action = completeAction;
        }

        @Override
        public void complete() {
            complete_action.accept(this);
        }
    }