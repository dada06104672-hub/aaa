package com.example.villagerpower;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(VillagerPower.MODID)
public class VillagerPower {
    public static final String MODID = "villagerpower";

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<HamsterWheelBlock> WHEEL = BLOCKS.register("hamster_wheel",
            () -> new HamsterWheelBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                    .strength(2.5f).sound(SoundType.WOOD).noOcclusion()));
    public static final RegistryObject<MotorBlock> MOTOR = BLOCKS.register("kinetic_motor",
            () -> new MotorBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL)
                    .strength(3.0f).sound(SoundType.METAL).noOcclusion()));

    public static final RegistryObject<Item> WHEEL_ITEM = ITEMS.register("hamster_wheel",
            () -> new BlockItem(WHEEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MOTOR_ITEM = ITEMS.register("kinetic_motor",
            () -> new BlockItem(MOTOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> CAPSULE = ITEMS.register("villager_capsule",
            () -> new VillagerCapsuleItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<BlockEntityType<HamsterWheelBE>> WHEEL_BE = BLOCK_ENTITIES.register("hamster_wheel",
            () -> BlockEntityType.Builder.of(HamsterWheelBE::new, WHEEL.get()).build(null));
    public static final RegistryObject<BlockEntityType<MotorBE>> MOTOR_BE = BLOCK_ENTITIES.register("kinetic_motor",
            () -> BlockEntityType.Builder.of(MotorBE::new, MOTOR.get()).build(null));

    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.villagerpower"))
                    .icon(() -> new ItemStack(WHEEL_ITEM.get()))
                    .displayItems((params, out) -> {
                        out.accept(CAPSULE.get());
                        out.accept(WHEEL_ITEM.get());
                        out.accept(MOTOR_ITEM.get());
                    }).build());

    public VillagerPower() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITIES.register(bus);
        TABS.register(bus);
    }
}
