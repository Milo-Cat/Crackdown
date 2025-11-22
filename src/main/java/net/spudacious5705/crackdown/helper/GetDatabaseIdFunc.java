package net.spudacious5705.crackdown.helper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface GetDatabaseIdFunc {
    int crackdown$getDatabaseID();

    static int getDatabaseID(Entity subject){
        return ((GetDatabaseIdFunc)subject).crackdown$getDatabaseID();
    }

    static int getDatabaseID(BlockEntity subject){
        return ((GetDatabaseIdFunc)subject).crackdown$getDatabaseID();
    }

    static int getDatabaseID(ServerPlayer subject){
        return ((GetDatabaseIdFunc)subject).crackdown$getDatabaseID();
    }
}
