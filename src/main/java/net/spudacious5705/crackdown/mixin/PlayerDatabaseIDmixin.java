package net.spudacious5705.crackdown.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;
import net.spudacious5705.crackdown.helper.PlayerInfoFuc;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static net.spudacious5705.crackdown.db_operations.CommonOperations.getOrCreateId_Player;

@Mixin(ServerPlayer.class)
public class PlayerDatabaseIDmixin implements PlayerInfoFuc {
    @Unique
    private int crackdown$databaseID = -1;

    @Unique
    private CompoundTag crackdown$info = null;

    @Override
    public synchronized int crackdown$getDatabaseID() {
        if (crackdown$databaseID < 0) {
            crackdown$databaseID = getOrCreateId_Player((ServerPlayer) (Object) this, this);
        }
        return crackdown$databaseID;
    }

    @Override
    public void crackdown$update(@Nullable CompoundTag info) {
        crackdown$info = info;
    }

    @Nullable
    @Override
    public CompoundTag crackdown$get() {
        return crackdown$info;
    }
}
