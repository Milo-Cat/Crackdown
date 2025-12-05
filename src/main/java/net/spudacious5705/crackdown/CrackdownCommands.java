package net.spudacious5705.crackdown;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.datafixers.util.Pair;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.spudacious5705.crackdown.database.DatabaseManager;
import net.spudacious5705.crackdown.db_operations.player.GetListOfSavedPlayers;
import net.spudacious5705.crackdown.helper.PlayerInfoFuc;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static net.spudacious5705.crackdown.events.PlayerEvents.acceptedRulesKey;

@Mod.EventBusSubscriber(modid = Crackdown.MODID)
public class CrackdownCommands {
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
                                Crackdown.report("Player "+player.getName()+" accepted the rules");
                                return 1;
                            }
                            return 0;
                        })
        );


        event.getDispatcher().register(
                Commands.literal("crackdown")
                        .then(Commands.literal("undo_for_player")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.argument("player", StringArgumentType.string())
                                .suggests(OPTIONS)
                        .executes((ctx) -> {
                            int playerID = SAVED_PLAYERS_CACHE.get(
                            ctx.getArgument("player", String.class)
                            );
                            DatabaseManager.timestamp();
                            return 0;
                        })))
        );

        event.getDispatcher().register(
                Commands.literal("crackdown")
                        .requires(source -> source.hasPermission(4))
                        .then(Commands.literal("load_player_names")
                        .executes((ctx) -> {
                            saved_player_options();
                            return 1;
                        }))
        );


        event.getDispatcher().register(
                Commands.literal("crackdown")
                        .then(Commands.literal("raidMode")
                                .requires(source -> source.hasPermission(4))
                                .then(Commands.literal("ON")
                                        .executes((ctx) -> {
                                            Crackdown.raidMode = true;
                                            return 0;
                                        }))
                                .then(Commands.literal("OFF")
                                        .executes((ctx) -> {
                                            Crackdown.raidMode = false;
                                            return 0;
                                        })))
        );
    }

    private static double player_db_last_checked = -1;
    private static final Map<String, Integer> SAVED_PLAYERS_CACHE = new ConcurrentHashMap<>();

    private static final SuggestionProvider<CommandSourceStack> OPTIONS =
            (context, builder) -> {
        SAVED_PLAYERS_CACHE.forEach((name,id) ->
        {
            builder.suggest(name);
        });
        return builder.buildFuture();
    };;

    private static void saved_player_options(){
        if(player_db_last_checked+4000 < DatabaseManager.timestamp() && DatabaseManager.isConnected()){
            player_db_last_checked = DatabaseManager.timestamp();

            CompletableFuture<List<Pair<String, Integer>>> future = new CompletableFuture<>();

            DatabaseManager.priorityQueueEntry(new GetListOfSavedPlayers(future));

            List<Pair<String, Integer>> pairs;
            try {
                pairs = future.get();
            } catch (InterruptedException | ExecutionException e) {
                Crackdown.reportError("Failed to retrieve saved players future");
                return;
            }

            SAVED_PLAYERS_CACHE.clear();

            pairs.forEach((pair) ->{
                SAVED_PLAYERS_CACHE.put(pair.getFirst(),pair.getSecond());
            });

        }
    }



    @SubscribeEvent
    public static void serverShutdown(ServerStoppingEvent event){
        player_db_last_checked = -1;
        SAVED_PLAYERS_CACHE.clear();
    }
}
