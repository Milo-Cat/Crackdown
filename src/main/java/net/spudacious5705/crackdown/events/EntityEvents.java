package net.spudacious5705.crackdown.events;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.AnimalTameEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.db_operations.entity.EntityBackup;
import net.spudacious5705.crackdown.db_operations.entity.EntityInteraction;
import net.spudacious5705.crackdown.events.listeners.EntityRideTracker;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

import java.util.UUID;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class EntityEvents {

    @SubscribeEvent
    public static void onTamed(AnimalTameEvent event) {

        if (event.getTamer() instanceof ServerPlayer player) {
            Animal pet = event.getAnimal();
            String dimension = EventsUtil.DimensionName(player.level());
            BlockPos pos = EventsUtil.copyBlockPos(pet.blockPosition());
            UUID petUUID = pet.getUUID();
            String petType = EventsUtil.entityType(pet);
            int playerID = ((GetDatabaseIdFunc) player).crackdown$getDatabaseID();

            EntityInteraction.log(pos, dimension, petUUID, petType, "player", playerID, "TAME", null);
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

        Entity victimEntity = event.getEntity();
        DamageSource damageSource = event.getSource();

        if (victimEntity == null) return;

        Entity attackerEntity = damageSource.getEntity();

        String dimension;
        if (victimEntity.level() instanceof ServerLevel level) {
            dimension = EventsUtil.DimensionName(level);
        } else return;

        BlockPos pos = EventsUtil.copyBlockPos(victimEntity.blockPosition());
        UUID entityUUID = victimEntity.getUUID();
        String entityType = EventsUtil.entityType(victimEntity);
        String source = damageSource.type().msgId();


        if (attackerEntity instanceof ServerPlayer player) {
            int playerID = ((GetDatabaseIdFunc) player).crackdown$getDatabaseID();
            EntityInteraction.log(pos, dimension, entityUUID, entityType, source, playerID, "KILLED", null);
        } else {
            EntityInteraction.log(pos, dimension, entityUUID, entityType, source, "KILLED",
                    attackerEntity != null ? "{\"attacker_entity\": \"" + EventsUtil.entityType(attackerEntity) + "\"}" : null
            );
        }
    }

    @SubscribeEvent
    public static void onMount(EntityMountEvent event) {
        if (event.getEntityMounting() instanceof ServerPlayer player) {
            if (event.isMounting()) {
                Entity entity = event.getEntityBeingMounted();
                if (entity != null) {
                    new EntityRideTracker<>(entity, player);//start tracker
                }
            }
        }
    }

    @SubscribeEvent
    public static void onInteractEntity(PlayerInteractEvent.EntityInteractSpecific event) {
        if(event.getEntity() instanceof ServerPlayer player) {
            int playerID = GetDatabaseIdFunc.getDatabaseID(player);
            Entity entity = event.getTarget();//entity

            if (event.getLevel() instanceof ServerLevel level) {
                CompoundTag tagSnapshot = entity.serializeNBT();
                //execute at end of tick
                level.getServer().execute(() -> checkInteraction(level, playerID, entity, tagSnapshot));
            }
        }
    }

    private static void checkInteraction(ServerLevel level, int playerID, Entity entity, CompoundTag tagSnapshot) {
        CompoundTag newSnapshot = entity.serializeNBT();
        CompoundTag diff = EventsUtil.findDifference(newSnapshot, tagSnapshot);
        String info = diff.toString();//todo OFFLOAD difference finding to 3rd thread
        EntityBackup.save(entity,newSnapshot, false);
        EntityInteraction.log(
                entity.blockPosition(),
                EventsUtil.DimensionName(level),
                entity.getUUID(),
                EventsUtil.entityType(entity),
                "player",
                playerID,
                "INTERACT",
                info
        );
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