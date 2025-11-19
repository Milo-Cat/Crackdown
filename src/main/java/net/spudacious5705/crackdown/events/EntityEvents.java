package net.spudacious5705.crackdown.events;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class EntityEvents {

    @SubscribeEvent
    public static void onTamed(AnimalTameEvent event) {
        event.getEntity();//pet
        event.getAnimal();
        event.getTamer();//player
    }

    //LivingAttackEvent - unused

    @SubscribeEvent
    public static void onDamaged(LivingDamageEvent event) {
        event.getEntity();
        event.getSource().getEntity();
    }

    @SubscribeEvent
    public static void onKilled(LivingDeathEvent event) {
        event.getEntity();
        event.getSource().getEntity();
    }

    @SubscribeEvent
    public static void onDestroyBlock(LivingDestroyBlockEvent event) {
        event.getEntity();
    }

    @SubscribeEvent
    public static void onMount(EntityMountEvent event) {
        event.getEntity();
    }

    @SubscribeEvent
    public static void onInteractEntity(PlayerInteractEvent.EntityInteractSpecific event) {
        event.getEntity();//player
        event.getTarget();//entity
        event.getLocalPos();
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!event.getLevel().isClientSide) {
            ((GetDatabaseIdFunc)entity).crackdown$getDatabaseID();
        }
    }

}