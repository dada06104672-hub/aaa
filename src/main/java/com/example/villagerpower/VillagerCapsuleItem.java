package com.example.villagerpower;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

/** 村人を封入するカプセル。村人に右クリックで封入、ブロックに右クリックで解放。 */
public class VillagerCapsuleItem extends Item {
    public static final String TAG = "Villager";

    public VillagerCapsuleItem(Properties props) {
        super(props);
    }

    public static boolean hasVillager(ItemStack stack) {
        return stack.getItem() instanceof VillagerCapsuleItem && stack.hasTag() && stack.getTag().contains(TAG, 10);
    }

    /** カプセルから村人データを取り出す(カプセルは空になる) */
    public static CompoundTag take(ItemStack stack) {
        CompoundTag tag = stack.getTag().getCompound(TAG).copy();
        stack.removeTagKey(TAG);
        return tag;
    }

    public static ItemStack make(CompoundTag villager) {
        ItemStack stack = new ItemStack(VillagerPower.CAPSULE.get());
        stack.getOrCreateTag().put(TAG, villager);
        return stack;
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        ItemStack stack = ctx.getItemInHand();
        if (!hasVillager(stack)) return InteractionResult.PASS;
        Level level = ctx.getLevel();
        if (!level.isClientSide) {
            BlockPos pos = ctx.getClickedPos().relative(ctx.getClickedFace());
            CompoundTag tag = take(stack);
            Entity e = EntityType.loadEntityRecursive(tag, level, ent -> {
                ent.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, ctx.getRotation(), 0f);
                return ent;
            });
            if (e != null) level.addFreshEntity(e);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return hasVillager(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(hasVillager(stack)
                ? "tooltip.villagerpower.capsule_filled" : "tooltip.villagerpower.capsule_empty")
                .withStyle(ChatFormatting.GRAY));
    }
}
