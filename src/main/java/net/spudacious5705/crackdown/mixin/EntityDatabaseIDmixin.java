package net.spudacious5705.crackdown.mixin;

import net.minecraft.world.entity.Entity;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import static net.spudacious5705.crackdown.db_operations.CommonOperations.getOrCreateId_Entity;

@Mixin(Entity.class)
public class EntityDatabaseIDmixin implements GetDatabaseIdFunc {
    @Unique
    private int crackdown$databaseID = -1;

    @Override
    public synchronized int crackdown$getDatabaseID() {
        if(crackdown$databaseID < 0){
            crackdown$databaseID = getOrCreateId_Entity((Entity)(Object)this);
        }
        return crackdown$databaseID;
    }
}
