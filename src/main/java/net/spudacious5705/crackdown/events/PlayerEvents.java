package net.spudacious5705.crackdown.events;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.village.VillageSiegeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.db_operations.player.PlayerConnectSQL;
import net.spudacious5705.crackdown.database.DatabaseManager;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class PlayerEvents {
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            DatabaseManager.queueEntry(new PlayerConnectSQL(player, true));
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            DatabaseManager.queueEntry(new PlayerConnectSQL(player, false));
        }
    }

    @SubscribeEvent
    public static void onPlayerStartSiege(VillageSiegeEvent event) {
        event.getPlayer();
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {//check suspicious advancement orders for potential robbery flagging.
        event.getEntity();
        event.getAdvancement();
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {

    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {

    }

    @SubscribeEvent
    public static void onItemDestroy(PlayerDestroyItemEvent event) {
        event.getOriginal();
        InteractionHand hand = event.getHand();
        if(hand!=null) {
            event.getEntity().getItemInHand(event.getHand()).getItem();
        }
    }
}
