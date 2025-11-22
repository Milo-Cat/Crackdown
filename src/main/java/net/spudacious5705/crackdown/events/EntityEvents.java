package net.spudacious5705.crackdown.events;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.spudacious5705.crackdown.Crackdown;
import net.spudacious5705.crackdown.DBOperations.Entity.EntityInteraction;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class EntityEvents {

    @SubscribeEvent
    public static void onTamed(AnimalTameEvent event) {

        if(event.getTamer() instanceof ServerPlayer player){
            Animal pet = event.getAnimal();
            String dimension = EventsUtil.DimensionName(player.level());
            BlockPos pos = EventsUtil.copyBlockPos(pet.blockPosition());
            int petID = ((GetDatabaseIdFunc)pet).crackdown$getDatabaseID();
            int playerID = ((GetDatabaseIdFunc)player).crackdown$getDatabaseID();

            EntityInteraction.log(pos,dimension,petID,"player",playerID,"TAME",null);
        }
    }

    @SubscribeEvent
    public static void onAttacked(LivingAttackEvent event) {
        damagingEvent("ATTACKED",
                event.getEntity(),
                event.getSource(),
                event.getAmount()
        );
    }

    @SubscribeEvent
    public static void onKilled(LivingDeathEvent event) {
        damagingEvent("KILLED",
                event.getEntity(),
                event.getSource(),
                0
        );
    }

    static void damagingEvent(String action, Entity victimEntity, DamageSource damageSource, float damageQuantity){
        if(victimEntity == null)return;

        Entity attackerEntity = damageSource.getEntity();

        String dimension;
        if (victimEntity.level() instanceof ServerLevel level) {
            dimension = EventsUtil.DimensionName(level);
        } else return;

        BlockPos pos = EventsUtil.copyBlockPos(victimEntity.blockPosition());
        int entityID = ((GetDatabaseIdFunc)victimEntity).crackdown$getDatabaseID();
        String source = damageSource.type().msgId();
        String damageInfo = "{\"damage_amount\": " + damageQuantity;


        if(attackerEntity instanceof ServerPlayer player){
            int playerID = ((GetDatabaseIdFunc)player).crackdown$getDatabaseID();
            EntityInteraction.log(pos,dimension,entityID,source,playerID,action,damageInfo+"}");
        } else {
            if(attackerEntity != null) {
                String type = EventsUtil.entityType(attackerEntity);
                damageInfo = damageInfo + ", \"attacker_entity\": \""+
                        type
                        +"\"}";
            } else {
                damageInfo = damageInfo + "}";
            }
            EntityInteraction.log(pos,dimension,entityID,source,action,damageInfo);
        }
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
        if(event.getTarget() instanceof ContainerEntity c){
            event.getTarget();

        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (!event.getLevel().isClientSide) {
            ((GetDatabaseIdFunc)entity).crackdown$getDatabaseID();
        }
    }

}