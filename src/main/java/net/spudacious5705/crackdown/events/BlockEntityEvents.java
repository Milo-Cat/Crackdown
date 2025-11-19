package net.spudacious5705.crackdown.events;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.events.listeners.CrackdownContainerListener;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class BlockEntityEvents {

    private static final Map<AbstractContainerMenu, CrackdownContainerListener> LISTENERS = new WeakHashMap<>();


    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if(event.getEntity() instanceof ServerPlayer player) {

            AbstractContainerMenu menu = event.getContainer();

            int size = menu.slots.size();

            if(size == 0) return;

            Boolean[] isTracked = new Boolean[size];
            ItemStack[] snapshot = new ItemStack[size];

            boolean shouldTrack = false;

            for (int i = 0; i < size; i++){
                Slot slot = menu.slots.get(i);
                if(slot.container instanceof BlockEntity blockEntity){
                    //todo: check if this block entity is in the database and consider making a backup.
                    int id = ((GetDatabaseIdFunc)blockEntity).crackdown$getDatabaseID();

                    isTracked[i] = true;
                    shouldTrack = true;
                } else {
                    isTracked[i] = false;
                }
                snapshot[i] = slot.getItem().copy();
            }

            if(shouldTrack) {

                //todo: log action: container opened

                CrackdownContainerListener listener = new CrackdownContainerListener(menu, player, snapshot, isTracked);

                event.getContainer().addSlotListener(listener);

                LISTENERS.put(menu, listener);

            }
        }
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            AbstractContainerMenu menu = event.getContainer();
            CrackdownContainerListener listener = LISTENERS.remove(menu);
            if (listener != null) {
                listener.close();
                menu.removeSlotListener(listener);
            }

            //todo: log action: container closed
        }
    }

}