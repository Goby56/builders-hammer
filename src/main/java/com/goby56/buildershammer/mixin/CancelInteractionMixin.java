package com.goby56.buildershammer.mixin;

import com.goby56.buildershammer.ModUtils;
import com.goby56.buildershammer.item.ModItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class CancelInteractionMixin {

    @Inject(at = @At("HEAD"), method = "shouldCancelInteraction", cancellable = true)
    private void shouldCancelInteraction(CallbackInfoReturnable<Boolean> cir) {
       if (((LivingEntity) (Object) this).getStackInHand(Hand.MAIN_HAND).isOf(ModItems.COPPER_HAMMER)) {
           if (ModUtils.lookingAtChangeableBlock(MinecraftClient.getInstance())) cir.setReturnValue(true);
       }
    }
}
