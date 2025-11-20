package net.spudacious5705.crackdown.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.LevelAccessor;

interface EventsUtil {

    static String DimensionName(LevelAccessor level){return ((ServerLevel)level).dimension().location().toString();}

    static BlockPos copyBlockPos(BlockPos pos){
        return new BlockPos(
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );
    }
}
