package net.spudacious5705.crackdown.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static net.spudacious5705.crackdown.DBOperations.CommonOperations.getOrCreateId_Player;

@Mixin(ServerPlayer.class)
public class PlayerDatabaseIDmixin implements GetDatabaseIdFunc {
    @Unique
    private int crackdown$databaseID = -1;

    @Override
    public synchronized int crackdown$getDatabaseID() {
        if(crackdown$databaseID < 0){
            crackdown$databaseID = getOrCreateId_Player((ServerPlayer)(Object)this);
        }
        return crackdown$databaseID;
    }
}
