package com.example.villagerpower;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** 村人の取引画面が開く前に横取りして、カプセルに封入する */
@Mod.EventBusSubscriber(modid = VillagerPower.MODID)
public class CapsuleEvents {
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        ItemStack held = event.getItemStack();
        if (!(held.getItem() instanceof VillagerCapsuleItem) || VillagerCapsuleItem.hasVillager(held)) return;
        if (!(event.getTarget() instanceof Villager villager)) return;

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(event.getLevel().isClientSide));
        if (event.getLevel().isClientSide) return;

        CompoundTag tag = new CompoundTag();
        if (!villager.save(tag)) return;
        held.getOrCreateTag().put(VillagerCapsuleItem.TAG, tag);
        villager.discard();
    }
}
