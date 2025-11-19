package net.spudacious5705.crackdown.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.DBOperations.CommonOperations;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class BlockEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if(event.getPlayer() instanceof ServerPlayer player) {// OLD CODE
            BlockPos pos = event.getPos();
            String levelName = EventsUtil.DimensionName(event.getLevel());

            int playerId = ((GetDatabaseIdFunc) player).crackdown$getDatabaseID();

            /*DatabaseManager.queueEntry(
                    new BreakBlockSQL(
                            new BlockPos(pos),
                            playerId
                    )
            );*/

        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {

    }

    @SubscribeEvent
    public static void onBlockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
    }

    @SubscribeEvent
    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event) {//useful for lavacasts?
    }

    @SubscribeEvent
    public static void onToolModifiesBlock(BlockEvent.BlockToolModificationEvent event) {
    }

    @SubscribeEvent//todo for BOTH of these, need to check if the block has a BE and if it changed.
    public static void onInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        event.getEntity();//player
        event.getUseBlock();//result
    }

    @SubscribeEvent
    public static void onAttackBlock(PlayerInteractEvent.LeftClickBlock event) {
        event.getEntity();//player
        event.getUseBlock();//result
    }


    @SubscribeEvent
    public static void onTrample(BlockEvent.FarmlandTrampleEvent event) {
        event.getEntity();//player
        event.getPos();//pos
    }

    /*@SubscribeEvent
    public static void onX(ExplosionEvent.Start event) {

    }

    @SubscribeEvent
    public static void onX(ExplosionEvent.Detonate event) {

    }*/
}