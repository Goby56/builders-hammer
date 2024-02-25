package com.goby56.buildershammer.item;

import com.goby56.buildershammer.ChangeableProperties;
import com.goby56.buildershammer.PropertyController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class CopperHammerItem extends ToolItem {

    public CopperHammerItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity playerEntity = context.getPlayer();
        World world = context.getWorld();
        if (!world.isClient && playerEntity != null && !this.modifyPreset(!playerEntity.isSneaking(), playerEntity, world.getBlockState(context.getBlockPos()), context.getStack())) {
            return ActionResult.FAIL;
        }
        return ActionResult.success(world.isClient);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return false;
    }

    @Override
    public boolean allowContinuingBlockBreaking(PlayerEntity player, ItemStack oldStack, ItemStack newStack) {
        return true;
    }

    public static boolean changeState(PlayerEntity player, BlockState state, World world, BlockPos pos) {
        ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
        NbtCompound preset = getPreset(state, stack);
        if (preset != null) {
            BlockState newState = NbtHelper.toBlockState(world.createCommandRegistryWrapper(RegistryKeys.BLOCK), preset);
            return applyState(player, newState, pos, world, stack,
                    Text.translatable(stack.getTranslationKey() + ".applied_preset").append(Text.translatable(newState.getBlock().getTranslationKey())));
        }
        Property<?> property = getAffectedProperty(player, state);
        if (property != null) {
            BlockState newState = CopperHammerItem.cycleValue(state, property);
            return applyState(player, newState, pos, world, stack,
                    Text.of(property.getName() + " " + CopperHammerItem.getValueString(newState, property)));
        }
        return false;
    }

    private static boolean applyState(PlayerEntity player, BlockState newState, BlockPos pos, WorldAccess world, ItemStack stack, Text resultMessage) {
        if (newState == null) return false;
        world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
        stack.damage(1, player, p -> p.sendToolBreakStatus(Hand.MAIN_HAND));
        world.playSound(null, pos, newState.getSoundGroup().getPlaceSound(), SoundCategory.BLOCKS, 1f, 1f);
        sendMessage(player, resultMessage);

        return true;
    }

    private static Property<?> getAffectedProperty(PlayerEntity player, BlockState state) {
        for (Property<?> p : state.getProperties()) {
            ChangeableProperties property = ChangeableProperties.fromProperty(p);
            if (property == null) continue;
            if (player.isSneaking()) {
                if (property.controller == PropertyController.SNEAKING) {
                    return property.property;
                }
                continue;
            }
            if (player.getHorizontalFacing() == property.controller.associatedDirection) {
                return property.property;
            }
        }
        return null;
    }

    private boolean modifyPreset(boolean save, PlayerEntity player, BlockState state, ItemStack stack) {
        Block block = state.getBlock();
        if (state.getProperties().isEmpty()) {
            return false;
        }
        NbtCompound blockStatePresets = stack.getOrCreateSubNbt("block_state_presets");
        String resultMessage;
        if (save) {
            blockStatePresets.put(blockIdOf(block), NbtHelper.fromBlockState(state));
            resultMessage = ".saved_preset";
        } else {
            blockStatePresets.remove(blockIdOf(block));
            resultMessage = ".removed_preset";
        }
        sendMessage(player, Text.translatable(this.getTranslationKey() + resultMessage).append(Text.translatable(block.getTranslationKey())));
        return true;
    }

    private static NbtCompound getPreset(BlockState state, ItemStack stack) {
        Block block = state.getBlock();
        NbtCompound blockStatePresets = stack.getOrCreateSubNbt("block_state_presets");
        return (NbtCompound) blockStatePresets.get(blockIdOf(block));
    }

    private static <T extends Comparable<T>> BlockState cycleValue(BlockState state, Property<T> property) {
        return state.with(property, CopperHammerItem.cycleProperty(property.getValues(), state.get(property)));
    }

    private static <T> T cycleProperty(Iterable<T> elements, @Nullable T current) {
        return Util.next(elements, current);
    }

    private static void sendMessage(PlayerEntity player, Text message) {
        ((ServerPlayerEntity)player).sendMessageToClient(message, true);
    }

    private static String blockIdOf(Block block) {
       return Registries.BLOCK.getId(block).toString();
    }

    private static <T extends Comparable<T>> String getValueString(BlockState state, Property<T> property) {
        return property.name(state.get(property));
    }
}
