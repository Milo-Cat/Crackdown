package net.spudacious5705.crackdown;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.helper.PlayerInfoFuc;

import static net.spudacious5705.crackdown.events.PlayerEvents.acceptedRulesKey;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class Commands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                net.minecraft.commands.Commands.literal("accept_rules")
                        .executes((ctx) -> {
                            if(ctx.getSource().getEntity() instanceof ServerPlayer player){
                                var p =((PlayerInfoFuc)player);
                                CompoundTag tag = p.crackdown$get();
                                if(tag == null){
                                    tag = new CompoundTag();
                                }
                                tag.putBoolean(acceptedRulesKey, true);
                                p.crackdown$update(tag);
                                Crackdown.report("Player "+player.getName()+" accepted the rules");
                                return 1;
                            }
                            return 0;
                        })
        );
    }
}
