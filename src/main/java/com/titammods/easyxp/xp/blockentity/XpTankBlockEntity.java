package com.titammods.easyxp.xp.blockentity;

import com.titammods.easyxp.registry.ModBlocks;
import com.titammods.easyxp.xp.block.XpTankBlock;
import com.titammods.easyxp.xp.menu.XpTankMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class XpTankBlockEntity extends BlockEntity implements MenuProvider {
    private static final String XP_KEY = "Xp";
    private static final int VACUUM_INTERVAL_TICKS = 10;

    private int xp;

    public XpTankBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.XP_TANK_BE.get(), pos, state);
    }

    public int xpStored() {
        return xp;
    }

    public int capacity() {
        return getBlockState().getBlock() instanceof XpTankBlock tank ? tank.capacity() : 0;
    }

    // Raio de vácuo do tier deste tanque (4 no básico, 8 no upgraded)
    public double vacuumRadius() {
        return getBlockState().getBlock() instanceof XpTankBlock tank ? tank.vacuumRadius() : 0;
    }

    public int addXp(int amount) {
        int accepted = Math.clamp(amount, 0, capacity() - xp);
        if (accepted > 0) {
            xp += accepted;
            setChanged();
            if (level != null) level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        }
        return accepted;
    }

    public int takeXp(int amount) {
        int taken = Math.clamp(amount, 0, xp);
        if (taken > 0) {
            xp -= taken;
            setChanged();
            if (level != null) level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
        }
        return taken;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, XpTankBlockEntity tank) {
        if (level.getGameTime() % VACUUM_INTERVAL_TICKS != 0L) return;

        AABB area = new AABB(pos).inflate(tank.vacuumRadius());
        boolean absorbed = false;
        for (ExperienceOrb orb : level.getEntitiesOfClass(ExperienceOrb.class, area)) {
            if (!orb.isAlive()) continue;
            int value = orb.getValue();
            int accepted = tank.addXp(value);
            if (accepted <= 0) break;
            if (accepted < value) {
                orb.setValue(value - accepted);
            } else {
                orb.discard();
            }
            absorbed = true;
        }
        if (absorbed) {
            level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.4f, 0.8f);
        }
    }

    @Override
    public void preRemoveSideEffects(BlockPos pos, BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (level instanceof ServerLevel serverLevel && xp > 0) {
            ExperienceOrb.award(serverLevel, Vec3.atCenterOf(pos), xp);
            xp = 0;
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(XP_KEY, xp);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        xp = input.getIntOr(XP_KEY, 0);
    }

    @Override
    public Component getDisplayName() {
        return getBlockState().getBlock().getName();
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new XpTankMenu(containerId, inventory, this);
    }
}