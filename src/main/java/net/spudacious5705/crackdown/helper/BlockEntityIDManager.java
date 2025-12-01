package net.spudacious5705.crackdown.helper;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.block_entity.BlockEntityBackup;
import net.spudacious5705.crackdown.db_operations.block_entity.GetOrCreateBlockEntityID;
import net.spudacious5705.crackdown.events.EventsUtil;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BlockEntityIDManager {
//todo clear on server shutdown
    private static final List<BlockEntityReference> entries = new ArrayList<>();

    private static int uniqueInt = 0;

    public static int getTempID(BlockEntity be){

        if (be.getLevel() == null) {
            Crackdown.reportError("BlockEntity ID was requested for a block entity without a valid LevelAccessor");
            Thread.dumpStack();
            return 0;
        }
        final String dimension = be.getLevel().dimension().location().toString();

        EventsUtil.DimensionName(be.getLevel());

        final BlockPos pos = be.getBlockPos();
        final String type = EventsUtil.blockEntityType(be);

        uniqueInt--;
        put(uniqueInt,be);

        DatabaseManager.queueEntry(new GetOrCreateBlockEntityID(pos, dimension, type, uniqueInt));


        return uniqueInt;
    }

    public static void setDatabaseID(int tempID, int dbID, boolean doBackup){
        BlockEntity be = pop(tempID);
        if(be != null) {
            ((GetDatabaseIdFunc) be).crackdown$setDatabaseID(dbID);
            if(doBackup) {
                BlockEntityBackup.save(dbID, be.serializeNBT(), true);
            }
        }
    }

    private static void put(int key, BlockEntity blockEntity) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).id == key) {
                entries.set(i, new BlockEntityReference(key, new WeakReference<>(blockEntity)));
                return;
            }
        }
        entries.add(new BlockEntityReference(key, new WeakReference<>(blockEntity)));
    }

    private static BlockEntity pop(int key) {
        for (int i = entries.size()-1; i >= 0; i--) {
            BlockEntityReference entry = entries.get(i);
            if (entry.id == key) {
                entries.remove(i);
                return entry.subject.get();
            }
        }
        return null;
    }


    record BlockEntityReference(Integer id, WeakReference<BlockEntity> subject){}

    /**
     *
     * Only to be used by the worker thread
     */
    public static int workerGetID(int thisID) {
        if(thisID>0){
            if(idMap.containsValue(thisID)){
                idMap.entrySet().removeIf(e -> e.getValue().equals(thisID));
            }
            return thisID;
        }
        return idMap.getOrDefault(thisID, 0);
    }
    static final Map<Integer,Integer> idMap = new Int2IntOpenHashMap();


}
