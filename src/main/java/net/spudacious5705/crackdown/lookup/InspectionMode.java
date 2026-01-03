package net.spudacious5705.crackdown.lookup;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.NBTComparisonConstructor;
import net.spudacious5705.crackdown.db_operations.block.BlockInteraction;
import net.spudacious5705.crackdown.db_operations.block.BlocksExploded;
import net.spudacious5705.crackdown.db_operations.block_entity.BlockEntityBackup;
import net.spudacious5705.crackdown.db_operations.block_entity.BlockEntityInteraction;
import net.spudacious5705.crackdown.events.EventsUtil;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;
import net.spudacious5705.crackdown.helper.PlayerInfoFuc;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;

import static net.spudacious5705.crackdown.db_operations.block.BlockDBHelper.pattern;

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