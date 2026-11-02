package com.titammods.easyxp.xp.item;

import com.titammods.easyxp.EasyXP;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.phys.Vec3;

import java.util.function.Consumer;

public class XpMagnetItem extends Item {
    private static final double PULL_RADIUS = 6.0;    // raio de atracao
    private static final double ABSORB_RADIUS = 2.0;  // distancia para absorver
    private static final double PULL_STRENGTH = 0.06;

    public XpMagnetItem(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (slot != EquipmentSlot.MAINHAND && slot != EquipmentSlot.OFFHAND) return;
        if (!(entity instanceof Player player) || player.isSpectator()) return;
        if (level.getGameTime() % 2L != 0L) return; // processa a cada 2 ticks

        for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class,
                player.getBoundingBox().inflate(PULL_RADIUS))) {
            if (!orb.isAlive()) continue;

            if (orb.distanceTo(player) <= ABSORB_RADIUS) {
                int value = orb.getValue();
                if (value > 0) {
                    player.giveExperiencePoints(value);
                    orb.discard();
                    level.playSound(null, player.blockPosition(),
                            SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, 1.0f);
                }
            } else {
                Vec3 pull = player.position().add(0, 0.6, 0)
                        .subtract(orb.position()).normalize().scale(PULL_STRENGTH);
                orb.setDeltaMovement(orb.getDeltaMovement().add(pull));
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("item." + EasyXP.MOD_ID + ".xp_magnet.tooltip"));
    }
}
