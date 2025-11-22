package net.spudacious5705.crackdown.events.listeners;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.spudacious5705.crackdown.db_operations.entity.EntityInteraction;
import net.spudacious5705.crackdown.events.EventsUtil;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;
import net.spudacious5705.crackdown.logging.ItemStackChangeType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class EntityContainerListener extends CrackdownContainerListener {

    final Supplier<BlockPos> blockPos;
    final int entityID;
    final String dimension;

    public EntityContainerListener(@NotNull ServerPlayer player, @NotNull ItemStack[] snapshot, int trackArraySize, Entity entity) {
        super(player, snapshot, trackArraySize);
        this.blockPos = entity::blockPosition;
        this.entityID = ((GetDatabaseIdFunc)entity).crackdown$getDatabaseID();
        this.dimension = EventsUtil.DimensionName(entity.level());
        EntityInteraction.log(
                blockPos.get(),
                dimension,
                entityID,
                "player",
                playerDBID,
                "CONTAINER_OPEN",
                null
        );
    }

    @Override
    void logItemSwap(int slotIndex, ItemStack now, ItemStack old) {
        EntityInteraction.logItemSwap(slotIndex, entityID, playerDBID, now, old, blockPos.get(), dimension);
    }

    @Override
    void logItemCountChange(int slotIndex, ItemStackChangeType itemStackChangeType, int count, ItemStack now) {
        EntityInteraction.logItemCountChange(slotIndex, itemStackChangeType.name(), count, entityID, playerDBID, now, blockPos.get(), dimension);
    }

    @Override
    public void close() {
        EntityInteraction.log(
                blockPos.get(),
                dimension,
                entityID,
                "player",
                playerDBID,
                "CONTAINER_CLOSE",
                null
        );
    }
}
