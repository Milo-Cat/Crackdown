package net.spudacious5705.crackdown.db_operations.block_entity;

import net.minecraft.world.item.ItemStack;
import net.spudacious5705.crackdown.db_operations.CommonOperations;
import net.spudacious5705.crackdown.db_operations.TimestampedEntry;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.logging.ItemStackChangeType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Objects;

public class BlockEntityInteraction extends TimestampedEntry {
    final int thisID;
    final String source;
    final int playerID;
    final String action;
    final String info;

    protected BlockEntityInteraction(int thisID, String source, int playerID, String action, String info) {
        this.thisID = thisID;
        this.source = source;
        this.playerID = playerID;
        this.action = action;
        this.info = info;
    }



    public static void log(int thisID, String source, int playerID, String interaction, String info) {
        DatabaseManager.queueEntry(new BlockEntityInteraction(
                thisID,
                source,
                playerID,
                interaction,
                info
        ));
    }

    public static void logItemCountChange(int slotIndex, ItemStackChangeType interaction, int count, int blockEntityID, int playerID, ItemStack stack){
        String item = stack.getItem().toString();
        String nbt = "";
        if(stack.hasTag()){
            nbt = ", \"item_nbt\": \"" + stack.getTag() + "\"";
        }
        DatabaseManager.queueEntry(new BlockEntityInteraction(
                blockEntityID,
                "player",
                playerID,
                interaction.name(),
                "{\"slot\": "+slotIndex+", \"item\": \""+item+"\", \"count\": "+count+nbt+"}"
        ));
    }

    public static void logItemSwap(int slotIndex, int blockEntityID, int playerID, ItemStack stackNew, ItemStack stackOld){
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
        DatabaseManager.queueEntry(new BlockEntityInteraction(
                blockEntityID,
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
                    INSERT INTO block_entity_interaction(
                    timestamp,
                    block_entity,
                    source,
                    player,
                    action,
                    info
                    ) VALUES (?, ?, ?, ?, ?, ?)
                    """
            );
            stmt.setLong(1, timestamp);
            stmt.setInt(2, thisID);
            stmt.setInt(3, CommonOperations.getOrCreateId_Source(source,connection));
            stmt.setInt(4, playerID);
            stmt.setInt(5, CommonOperations.getOrCreateId_BlockEntityAction(action,connection));
            if (info != null) {
                stmt.setString(6, info);
            } else {
                stmt.setNull(6, Types.VARCHAR);
            }

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[CRACKDOWN] Failed to prepare insert statement for entity_interaction", e);
        }
        if(Objects.equals(action, "REMOVED")||Objects.equals(action, "REPLACED")){
            try {
                PreparedStatement stmt = connection.prepareStatement(
                        """
                        UPDATE block_entity
                        SET destroyed_at = ?
                        WHERE id = ?
                        """
                );
                stmt.setLong(1, timestamp);
                stmt.setInt(2, thisID);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("[CRACKDOWN] Failed to prepare update statement for block_entity", e);
            }
        }
    }
}
