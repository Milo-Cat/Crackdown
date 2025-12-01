package net.spudacious5705.crackdown.events;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.database.DatabaseManager;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class WorldEvents {

    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        DatabaseManager.serverStopping();
    }

    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        DatabaseManager.serverStopped();
    }

    @SubscribeEvent
    public static void tick(TickEvent event){
        if(event.side.isServer()){
            DatabaseManager.onTick();
        }
    }

}
