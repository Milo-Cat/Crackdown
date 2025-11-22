package net.spudacious5705.crackdown.events;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;

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

    static CompoundTag findDifference(CompoundTag newSnapshot, CompoundTag oldSnapshot) {
        CompoundTag removed = new CompoundTag();
        CompoundTag added = new CompoundTag();
        CompoundTag diff = new CompoundTag();

        // Collect keys
        Set<String> oldKeys = new HashSet<>(oldSnapshot.getAllKeys());
        Set<String> newKeys = new HashSet<>(newSnapshot.getAllKeys());

        // Keys removed
        Set<String> removedKeys = new HashSet<>(oldKeys);
        removedKeys.removeAll(newKeys);
        for (String key : removedKeys) {
            Tag tag = oldSnapshot.get(key);
            if(tag!=null) {
                removed.put(key, tag);
            }
        }

        // Keys added
        Set<String> addedKeys = new HashSet<>(newKeys);
        addedKeys.removeAll(oldKeys);
        for (String key : addedKeys) {
            Tag tag = newSnapshot.get(key);
            if(tag!=null) {
                added.put(key, tag);
            }
        }

        // Keys present in both — check for changed values
        Set<String> commonKeys = new HashSet<>(oldKeys);
        commonKeys.retainAll(newKeys);
        for (String key : commonKeys) {
            Tag oldVal = oldSnapshot.get(key);
            Tag newVal = newSnapshot.get(key);
            if (oldVal != null && newVal != null && !newVal.equals(oldVal)) {
                if(oldVal instanceof CompoundTag oldComp && newVal instanceof CompoundTag newComp){
                    CompoundTag subTag = findDifference(newComp,oldComp);//recursive search
                    Tag splitSub = subTag.get("removed");
                    if (splitSub != null) {
                        removed.put(key, splitSub);
                    }
                    splitSub = subTag.get("added");
                    if (splitSub != null) {
                        added.put(key, splitSub);
                    }
                } else {
                    removed.put(key, oldVal);
                    added.put(key, newVal);
                }
            }
        }
        diff.put("removed",removed);
        diff.put("added",added);

        return diff;
    }
}
