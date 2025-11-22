package net.spudacious5705.crackdown.events;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.db_operations.entity.EntityInteraction;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class EntityEvents {

    @SubscribeEvent
    public static void onTamed(AnimalTameEvent event) {

        if(event.getTamer() instanceof ServerPlayer player){
            Animal pet = event.getAnimal();
            String dimension = EventsUtil.DimensionName(player.level());
            BlockPos pos = EventsUtil.copyBlockPos(pet.blockPosition());
            UUID petUUID = pet.getUUID();
            String petType = EventsUtil.entityType(pet);
            int playerID = ((GetDatabaseIdFunc)player).crackdown$getDatabaseID();

            EntityInteraction.log(pos,dimension,petUUID, petType, "player",playerID,"TAME",null);
        }
    }

    /*@SubscribeEvent
    public static void onAttacked(LivingAttackEvent event) {
        damagingEvent("ATTACKED",
                event.getEntity(),
                event.getSource(),
                event.getAmount()
        );
    }*/

    @SubscribeEvent
    public static void onKilled(LivingDeathEvent event) {
        damagingEvent("KILLED",
                event.getEntity(),
                event.getSource(),
                0
        );
    }

    static void damagingEvent(String action, Entity victimEntity, DamageSource damageSource, float damageQuantity){
        if(victimEntity == null || victimEntity instanceof ItemEntity)return;

        Entity attackerEntity = damageSource.getEntity();

        String dimension;
        if (victimEntity.level() instanceof ServerLevel level) {
            dimension = EventsUtil.DimensionName(level);
        } else return;

        BlockPos pos = EventsUtil.copyBlockPos(victimEntity.blockPosition());
        UUID entityUUID = victimEntity.getUUID();
        String entityType = EventsUtil.entityType(victimEntity);
        String source = damageSource.type().msgId();
        String damageInfo = "{\"damage_amount\": " + damageQuantity;


        if(attackerEntity instanceof ServerPlayer player){
            int playerID = ((GetDatabaseIdFunc)player).crackdown$getDatabaseID();
            EntityInteraction.log(pos,dimension,entityUUID, entityType, source, playerID, action,damageInfo+"}");
        } else {
            if(attackerEntity != null) {
                damageInfo = damageInfo + ", \"attacker_entity\": \""+entityType+"\"}";
            } else {
                damageInfo = damageInfo + "}";
            }
            EntityInteraction.log(pos,dimension,entityUUID,entityType,source,action,damageInfo);
        }
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
        if(event.getTarget() instanceof ContainerEntity c){
            event.getTarget();

        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!event.getLevel().isClientSide) {
            EntityInteraction.log(
                    entity.blockPosition(),
                    EventsUtil.DimensionName(entity.level()),
                    entity.getUUID(),
                    EventsUtil.entityType(entity),
                    "world",
                    "spawn",
                    null
            );
        }
    }

}