package net.spudacious5705.crackdown.lookup;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.lang.ref.WeakReference;
import java.util.List;

public class BlockInspectResult extends SQLSearchResult{
    private final List<String> results;
    private final WeakReference<ServerPlayer> playerRef;

    public BlockInspectResult(int resultCount, List<String> results, WeakReference<ServerPlayer> playerRef) {
        super(resultCount);
        this.results = results;
        this.playerRef = playerRef;
    }

    @Override
    public void complete() {//process the response on game thread
        ServerPlayer player = playerRef.get();
        if(player == null) return;

        int loopCount = Math.min(resultCount, 8);
        for(int i = 0; i<loopCount; i++) {
            player.displayClientMessage(Component.literal(results.get(i)), false);
        }
    }
}
