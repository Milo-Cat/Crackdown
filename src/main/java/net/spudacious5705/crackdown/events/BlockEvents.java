package net.spudacious5705.crackdown.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventDispatcher;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.DBOperations.Block.BlockInteraction;
import net.spudacious5705.crackdown.DBOperations.CommonOperations;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class BlockEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if(event.getPlayer() instanceof ServerPlayer player) {// OLD CODE

            BlockInteraction.logPlayerInteraction(
                    event.getPos(),
                    EventsUtil.DimensionName(player.level()),
                    ((GetDatabaseIdFunc)player).crackdown$getDatabaseID(),
                    Blocks.AIR.defaultBlockState(),
                    event.getState(),
                    "BREAK",
                    null);

        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if(!event.isCanceled()&&event.getEntity()!=null){
            if(event.getLevel() instanceof ServerLevel level){
                String dimension = EventsUtil.DimensionName(level);
                BlockState oldState = event.getLevel().getBlockState(event.getPos());
                BlockState newState = event.getPlacedBlock();
                BlockInteraction.logPlayerInteraction(
                        event.getPos(),
                        dimension,
                        event.getEntity() instanceof ServerPlayer player ? ((GetDatabaseIdFunc)player).crackdown$getDatabaseID() : -1,
                        newState,
                        oldState,
                        "PLACE",
                        null);
            }
        }
    }

    @SubscribeEvent
    public static void onBlockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
    }

    @SubscribeEvent
    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event) {//useful for lavacasts?
    }

    @SubscribeEvent
    public static void onToolModifiesBlock(BlockEvent.BlockToolModificationEvent event) {
        event.getContext().getClickedPos();
        event.getToolAction().name();
        event.getPlayer();
        event.getLevel();
        event.getFinalState();
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