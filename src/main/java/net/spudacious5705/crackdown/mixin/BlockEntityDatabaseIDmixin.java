package net.spudacious5705.crackdown.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.spudacious5705.crackdown.DBOperations.CommonOperations.getOrCreateId_BlockEntity;

@Mixin(BlockEntity.class)
public class BlockEntityDatabaseIDmixin implements GetDatabaseIdFunc {
    @Unique
    private int crackdown$databaseID = -1;

    @Override
    public synchronized int crackdown$getDatabaseID() {
        if(crackdown$databaseID < 0){
            crackdown$databaseID = getOrCreateId_BlockEntity((BlockEntity)(Object)this);
        }
        return crackdown$databaseID;
    }

    @Inject(method = "load", at = @At("TAIL"))
    void f(CompoundTag p_155245_, CallbackInfo ci){
        crackdown$getDatabaseID();
    }

}
