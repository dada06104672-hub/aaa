package com.example.villagerpower;

import javax.annotation.Nullable;

import com.simibubi.create.content.kinetics.base.GeneratingKineticBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HamsterWheelBE extends GeneratingKineticBlockEntity {
    /** サーバー: 完全な村人NBT / クライアント: 描画用の最小限NBT */
    @Nullable
    private CompoundTag villager;
    /** クライアント描画用エンティティ(キャッシュ) */
    @Nullable
    private Entity renderEntity;

    public HamsterWheelBE(BlockPos pos, BlockState state) {
        super(VillagerPower.WHEEL_BE.get(), pos, state);
    }

    public boolean hasVillager() {
        return villager != null;
    }

    public void insertVillager(CompoundTag tag) {
        villager = tag.copy();
        renderEntity = null;
        setChanged();
        sendData();
        updateGeneratedRotation();
    }

    public CompoundTag removeVillager() {
        CompoundTag t = villager;
        villager = null;
        renderEntity = null;
        setChanged();
        sendData();
        updateGeneratedRotation();
        return t;
    }

    /** 村人が走っている間だけ回る */
    @Override
    public float getGeneratedSpeed() {
        return hasVillager() ? Tuning.WHEEL_RPM : 0f;
    }

    /** 応力容量(SU/RPM)。WHEEL_RPM x WHEEL_CAPACITY = 256 SU */
    @Override
    public float calculateAddedStressCapacity() {
        float capacity = hasVillager() ? Tuning.WHEEL_CAPACITY : 0f;
        this.lastCapacityProvided = capacity;
        return capacity;
    }

    @Nullable
    public Entity getRenderEntity() {
        if (renderEntity == null && villager != null && level != null) {
            renderEntity = EntityType.create(villager, level).orElse(null);
        }
        return renderEntity;
    }

    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        if (villager == null) return;
        if (clientPacket) {
            CompoundTag c = new CompoundTag();
            c.putString("id", "minecraft:villager");
            if (villager.contains("VillagerData")) c.put("VillagerData", villager.get("VillagerData").copy());
            tag.put("Villager", c);
        } else {
            tag.put("Villager", villager);
        }
    }

    @Override
    public void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        villager = tag.contains("Villager", 10) ? tag.getCompound("Villager") : null;
        renderEntity = null;
    }
}
