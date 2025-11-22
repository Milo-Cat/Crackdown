package net.spudacious5705.crackdown.events;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.events.listeners.BlockEntityContainerListener;
import net.spudacious5705.crackdown.events.listeners.CrackdownContainerListener;
import net.spudacious5705.crackdown.events.listeners.EntityContainerListener;

import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class BlockEntityEvents {

    private static final Map<AbstractContainerMenu, CrackdownContainerListener> LISTENERS = new WeakHashMap<>();


    @SubscribeEvent
    public static void onContainerOpen(PlayerContainerEvent.Open event) {
        if (event.getEntity() instanceof ServerPlayer player) {

            AbstractContainerMenu menu = event.getContainer();

            int size = menu.slots.size();

            if (size == 0) return;

            ItemStack[] snapshot = new ItemStack[size];

            Map<Container, CrackdownContainerListener> listeners = new HashMap<>();

            for (int i = 0; i < size; i++) {
                Slot slot = menu.slots.get(i);
                Container container = slot.container;
                if (listeners.containsKey(container)) {
                    listeners.get(container).track(slot.getContainerSlot());
                } else if (container instanceof BlockEntity blockEntity) {

                    listeners.put(container,
                            new BlockEntityContainerListener(
                                    player,
                                    snapshot,
                                    size,
                                    blockEntity
                            ));
                } else if (container instanceof Entity entity) {

                    listeners.put(container,
                            new EntityContainerListener(
                                    player,
                                    snapshot,
                                    size,
                                    entity
                            ));
                }
                snapshot[i] = slot.getItem().copy();
            }

            for (CrackdownContainerListener listener : listeners.values()) {

                event.getContainer().addSlotListener(listener);

                LISTENERS.put(menu, listener);
            }

        }
    }

    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        if (event.getEntity() instanceof ServerPlayer) {
            AbstractContainerMenu menu = event.getContainer();

            do {
                CrackdownContainerListener listener = LISTENERS.remove(menu);
                if (listener != null) {
                    listener.close();
                    menu.removeSlotListener(listener);
                } else {
                    return;
                }
            } while (true);
        }
    }

}