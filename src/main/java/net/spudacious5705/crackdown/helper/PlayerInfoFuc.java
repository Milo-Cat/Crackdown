package net.spudacious5705.crackdown.helper;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import javax.annotation.Nullable;

public interface PlayerInfoFuc extends GetDatabaseIdFunc {

    static int getDatabaseID(ServerPlayer subject) {
        return ((PlayerInfoFuc) subject).crackdown$getDatabaseID();
    }

    void crackdown$update(@Nullable CompoundTag info);
    @Nullable
    CompoundTag crackdown$get();
}
