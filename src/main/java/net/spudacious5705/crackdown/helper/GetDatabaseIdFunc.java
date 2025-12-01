package net.spudacious5705.crackdown.helper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface GetDatabaseIdFunc {

    static int getDatabaseID(BlockEntity subject) {
        return ((GetDatabaseIdFunc) subject).crackdown$getDatabaseID();
    }

    static int getDatabaseID(ServerPlayer subject) {
        return ((PlayerInfoFuc) subject).crackdown$getDatabaseID();
    }

    int crackdown$getDatabaseID();

    void crackdown$setDatabaseID(int id);
}
