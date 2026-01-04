package net.spudacious5705.crackdown.lookup;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.block.BlockSimpleLookup;
import net.spudacious5705.crackdown.events.EventsUtil;
import net.spudacious5705.crackdown.helper.PlayerInfoFuc;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class InspectionMode {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        if(event.getEntity() instanceof PlayerInfoFuc player){
            if(player.crackdown$isInspector()) {
                if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                    event.setCanceled(true);

                    BlockPos pos = event.getPos();
                    Direction face = event.getFace();
                    if (face != null) {
                        pos = pos.relative(
                                face
                        );
                    }

                    String dimension = EventsUtil.DimensionName(serverPlayer.level());

                    DatabaseManager.priorityQueueEntry(
                    new BlockSimpleLookup(dimension, pos, serverPlayer)
                    );


                }
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackBlock(PlayerInteractEvent.LeftClickBlock event) {
        if(event.getEntity() instanceof PlayerInfoFuc player){
            if(player.crackdown$isInspector()) {
                if (event.getEntity() instanceof ServerPlayer serverPlayer) {
                    event.setCanceled(true);

                    BlockPos pos = event.getPos();
                    String dimension = EventsUtil.DimensionName(event.getEntity().level());

                    DatabaseManager.priorityQueueEntry(
                            new BlockSimpleLookup(dimension, pos, serverPlayer)
                    );
                }
            }
        }
    }

}