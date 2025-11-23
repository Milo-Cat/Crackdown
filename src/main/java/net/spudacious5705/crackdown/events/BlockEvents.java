package net.spudacious5705.crackdown.events;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.BlockSnapshot;
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
import net.spudacious5705.crackdown.db_operations.entity.EntityInteraction;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Matcher;

import static net.spudacious5705.crackdown.db_operations.block.BlockDBHelper.pattern;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class BlockEvents {

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() instanceof ServerPlayer player) {// OLD CODE

            Level level = player.level();
            String nbt = null;
            if (level.getBlockState(event.getPos()).hasBlockEntity()) {
                BlockEntity be = level.getBlockEntity(event.getPos());
                if (be != null) {
                    nbt = be.serializeNBT().toString();
                }
            }

            BlockInteraction.logPlayerInteraction(
                    event.getPos(),
                    EventsUtil.DimensionName(level),
                    ((GetDatabaseIdFunc) player).crackdown$getDatabaseID(),
                    Blocks.AIR.defaultBlockState(),
                    event.getState(),
                    "BREAK",
                    nbt);

        }

    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!event.isCanceled() && event.getEntity() != null) {
            if (event.getLevel() instanceof ServerLevel level) {
                String dimension = EventsUtil.DimensionName(level);
                BlockState oldState = event.getLevel().getBlockState(event.getPos());
                BlockState newState = event.getPlacedBlock();

                String nbt = null;
                if (level.getBlockState(event.getPos()).hasBlockEntity()) {
                    BlockEntity be = level.getBlockEntity(event.getPos());
                    if (be != null) {
                        nbt = be.serializeNBT().toString();
                    }
                }

                BlockInteraction.logPlayerInteraction(
                        event.getPos(),
                        dimension,
                        event.getEntity() instanceof ServerPlayer player ? ((GetDatabaseIdFunc) player).crackdown$getDatabaseID() : -1,
                        newState,
                        oldState,
                        "PLACE",
                        nbt);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockMultiPlace(BlockEvent.EntityMultiPlaceEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            String dimension = EventsUtil.DimensionName(level);

            for (BlockSnapshot blockSnapshot : event.getReplacedBlockSnapshots()) {
                String nbt = null;
                if (blockSnapshot.getReplacedBlock().hasBlockEntity()) {
                    BlockEntity be = blockSnapshot.getBlockEntity();
                    if (be != null) {
                        nbt = be.serializeNBT().toString();
                    }
                }

                String action;
                if (blockSnapshot.getReplacedBlock().getBlock() == Blocks.AIR) {
                    action = "PLACE";
                } else if (blockSnapshot.getCurrentBlock().getBlock() == Blocks.AIR) {
                    action = "BREAK";
                } else {
                    action = "REPLACE";
                }

                BlockInteraction.logPlayerInteraction(
                        event.getPos(),
                        dimension,
                        event.getEntity() instanceof ServerPlayer player ? ((GetDatabaseIdFunc) player).crackdown$getDatabaseID() : -1,
                        blockSnapshot.getCurrentBlock(),
                        blockSnapshot.getReplacedBlock(),
                        action,
                        nbt);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onFluidPlaceBlock(BlockEvent.FluidPlaceBlockEvent event) {//useful for lavacasts?
        if (!event.isCanceled() && event.getLevel() instanceof ServerLevel level) {
            BlockInteraction.logInteraction(event.getPos(), EventsUtil.DimensionName(level), "fluid", -1, event.getNewState(), event.getOriginalState(), "FLUID_PLACE", null);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onToolModifiesBlock(BlockEvent.BlockToolModificationEvent event) {
        if (!event.isCanceled() && event.getLevel() instanceof ServerLevel level && event.getPlayer() instanceof ServerPlayer player) {
            BlockInteraction.logPlayerInteraction(event.getPos(), EventsUtil.DimensionName(level),
                    GetDatabaseIdFunc.getDatabaseID(player),
                    event.getFinalState(),
                    event.getState(),
                    event.getToolAction().name(), null);

        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onInteractBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getUseBlock() == Event.Result.DENY || event.isCanceled()) return;

        if (event.getLevel() instanceof ServerLevel level) {
            beginCheckInteraction(level, (ServerPlayer) event.getEntity(), event.getPos());
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onAttackBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getUseBlock() == Event.Result.DENY || event.isCanceled()) return;

        if (event.getLevel() instanceof ServerLevel level) {
            beginCheckInteraction(level, (ServerPlayer) event.getEntity(), event.getPos());
        }
    }

    private static void beginCheckInteraction(ServerLevel level, ServerPlayer player, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.hasBlockEntity()) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be != null) {
                CompoundTag tagSnapshot = be.serializeNBT();
                //execute at end of tick
                level.getServer().execute(() -> checkInteractionWithBE(level, player, pos, state, tagSnapshot, be));
                return;
            }
        }
        //execute at end of tick
        level.getServer().execute(() -> checkInteraction(level, player, pos, state));
    }

    private static void checkInteraction(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state) {
        BlockState newState = level.getBlockState(pos);
        if (newState != state) {
            String action;
            if (newState.getBlock() != state.getBlock()) {
                action = "REPLACE";
            } else {
                action = "STATE_CHANGE";
            }
            BlockInteraction.logPlayerInteraction(pos, EventsUtil.DimensionName(level), GetDatabaseIdFunc.getDatabaseID(player), newState, state, action, null);
        }
        //noChange
    }

    private static void checkInteractionWithBE(ServerLevel level, ServerPlayer player, BlockPos pos, BlockState state, CompoundTag tagSnapshot, BlockEntity blockEntity) {
        checkInteraction(level, player, pos, state);
        BlockEntity be = level.getBlockEntity(pos);
        int beID = GetDatabaseIdFunc.getDatabaseID(blockEntity);
        String action;
        if (be != null) {
            CompoundTag newSnapshot = be.serializeNBT();
            if (be == blockEntity) {
                if (newSnapshot.equals(tagSnapshot)) {
                    return;//NO CHANGE :)
                }
                action = "DATA_MODIFIED";
                BlockEntityBackup.save(beID, newSnapshot, false);

                Consumer<String> logger = (info) ->
                        BlockEntityInteraction.log(beID, "player", GetDatabaseIdFunc.getDatabaseID(player), action, info);

                NBTComparisonConstructor.queue(logger, newSnapshot, tagSnapshot);
                return;

            } else {
                action = "REPLACED";
                BlockEntityBackup.save(beID, tagSnapshot, true);
            }
        } else {
            action = "REMOVED";
            BlockEntityBackup.save(beID, tagSnapshot, true);
        }
        BlockEntityInteraction.log(beID, "player", GetDatabaseIdFunc.getDatabaseID(player), action, null);

    }

    @SubscribeEvent
    public static void onNonPlayerDestroyBlock(LivingDestroyBlockEvent event) {
        Entity entity = event.getEntity();

        if (entity.level() instanceof ServerLevel level) {// OLD CODE

            String nbt = null;
            if (level.getBlockState(event.getPos()).hasBlockEntity()) {
                BlockEntity be = level.getBlockEntity(event.getPos());
                if (be != null) {
                    nbt = be.serializeNBT().toString();
                }
            }

            BlockInteraction.logInteraction(
                    event.getPos(),
                    EventsUtil.DimensionName(level),
                    EventsUtil.entityType(entity),
                    -1,
                    null,
                    event.getState(),
                    "BREAK",
                    nbt);

        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onTrample(BlockEvent.FarmlandTrampleEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            BlockState state = level.getBlockState(event.getPos());
            int playerID;
            String source;
            if (event.getEntity() instanceof ServerPlayer player) {
                playerID = GetDatabaseIdFunc.getDatabaseID(player);
                source = "player";
            } else {
                playerID = -1;
                source = EventsUtil.entityType(event.getEntity());
            }
            BlockInteraction.logInteraction(event.getPos(), EventsUtil.DimensionName(level), source, playerID, null, state, "TRAMPLE", null);
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onX(ExplosionEvent.Detonate event) {
        if (!event.isCanceled() && event.getLevel() instanceof ServerLevel level) {
            final long timestamp = DatabaseManager.timestamp();
            final String dimension = EventsUtil.DimensionName(level);

            List<BlockPos> positions = event.getAffectedBlocks();

            int size = positions.size();

            final String[] blocks = new String[size];
            final String[] states = new String[size];
            final int[] x = new int[size];
            final int[] y = new int[size];
            final int[] z = new int[size];

            int ptr = 0;

            for (BlockPos pos : positions) {
                BlockState state = level.getBlockState(pos);
                if (state.getBlock() != Blocks.AIR) {
                    Matcher matcher = pattern.matcher(state.toString());

                    if (matcher.matches()) {
                        //group 1 is usually just "Block". and is always block for blockState
                        blocks[ptr] = matcher.group(2); // inside ()
                        states[ptr] = matcher.group(3); // inside []
                        x[ptr] = pos.getX();
                        y[ptr] = pos.getY();
                        z[ptr] = pos.getZ();
                    }
                    ptr++;
                }

            }

            new BlocksExploded(timestamp, dimension, ptr, blocks, states, x, y, z);
        }
    }
}