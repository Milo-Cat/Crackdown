package net.spudacious5705.crackdown.events;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.db_operations.block.BlockInteraction;
import net.spudacious5705.crackdown.db_operations.block.BlocksExploded;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

import static net.spudacious5705.crackdown.db_operations.block.BlockDBHelper.*;

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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event) {//useful for lavacasts?
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onToolModifiesBlock(BlockEvent.BlockToolModificationEvent event) {
        event.getContext().getClickedPos();
        event.getToolAction().name();
        event.getPlayer();
        event.getLevel();
        event.getFinalState();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)//todo for BOTH of these, need to check if the block has a BE and if it changed.
    public static void onInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        event.getEntity();//player
        event.getUseBlock();//result
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onAttackBlock(PlayerInteractEvent.LeftClickBlock event) {
        event.getEntity();//player
        event.getUseBlock();//result
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTrample(BlockEvent.FarmlandTrampleEvent event) {
        event.getEntity();//player
        event.getPos();//pos
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onX(ExplosionEvent.Detonate event) {
        if(!event.isCanceled() && event.getLevel() instanceof ServerLevel level) {
            final long timestamp = DatabaseManager.timestamp();
            final String dimension = EventsUtil.DimensionName(level);

            AffectedBlock[] affectedBlocks =
                    event.getAffectedBlocks().stream().map((pos) ->
                CreateAffectedBlock(
                        pos, level.getBlockState(pos)
                )
            ).toArray(AffectedBlock[]::new);

            new BlocksExploded(timestamp,dimension,affectedBlocks);
        }
    }
}