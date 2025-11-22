package net.spudacious5705.crackdown.events.listeners;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.db_operations.entity.EntityInteraction;
import net.spudacious5705.crackdown.events.EventsUtil;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class EntityRideTracker<T extends Entity> {

    private T entity;
    private String entityType;


    public EntityRideTracker(T entity, ServerPlayer player) {
        this.entity = entity;
        this.entityType = EventsUtil.entityType(entity);
        log("MOUNT", GetDatabaseIdFunc.getDatabaseID(player));
        trackers.add(this);
    }

    static int globalTimer = 0;
    @SubscribeEvent
    public static void tickAllTrackers(TickEvent.ServerTickEvent event) {
        if (++globalTimer % 20 == 0) {
            trackers.removeIf(EntityRideTracker::tick);
        }
    }

    private static final List<EntityRideTracker<?>> trackers = new ArrayList<>();

    int timer = 0;
    private boolean tick(){
        if (++timer % 4 == 0) {
            return updateTracker();
        }
        return false;//DON'T REMOVE
    }

    private boolean updateTracker(){
        if(entity.isVehicle() && entity.getControllingPassenger() instanceof ServerPlayer player){
            log("RIDE", GetDatabaseIdFunc.getDatabaseID(player));
            return false;
        }
        log("DISMOUNTED", -1);
        entity = null;
        entityType = null;
        return true;//should remove
    }

    private void log(String action, int playerID){
        EntityInteraction.log(entity.blockPosition(), EventsUtil.DimensionName(entity.level()),entity.getUUID(), entityType, "player", playerID, action, null);
    }
}
