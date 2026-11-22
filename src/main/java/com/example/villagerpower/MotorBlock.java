package com.example.villagerpower;

import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;
import com.simibubi.create.foundation.block.IBE;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class MotorBlock extends RotatedPillarKineticBlock implements IBE<MotorBE> {

    public MotorBlock(Properties props) {
        super(props);
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
    public Class<MotorBE> getBlockEntityClass() {
        return MotorBE.class;
    }

    @Override
    public BlockEntityType<? extends MotorBE> getBlockEntityType() {
        return VillagerPower.MOTOR_BE.get();
    }
}
