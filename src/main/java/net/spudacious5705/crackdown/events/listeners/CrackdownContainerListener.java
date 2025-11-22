package net.spudacious5705.crackdown.events.listeners;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.item.ItemStack;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;
import net.spudacious5705.crackdown.logging.ItemStackChangeType;
import org.jetbrains.annotations.NotNull;

public abstract class CrackdownContainerListener implements ContainerListener {
    final int playerDBID;
    final boolean[] isTracked;
    final ItemStack[] snapshot;

    public CrackdownContainerListener(@NotNull ServerPlayer player, @NotNull ItemStack[] snapshot, int trackArraySize) {
        this.playerDBID = ((GetDatabaseIdFunc) player).crackdown$getDatabaseID();
        this.isTracked = new boolean[trackArraySize];
        this.snapshot = snapshot;
    }

    public void track(int i) {
        isTracked[i] = true;
    }

    @Override
    public void slotChanged(@NotNull AbstractContainerMenu menu, int slotIndex, @NotNull ItemStack stack) {//todo: change implementation to handle multiple viewers.
        if (isTracked[slotIndex]) {

            ItemStack old = snapshot[slotIndex];
            ItemStack now = menu.slots.get(slotIndex).getItem().copy();

            if (now == ItemStack.EMPTY) {
                if (old != ItemStack.EMPTY) {
                    //ALL Items REMOVED!
                    logItemCountChange(slotIndex, ItemStackChangeType.REMOVED, old.getCount(), old);
                }

                //has item so compare
            } else if (ItemStack.isSameItemSameTags(old, now)) {
                int diff = old.getCount() - now.getCount();
                if (diff < 0) {
                    //Items REMOVED!
                    logItemCountChange(slotIndex, ItemStackChangeType.REMOVED, diff, old);
                } else if (diff > 0) {
                    //Items ADDED!
                    logItemCountChange(slotIndex, ItemStackChangeType.ADDED, diff, now);
                }
            } else {//Items CHANGED!
                if (old == ItemStack.EMPTY) {
                    logItemCountChange(slotIndex, ItemStackChangeType.ADDED, now.getCount(), now);
                } else {
                    logItemSwap(slotIndex, now, old);
                }

            }
            snapshot[slotIndex] = now;
        }
    }

    abstract void logItemSwap(int slotIndex, ItemStack now, ItemStack old);

    abstract void logItemCountChange(int slotIndex, ItemStackChangeType itemStackChangeType, int count, ItemStack now);

    @Override
    public void dataChanged(@NotNull AbstractContainerMenu menu, int dataIndex, int value) {

    }

    public abstract void close();
}
