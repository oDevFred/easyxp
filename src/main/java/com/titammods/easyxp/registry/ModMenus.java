package com.titammods.easyxp.registry;

import com.titammods.easyxp.EasyXP;
import com.titammods.easyxp.xp.menu.XpTankMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, EasyXP.MOD_ID);

    public static final Supplier<MenuType<XpTankMenu>> XP_TANK =
            MENUS.register("xp_tank",
                    () -> new MenuType<>((id, inv) -> new XpTankMenu(id, inv),
                            FeatureFlags.DEFAULT_FLAGS));
}
