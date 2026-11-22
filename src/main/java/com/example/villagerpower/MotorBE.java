package com.example.villagerpower;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.RotatedPillarKineticBlock;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

/** 回転力(応力)を消費してFEを発電するモーター */
public class MotorBE extends KineticBlockEntity {

    private static class Buffer extends EnergyStorage {
        Buffer(int capacity, int maxExtract) {
            super(capacity, 0, maxExtract);
        }

        int add(int amount) {
            int accepted = Math.min(capacity - energy, amount);
            if (accepted > 0) energy += accepted;
            return Math.max(accepted, 0);
        }

        void set(int value) {
            energy = Mth.clamp(value, 0, capacity);
        }
    }

    private final Buffer buffer = new Buffer(Tuning.MOTOR_BUFFER, Tuning.MOTOR_MAX_OUT);
    private final LazyOptional<IEnergyStorage> energyCap = LazyOptional.of(() -> buffer);
    private int lastProduced;

    public MotorBE(BlockPos pos, BlockState state) {
        super(VillagerPower.MOTOR_BE.get(), pos, state);
    }

    /** 消費する応力(SU/RPM) */
    @Override
    public float calculateStressApplied() {
        float impact = Tuning.MOTOR_IMPACT;
        this.lastStressApplied = impact;
        return impact;
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        float rpm = Math.abs(getSpeed());
        lastProduced = Math.round(rpm * Tuning.MOTOR_IMPACT * Tuning.FE_PER_SU);
        if (lastProduced > 0) buffer.add(lastProduced);

        // 軸方向以外の4面に隣接するエネルギー受け入れ先へ送る
        Direction.Axis axis = getBlockState().getValue(RotatedPillarKineticBlock.AXIS);
        for (Direction dir : Direction.values()) {
            if (dir.getAxis() == axis) continue;
            int offer = buffer.extractEnergy(Tuning.MOTOR_MAX_OUT, true);
            if (offer <= 0) break;
            BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(dir));
            if (neighbor == null) continue;
            neighbor.getCapability(ForgeCapabilities.ENERGY, dir.getOpposite()).ifPresent(target -> {
                if (!target.canReceive()) return;
                int accepted = target.receiveEnergy(offer, false);
                if (accepted > 0) buffer.extractEnergy(accepted, false);
            });
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            Direction.Axis axis = getBlockState().getValue(RotatedPillarKineticBlock.AXIS);
            if (side == null || side.getAxis() != axis) return energyCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        energyCap.invalidate();
    }

    @Override
    public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
        super.addToGoggleTooltip(tooltip, isPlayerSneaking);
        tooltip.add(Component.literal("    ").append(
                Component.translatable("tooltip.villagerpower.motor_output", lastProduced).withStyle(ChatFormatting.AQUA)));
        return true;
    }

    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("Energy", buffer.getEnergyStored());
        tag.putInt("Produced", lastProduced);
    }

    @Override
    public void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        buffer.set(tag.getInt("Energy"));
        lastProduced = tag.getInt("Produced");
    }
}
