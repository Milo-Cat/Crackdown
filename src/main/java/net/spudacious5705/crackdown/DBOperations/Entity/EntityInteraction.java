package net.spudacious5705.crackdown.DBOperations.Entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.spudacious5705.crackdown.DBOperations.CommonOperations;
import net.spudacious5705.crackdown.DBOperations.TimestampedPositionalEntry;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;
import net.spudacious5705.crackdown.logging.ItemStackChangeType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class EntityInteraction extends TimestampedPositionalEntry {
    final int entityID;
    final String source;
    final int playerID;
    final String action;
    final String info;

    public static void log(@NotNull BlockPos pos, @NotNull String dimension, int entityID, @NotNull String source, int playerID, @NotNull String action, @Nullable String info){
        DatabaseManager.queueEntry(
                new EntityInteraction(
                        pos,
                        dimension,
                        entityID,
                        source,
                        playerID,
                        action,
                        info
                )
        );
    }

    public static void log(@NotNull BlockPos pos, @NotNull String dimension, int entityID, @NotNull String source, @NotNull String action, @Nullable String info){
        log(pos,dimension,entityID,source,-1,action,info);
    }

    protected EntityInteraction(@NotNull BlockPos pos, @NotNull String dimension, int entityID, @NotNull String source, int playerID, @NotNull String action, @Nullable String info) {
        super(pos, dimension);
        this.entityID = entityID;
        this.source = source;
        this.playerID = playerID;
        this.action = action;
        this.info = info;
    }

    public static void logItemCountChange(int slotIndex, String interaction, int count, int entityID, int playerID, ItemStack stack, BlockPos blockPos, String dimension) {
        String item = stack.getItem().toString();
        String nbt = "";
        if(stack.hasTag()){
            nbt = ", \"item_nbt\": \"" + stack.getTag() + "\"";
        }
        DatabaseManager.queueEntry(new EntityInteraction(
                blockPos,
                dimension,
                entityID,
                "player",
                playerID,
                interaction,
                "{\"slot\": "+slotIndex+", \"item\": \""+item+"\", \"count\": "+count+nbt+"}"
        ));
    }

    public static void logItemSwap(int slotIndex, int entityID, int playerID, ItemStack stackNew, ItemStack stackOld, BlockPos blockPos, String dimension) {
        String itemOld = stackOld.getItem().toString();
        String nbtOld = "";
        if(stackOld.hasTag()){
            nbtOld = ", \"item_nbt\": \"" + stackOld.getTag() + "\"";
        }
        String nbtNew = "";
        if(stackNew.hasTag()){
            nbtNew = ", \"item_nbt\": \"" + stackNew.getTag() + "\"";
        }
        String itemNew = stackNew.getItem().toString();
        DatabaseManager.queueEntry(new EntityInteraction(
                blockPos,
                dimension,
                entityID,
                "player",
                playerID,
                ItemStackChangeType.SWAPPED.name(),
                "{\"slot\": "+slotIndex+", \"old\":{\"item\": \""+itemOld+"\", \"count\": "+stackOld.getCount()+nbtOld+"}, \"new\":{\"item\": \""+itemNew+"\", \"count\": "+stackNew.getCount()+nbtNew+"}}"
        ));
    }



    @Override
    public void accept(Connection connection) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    """
                    INSERT INTO entity_interaction(
                    timestamp,
                    x,
                    y,
                    z,
                    dimension,
                    entity,
                    source,
                    player,
                    action,
                    info
                    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                    """
            );
            stmt.setLong(1, timestamp);
            stmt.setInt(2, blockPos.getX());
            stmt.setInt(3, blockPos.getY());
            stmt.setInt(4, blockPos.getZ());
            stmt.setInt(5, CommonOperations.getOrCreateId_Dimension(dimension, connection));//dimension
            stmt.setInt(6, entityID);//entity
            stmt.setInt(7, CommonOperations.getOrCreateId_Source(source,connection));//source
            if (playerID > 0) {
                stmt.setInt(8, playerID);
            } else {
                stmt.setNull(8, java.sql.Types.INTEGER);
            }
            stmt.setInt(9, CommonOperations.getOrCreateId_EntityAction(action,connection));//action
            if (playerID > 0) {
                stmt.setString(10, info);//info
            } else {
                stmt.setNull(10, Types.VARCHAR);
            }
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[CRACKDOWN] Failed to prepare insert statement for entity_interaction", e);
        }
    }
}
