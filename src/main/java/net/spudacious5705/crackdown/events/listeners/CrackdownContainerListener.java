package net.spudacious5705.crackdown.events.listeners;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;
import net.spudacious5705.crackdown.logging.ItemStackChangeType;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class CrackdownContainerListener implements ContainerListener {
    final int playerDBID;
    final WeakReference<AbstractContainerMenu> storedMenu;
    final Boolean[] isTracked;
    final ItemStack[] snapshot;

    public CrackdownContainerListener(@NotNull AbstractContainerMenu menu, @NotNull ServerPlayer player, @NotNull ItemStack[] snapshot, @NotNull Boolean[] trackArray) {
        this.playerDBID = ((GetDatabaseIdFunc)player).crackdown$getDatabaseID();
        this.storedMenu = new WeakReference<>(menu);
        this.isTracked = trackArray;
        this.snapshot = snapshot;
    }

    @Override
    public void slotChanged(@NotNull AbstractContainerMenu m, int slotIndex, @NotNull ItemStack stack) {//todo: change implementation to handle multiple viewers.
        if(isTracked[slotIndex]){

            AbstractContainerMenu menu = storedMenu.get();
            if(menu == null){
                //todo: remove instance or something.
                return;
            }

            if(m != menu)return;//not MY menu >:(

            Slot slot = menu.slots.get(slotIndex);
            if(slot.container instanceof BlockEntity blockEntity) {

                ItemStack old = snapshot[slotIndex];
                ItemStack now = m.slots.get(slotIndex).getItem().copy();

                if(now == ItemStack.EMPTY){
                    if(old == ItemStack.EMPTY){
                        //no change? Don't log
                    } else {
                        //ALL Items REMOVED!
                        logCountChange(slotIndex,ItemStackChangeType.REMOVED, old.getCount(), blockEntity, playerDBID);
                    }

                    //has item so compare
                } else if(ItemStack.isSameItemSameTags(old,now)){
                    int diff = old.getCount()-now.getCount();
                    if(diff<0){
                        //Items REMOVED!
                        logCountChange(slotIndex,ItemStackChangeType.REMOVED, diff, blockEntity, playerDBID);
                    } else if (diff>0) {
                        //Items ADDED!
                        logCountChange(slotIndex,ItemStackChangeType.ADDED, diff, blockEntity, playerDBID);
                    } else {
                        //no change? Don't log
                    }
                } else {//Items CHANGED!
                    if(old == ItemStack.EMPTY){
                        logSwap(slotIndex, old, now, blockEntity, playerDBID);
                    }

                }
                snapshot[slotIndex] = now;
            }
        }
    }

    @Override
    public void dataChanged(@NotNull AbstractContainerMenu menu, int dataIndex, int value) {

    }

    public void close() {

    }

    static void logCountChange(int slotIndex, ItemStackChangeType interaction, int count, BlockEntity blockEntity, int playerID){

    }

    static void logSwap(int slotIndex, ItemStack old, ItemStack now, BlockEntity blockEntity, int playerID){

    }
}
