package net.spudacious5705.crackdown.events;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

public interface EventsUtil {

    static String DimensionName(LevelAccessor level){return ((ServerLevel)level).dimension().location().toString();}

    static BlockPos copyBlockPos(BlockPos pos){
        return new BlockPos(
                pos.getX(),
                pos.getY(),
                pos.getZ()
        );
    }

    static String entityType(Entity entity){
        ResourceLocation key = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
        return key != null ? key.toString() : "UNREGISTERED";
    }
    static String blockEntityType(BlockEntity be){
        ResourceLocation key = ForgeRegistries.BLOCK_ENTITY_TYPES.getKey(be.getType());
        return key != null ? key.toString() : "UNREGISTERED";
    }
    static String blockType(Block block){
        ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
        return key != null ? key.toString() : "UNREGISTERED";
    }
}
