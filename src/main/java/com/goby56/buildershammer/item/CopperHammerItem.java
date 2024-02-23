package com.goby56.buildershammer.item;


import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class CopperHammerItem extends Item {
    public CopperHammerItem(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        if (!world.isClient && !state.getProperties().isEmpty()) {
            NbtCompound blockStatePreset = getPreset(state, miner.getStackInHand(Hand.MAIN_HAND));
            if (blockStatePreset != null) {
                BlockState preset = NbtHelper.toBlockState(world.createCommandRegistryWrapper(RegistryKeys.BLOCK), blockStatePreset);
                applyState(preset, pos, world);
                sendMessage(miner, Text.translatable(this.getTranslationKey() + ".applied_preset").append(Text.translatable(preset.getBlock().getTranslationKey())));
            }
        }
        return false;
    }

    private void applyState(BlockState newState, BlockPos pos, WorldAccess world) {
        world.setBlockState(pos, newState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
    }

    private void cycleAlongDirection(PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos, ItemStack stack) {

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

    private NbtCompound getPreset(BlockState state, ItemStack stack) {
        Block block = state.getBlock();
        NbtCompound blockStatePresets = stack.getOrCreateSubNbt("block_state_presets");
        return (NbtCompound) blockStatePresets.get(blockIdOf(block));
    }

    public final boolean use(PlayerEntity player, BlockState state, WorldAccess world, BlockPos pos, boolean update, ItemStack stack) {
        Block block = state.getBlock();
        StateManager<Block, BlockState> stateManager = block.getStateManager();
        Collection<Property<?>> collection = stateManager.getProperties();
        String blockID = Registries.BLOCK.getId(block).toString();
        if (collection.isEmpty()) {
            CopperHammerItem.sendMessage(player, Text.translatable(this.getTranslationKey() + ".empty", blockID));
            return false;
        }
        NbtCompound nbtCompound = stack.getOrCreateSubNbt("DebugProperty");
        String string2 = nbtCompound.getString(blockID);
        Property<?> property = stateManager.getProperty(string2);
        if (update) {
            if (property == null) {
                property = collection.iterator().next();
            }
            BlockState blockState = CopperHammerItem.cycleValue(state, property, player.shouldCancelInteraction());
            world.setBlockState(pos, blockState, Block.NOTIFY_LISTENERS | Block.FORCE_STATE);
            CopperHammerItem.sendMessage(player, Text.translatable(this.getTranslationKey() + ".update", property.getName(), CopperHammerItem.getValueString(blockState, property)));
        } else {
            property = CopperHammerItem.cycleProperty(collection, property, player.shouldCancelInteraction());
            String string3 = property.getName();
            nbtCompound.putString(blockID, string3);
            CopperHammerItem.sendMessage(player, Text.translatable(this.getTranslationKey() + ".select", string3, CopperHammerItem.getValueString(state, property)));
        }
        return true;
    }

    private static <T extends Comparable<T>> BlockState cycleValue(BlockState state, Property<T> property, boolean inverse) {
        return state.with(property, CopperHammerItem.cycleProperty(property.getValues(), state.get(property), inverse));
    }

    private static <T> T cycleProperty(Iterable<T> elements, @Nullable T current, boolean inverse) {
        return inverse ? Util.previous(elements, current) : Util.next(elements, current);
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
