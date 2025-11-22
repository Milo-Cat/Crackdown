package net.spudacious5705.crackdown.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.spudacious5705.crackdown.db_operations.block.BlockInteraction;
import net.spudacious5705.crackdown.db_operations.block_entity.BlockEntityBackup;
import net.spudacious5705.crackdown.events.EventsUtil;
import net.spudacious5705.crackdown.helper.GetDatabaseIdFunc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Level.class)
public abstract class LevelMixin {

    @Shadow
    public abstract BlockState getBlockState(BlockPos p_46732_);

    @Shadow
    @Nullable
    public abstract MinecraftServer getServer();

    @Shadow
    @Nullable
    public abstract BlockEntity getBlockEntity(BlockPos p_46716_);

    @Inject(method = "removeBlock", at = @At("HEAD"))
    void beforePhysRemoval(BlockPos pos, boolean moved, CallbackInfoReturnable<Boolean> cir) {
        if (!moved) {
            BlockState originalState = getBlockState(pos);
            var server = getServer();
            if (server != null) {
                if(originalState.hasBlockEntity()){
                    BlockEntity be = getBlockEntity(pos);
                    if(be!=null){
                        //forced final backup
                        BlockEntityBackup.save(GetDatabaseIdFunc.getDatabaseID(be),be.serializeNBT(),true);
                    }
                }
                server.execute(() -> BlockInteraction.logPhysicsRemoved(pos, originalState, getBlockState(pos), EventsUtil.DimensionName((Level) (Object) this)));
            }
        }
    }

}
