package net.spudacious5705.crackdown.lookup;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.helper.PlayerInfoFuc;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class InspectionMode {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        if(event.getEntity() instanceof PlayerInfoFuc player){
            if(player.crackdown$isInspector()){
                event.setCanceled(true);

                BlockPos pos = event.getPos();
                Direction face = event.getFace();
                if(face != null) {
                    pos = pos.relative(
                            face
                    );
                }


            }
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttackBlock(PlayerInteractEvent.LeftClickBlock event) {
        if(event.getEntity() instanceof PlayerInfoFuc player){
            if(player.crackdown$isInspector()){
                event.setCanceled(true);

                BlockPos pos = event.getPos();


            }
        }
    }

}