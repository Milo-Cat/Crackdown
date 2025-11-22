package net.spudacious5705.crackdown.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.spudacious5705.crackdown.DBOperations.Block.BlockInteraction;
import net.spudacious5705.crackdown.events.EventsUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {

    @Shadow
    public abstract BlockState getBlockState(BlockPos p_46732_);

    @Inject(method = "removeBlock", at = @At("TAIL"))
    void f(BlockPos pos, boolean moved, CallbackInfoReturnable<Boolean> cir){
        if(!moved){
            BlockInteraction.logPhysicsRemoved(pos, getBlockState(pos), EventsUtil.DimensionName((Level)(Object)this));
        }
    }

}
