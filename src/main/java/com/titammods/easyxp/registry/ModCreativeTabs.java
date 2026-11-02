package com.titammods.easyxp.registry;

import com.titammods.easyxp.EasyXP;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EasyXP.MOD_ID);

    public static final Supplier<CreativeModeTab> MAIN_TAB = TABS.register("main",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + EasyXP.MOD_ID + ".main"))
                    .icon(() -> new ItemStack(ModBlocks.XP_TANK.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModBlocks.XP_TANK.get());
                        output.accept(ModBlocks.XP_TANK_UPGRADED.get());
                        output.accept(ModItems.XP_MAGNET.get());
                    })
                    .build());
}
