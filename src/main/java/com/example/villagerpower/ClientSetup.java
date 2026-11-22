package com.example.villagerpower;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = VillagerPower.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    public static final ResourceLocation WHEEL_ROTOR = new ResourceLocation(VillagerPower.MODID, "block/hamster_wheel_rotor");
    public static final ResourceLocation MOTOR_ROTOR = new ResourceLocation(VillagerPower.MODID, "block/kinetic_motor_rotor");

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        event.register(WHEEL_ROTOR);
        event.register(MOTOR_ROTOR);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(VillagerPower.WHEEL_BE.get(), WheelRenderer::new);
        event.registerBlockEntityRenderer(VillagerPower.MOTOR_BE.get(), ctx -> new RotorRenderer<MotorBE>(ctx, MOTOR_ROTOR));
    }

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> ItemProperties.register(VillagerPower.CAPSULE.get(),
                new ResourceLocation(VillagerPower.MODID, "filled"),
                (stack, level, entity, seed) -> VillagerCapsuleItem.hasVillager(stack) ? 1f : 0f));
    }
}
