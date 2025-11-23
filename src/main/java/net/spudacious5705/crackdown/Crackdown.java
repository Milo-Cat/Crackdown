package net.spudacious5705.crackdown;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.client.CrackdownClient;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.helper.PlayerInfoFuc;
import org.slf4j.Logger;

import static net.spudacious5705.crackdown.events.PlayerEvents.acceptedRulesKey;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Crackdown.MODID)
public class Crackdown {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "crackdown";
    public static final long BACKUP_INTERVAL = 3600L;
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public Crackdown() {

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> CrackdownClient::init);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }

    public static void report(String r) {
        LOGGER.info("[CRACKDOWN] {}", r);
    }

    public static void reportError(String r) {
        LOGGER.error("[CRACKDOWN] {}", r);
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("CRACKDOWN says: hello!");
        DatabaseManager.init(event, LOGGER);

    }

    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        DatabaseManager.serverStopping();
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        DatabaseManager.serverStopped();
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
                Commands.literal("accept_rules")
                        .executes((ctx) -> {
                            if(ctx.getSource().getEntity() instanceof ServerPlayer player){
                                var p =((PlayerInfoFuc)player);
                                CompoundTag tag = p.crackdown$get();
                                if(tag == null){
                                    tag = new CompoundTag();
                                }
                                tag.putBoolean(acceptedRulesKey, true);
                                p.crackdown$update(tag);
                                LOGGER.info("PLayer {} accepted the rules", player.getName());
                                return 1;
                            }
                            return 0;
                        })
        );
    }
}
