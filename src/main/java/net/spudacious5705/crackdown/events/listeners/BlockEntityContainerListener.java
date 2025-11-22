package net.spudacious5705.crackdown.events.listeners;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.spudacious5705.crackdown.db_operations.BlockEntity.BlockEntityInteraction;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;
import net.spudacious5705.crackdown.logging.ItemStackChangeType;
import org.jetbrains.annotations.NotNull;

public class BlockEntityContainerListener extends CrackdownContainerListener {

    final BlockEntity blockEntity;
    final int blockEntityID;

    public BlockEntityContainerListener(@NotNull ServerPlayer player, @NotNull ItemStack[] snapshot, int trackArraySize, BlockEntity blockEntity) {
        super(player, snapshot, trackArraySize);
        this.blockEntity = blockEntity;
        this.blockEntityID = GetDatabaseIdFunc.getDatabaseID(blockEntity);

        BlockEntityInteraction.log(
                blockEntityID,
                "player",
                playerDBID,
                "CONTAINER_OPEN",
                null
        );
    }

    @Override
    void logItemSwap(int slotIndex, ItemStack now, ItemStack old) {
        BlockEntityInteraction.logItemSwap(slotIndex,blockEntityID,playerDBID,now,old);
    }

    @Override
    void logItemCountChange(int slotIndex, ItemStackChangeType itemStackChangeType, int count, ItemStack now) {
        BlockEntityInteraction.logItemCountChange(slotIndex,itemStackChangeType, count, blockEntityID, playerDBID, now);
    }

    @Override
    public void close() {
        BlockEntityInteraction.log(
                blockEntityID,
                "player",
                playerDBID,
                "CONTAINER_CLOSE",
                null
        );
    }
}
