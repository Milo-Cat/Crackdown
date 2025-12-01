package net.spudacious5705.crackdown.mixin;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.spudacious5705.crackdown.helper.BlockEntityIDManager;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;


@Mixin(BlockEntity.class)
public class BlockEntityDatabaseIDmixin implements GetDatabaseIdFunc {
    @Unique
    private int crackdown$databaseID = 0;

    @Override
    public synchronized int crackdown$getDatabaseID() {
        if (crackdown$databaseID == 0) {
            crackdown$databaseID = BlockEntityIDManager.getTempID((BlockEntity)(Object) this);
        }
        return crackdown$databaseID;
    }

    @Override
    public synchronized void crackdown$setDatabaseID(int id) {
        crackdown$databaseID = id;
    }

}
