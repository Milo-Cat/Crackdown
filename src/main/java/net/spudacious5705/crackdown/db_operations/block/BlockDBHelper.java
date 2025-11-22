package net.spudacious5705.crackdown.db_operations.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface BlockDBHelper {
    Pattern pattern = Pattern.compile("([^\\{]+)\\{([^\\}]+)\\}(?:\\[([^]]+)])?");

    static BlockStateString getBlockStateAsString(BlockState state) {

        Matcher matcher = pattern.matcher(state.toString());

        if (matcher.matches()) {
            //group 1 is usually just "Block". and is always block for blockState
            String blockString = matcher.group(2); // inside ()
            String stateString = matcher.group(3); // inside []

            return new BlockStateString(
                    blockString,
                    stateString
            );
        }

        throw new IllegalStateException("[CRACKDOWN] BlockState.toString() is malformed or has unexpected structure: " + state);

    }

    record BlockStateString(String block, String state) {
    }
}
