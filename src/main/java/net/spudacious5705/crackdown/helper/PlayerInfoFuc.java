package net.spudacious5705.crackdown.helper;

import net.minecraft.nbt.CompoundTag;

import javax.annotation.Nullable;

public interface PlayerInfoFuc {

    void crackdown$update(@Nullable CompoundTag info);
    @Nullable
    CompoundTag crackdown$get();
}
