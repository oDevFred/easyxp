package com.titammods.easyxp.registry;

import com.titammods.easyxp.EasyXP;
import com.titammods.easyxp.xp.item.XpMagnetItem;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(EasyXP.MOD_ID);

    public static final DeferredItem<XpMagnetItem> XP_MAGNET =
            ITEMS.register("xp_magnet", k -> new XpMagnetItem(
                    new Item.Properties()
                            .setId(ResourceKey.create(Registries.ITEM, k))
                            .stacksTo(1)
                            .rarity(Rarity.UNCOMMON)
            ));
}
