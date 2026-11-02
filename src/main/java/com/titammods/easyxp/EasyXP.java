package com.titammods.easyxp;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.ModContainer;
import com.titammods.easyxp.registry.ModBlocks;
import com.titammods.easyxp.registry.ModCreativeTabs;
import com.titammods.easyxp.registry.ModItems;
import com.titammods.easyxp.registry.ModMenus;

@Mod(EasyXP.MOD_ID)
public class EasyXP {
    public static final String MOD_ID = "easyxp";

    public EasyXP(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.BLOCK_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModMenus.MENUS.register(modEventBus);
        ModCreativeTabs.TABS.register(modEventBus);
    }
}
