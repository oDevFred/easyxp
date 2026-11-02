package com.titammods.easyxp.registry;

import com.titammods.easyxp.EasyXP;
import com.titammods.easyxp.xp.block.XpTankBlock;
import com.titammods.easyxp.xp.blockentity.XpTankBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Function;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(EasyXP.MOD_ID);

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EasyXP.MOD_ID);

    public static final int BASIC_CAPACITY = 1395;
    public static final int UPGRADED_CAPACITY = 5345;

    public static final DeferredBlock<XpTankBlock> XP_TANK =
            registerBlock("xp_tank", k -> new XpTankBlock(tankProps(k), BASIC_CAPACITY, 4.0));

    public static final DeferredBlock<XpTankBlock> XP_TANK_UPGRADED =
            registerBlock("xp_tank_upgraded", k -> new XpTankBlock(tankProps(k), UPGRADED_CAPACITY, 8.0));

    public static final Supplier<BlockEntityType<XpTankBlockEntity>> XP_TANK_BE =
            BLOCK_ENTITIES.register("xp_tank",
                    () -> new BlockEntityType<>(XpTankBlockEntity::new, XP_TANK.get(), XP_TANK_UPGRADED.get()));

    private static BlockBehaviour.Properties tankProps(Identifier key) {
        return BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, key))
                .mapColor(MapColor.COLOR_CYAN)
                .sound(SoundType.COPPER)
                .strength(2.5f, 6.0f)
                .requiresCorrectToolForDrops();
    }

    private static <T extends Block> DeferredBlock<T> registerBlock(String name,
                                                                    Function<Identifier, T> factory) {
        DeferredBlock<T> block = BLOCKS.register(name, factory);
        ModItems.ITEMS.register(name,
                k -> new BlockItem(block.get(),
                        new Item.Properties().setId(ResourceKey.create(Registries.ITEM, k))));
        return block;
    }
}
