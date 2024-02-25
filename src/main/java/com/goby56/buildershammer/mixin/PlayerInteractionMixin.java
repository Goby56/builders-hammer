package com.goby56.buildershammer.mixin;

import com.goby56.buildershammer.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.client.network.SequencedPacketCreator;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class PlayerInteractionMixin {

    @Shadow @Final private MinecraftClient client;
    @Shadow private int blockBreakingCooldown;

    @Shadow protected abstract void sendSequencedPacket(ClientWorld world, SequencedPacketCreator packetCreator);
    @Shadow public abstract boolean breakBlock(BlockPos pos);

    @Inject(at = @At("HEAD"), method = "attackBlock", cancellable = true)
    private void attackBlock(BlockPos pos, Direction direction, CallbackInfoReturnable<Boolean> cir) {
        if (this.client.player == null || this.client.world == null) return;
        if (this.client.player.getStackInHand(Hand.MAIN_HAND).isOf(ModItems.COPPER_HAMMER)) {
            BlockState blockState = this.client.world.getBlockState(pos);
            this.client.getTutorialManager().onBlockBreaking(this.client.world, pos, blockState, 1.0f);
            this.sendSequencedPacket(this.client.world, sequence -> {
                this.breakBlock(pos);
                return new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, pos, direction, sequence);
            });
            this.blockBreakingCooldown = 5;
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
