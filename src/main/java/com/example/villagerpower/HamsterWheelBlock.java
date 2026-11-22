package com.example.villagerpower;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public class HamsterWheelBlock extends RotatedPillarKineticBlock implements IBE<HamsterWheelBE> {

    public HamsterWheelBlock(Properties props) {
        super(props);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        // 回し車の軸は水平。プレイヤーの向きに対して横向きに置く
        Direction.Axis axis = ctx.getHorizontalDirection().getClockWise().getAxis();
        return defaultBlockState().setValue(AXIS, axis);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face.getAxis() == state.getValue(AXIS);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(AXIS);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof HamsterWheelBE be)) return InteractionResult.PASS;
        ItemStack held = player.getItemInHand(hand);

        // カプセル(村人入り)で右クリック: 投入
        if (VillagerCapsuleItem.hasVillager(held)) {
            if (be.hasVillager()) return InteractionResult.PASS;
            if (!level.isClientSide) {
                be.insertVillager(VillagerCapsuleItem.take(held));
                level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_CLOSE, SoundSource.BLOCKS, 1f, 0.8f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        // 素手でしゃがみ右クリック: 取り出し
        if (held.isEmpty() && player.isShiftKeyDown() && be.hasVillager()) {
            if (!level.isClientSide) {
                player.getInventory().placeItemBackInInventory(VillagerCapsuleItem.make(be.removeVillager()));
                level.playSound(null, pos, SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.BLOCKS, 1f, 0.8f);
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof HamsterWheelBE be && be.hasVillager()) {
            popResource(level, pos, VillagerCapsuleItem.make(be.removeVillager()));
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public Class<HamsterWheelBE> getBlockEntityClass() {
        return HamsterWheelBE.class;
    }

    @Override
    public BlockEntityType<? extends HamsterWheelBE> getBlockEntityType() {
        return VillagerPower.WHEEL_BE.get();
    }
}
