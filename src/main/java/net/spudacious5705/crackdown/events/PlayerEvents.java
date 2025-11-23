package net.spudacious5705.crackdown.events;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.village.VillageSiegeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.db_operations.player.PlayerActivity;
import net.spudacious5705.crackdown.db_operations.player.PlayerINFO;
import net.spudacious5705.crackdown.helper.PlayerInfoFuc;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class PlayerEvents {

    static final String acceptedRulesKey = "accepted_rules";

    @SubscribeEvent
    public static void onPlayerJoin(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerActivity.log(player,"JOIN", player.blockPosition(), EventsUtil.DimensionName(player.level()),null);
            CompoundTag info = ((PlayerInfoFuc)player).crackdown$get();
            if(info != null && info.contains(acceptedRulesKey)){
                if(!info.getBoolean(acceptedRulesKey)){
                    //TODO popup rules screen... somehow
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLeave(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            PlayerActivity.log(player,"LEAVE", player.blockPosition(), EventsUtil.DimensionName(player.level()),null);
            PlayerINFO.update(player, ((PlayerInfoFuc)player).crackdown$get());
        }
    }

    @SubscribeEvent
    public static void onPlayerStartSiege(VillageSiegeEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            PlayerActivity.log(player,"STARTED_RAID", player.blockPosition(), EventsUtil.DimensionName(player.level()),null);
        }
    }

    @SubscribeEvent
    public static void onAdvancement(AdvancementEvent.AdvancementEarnEvent event) {//check suspicious advancement orders for potential robbery flagging.
        if (event.getEntity() instanceof ServerPlayer player && event.getAdvancement().sendsTelemetryEvent()) {
            String info = event.getAdvancement().getId().toString();
            PlayerActivity.log(player,"ADVANCEMENT", player.blockPosition(), EventsUtil.DimensionName(player.level()),
                    info);
        }
    }

    @SubscribeEvent
    public static void onItemPickup(EntityItemPickupEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack stack = event.getItem().getItem();
            String nbt = "";
            if (stack.hasTag()) {
                nbt = ", \"item_nbt\": \"" + stack.getTag() + "\"";
            }
            PlayerActivity.log(player,"PICKUP_ITEM", player.blockPosition(), EventsUtil.DimensionName(player.level()),
                    "{\"item\": \"" + stack.getItem() + "\", \"count\": " + stack.getCount() + nbt + "}");
        }
    }

    @SubscribeEvent
    public static void onItemToss(ItemTossEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {
            ItemStack stack = event.getEntity().getItem();
            String nbt = "";
            if (stack.hasTag()) {
                nbt = ", \"item_nbt\": \"" + stack.getTag() + "\"";
            }
            PlayerActivity.log(player,"DROP_ITEM", player.blockPosition(), EventsUtil.DimensionName(player.level()),
                    "{\"item\": \"" + EventsUtil.itemType(stack.getItem()) + "\", \"count\": " + stack.getCount() + nbt + "}");
        }
    }

    @SubscribeEvent
    public static void onItemDestroy(PlayerDestroyItemEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ItemStack stack = event.getOriginal();
            String nbt = "";
            if (stack.hasTag()) {
                nbt = ", \"item_nbt\": \"" + stack.getTag() + "\"";
            }
            PlayerActivity.log(player,"DESTROY_ITEM", player.blockPosition(), EventsUtil.DimensionName(player.level()),
                    "{\"item\": \"" + EventsUtil.itemType(stack.getItem()) + "\", \"count\": " + stack.getCount() + nbt + "}");
        }
    }
}
