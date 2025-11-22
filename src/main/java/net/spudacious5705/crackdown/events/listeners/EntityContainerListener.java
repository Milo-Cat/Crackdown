package net.spudacious5705.crackdown.events.listeners;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.spudacious5705.crackdown.db_operations.entity.EntityInteraction;
import net.spudacious5705.crackdown.events.EventsUtil;
import net.spudacious5705.crackdown.logging.ItemStackChangeType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Supplier;

public class EntityContainerListener extends CrackdownContainerListener {

    final Supplier<BlockPos> blockPos;
    final UUID entityUUID;
    final String entityType;
    final String dimension;

    public EntityContainerListener(@NotNull ServerPlayer player, @NotNull ItemStack[] snapshot, int trackArraySize, Entity entity) {
        super(player, snapshot, trackArraySize);
        this.blockPos = entity::blockPosition;
        this.entityUUID = entity.getUUID();
        this.entityType = EventsUtil.entityType(entity);
        this.dimension = EventsUtil.DimensionName(entity.level());
        EntityInteraction.log(
                blockPos.get(),
                dimension,
                entityUUID,
                entityType,
                "player",
                playerDBID,
                "CONTAINER_OPEN",
                null
        );
    }

    @Override
    void logItemSwap(int slotIndex, ItemStack now, ItemStack old) {
        EntityInteraction.logItemSwap(slotIndex, entityUUID, entityType, playerDBID, now, old, blockPos.get(), dimension);
    }

    @Override
    void logItemCountChange(int slotIndex, ItemStackChangeType itemStackChangeType, int count, ItemStack now) {
        EntityInteraction.logItemCountChange(slotIndex, itemStackChangeType.name(), count, entityUUID, entityType, playerDBID, now, blockPos.get(), dimension);
    }

    @Override
    public void close() {
        EntityInteraction.log(
                blockPos.get(),
                dimension,
                entityUUID,
                entityType,
                "player",
                playerDBID,
                "CONTAINER_CLOSE",
                null
        );
    }
}
