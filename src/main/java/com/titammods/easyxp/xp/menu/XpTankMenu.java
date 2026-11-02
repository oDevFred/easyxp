package com.titammods.easyxp.xp.menu;

import com.titammods.easyxp.registry.ModMenus;
import com.titammods.easyxp.xp.XpMath;
import com.titammods.easyxp.xp.blockentity.XpTankBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public class XpTankMenu extends AbstractContainerMenu {
    public static final int BTN_DEPOSIT_LEVEL = 0;
    public static final int BTN_DEPOSIT_ALL = 1;
    public static final int BTN_WITHDRAW_LEVEL = 2;
    public static final int BTN_WITHDRAW_ALL = 3;

    private static final int DATA_XP = 0;
    private static final int DATA_CAPACITY = 1;

    private final @Nullable XpTankBlockEntity tank;
    private final ContainerData data;

    public XpTankMenu(int containerId, Inventory playerInv) {
        this(containerId, playerInv, null, new SimpleContainerData(2));
    }

    public XpTankMenu(int containerId, Inventory playerInv, @Nullable XpTankBlockEntity tank) {
        this(containerId, playerInv, tank, new ContainerData() {
            @Override
            public int get(int index) {
                return index == DATA_XP ? tank.xpStored() : tank.capacity();
            }

            @Override
            public void set(int index, int value) {}

            @Override
            public int getCount() {
                return 2;
            }
        });
    }

    private XpTankMenu(int containerId, Inventory playerInv, @Nullable XpTankBlockEntity tank,
                       ContainerData data) {
        super(ModMenus.XP_TANK.get(), containerId);
        this.tank = tank;
        this.data = data;
        this.addDataSlots(data);

        for (int row = 0; row < 3; row++)
            for (int col = 0; col < 9; col++)
                this.addSlot(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 96 + row * 18));
        for (int col = 0; col < 9; col++)
            this.addSlot(new Slot(playerInv, col, 8 + col * 18, 154));
    }

    public int xpStored() {
        return data.get(DATA_XP);
    }

    public int capacity() {
        return data.get(DATA_CAPACITY);
    }

    @Override
    public boolean stillValid(Player player) {
        if (tank == null) return true;
        return !tank.isRemoved() && player.distanceToSqr(
                tank.getBlockPos().getX() + 0.5,
                tank.getBlockPos().getY() + 0.5,
                tank.getBlockPos().getZ() + 0.5) < 64.0;
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (tank == null || player.level().isClientSide() || !stillValid(player)) return false;

        switch (id) {
            case BTN_DEPOSIT_LEVEL -> {
                int space = tank.capacity() - tank.xpStored();
                int move = Math.min(XpMath.pointOfOneLevel(player), space);
                if (move > 0) {
                    player.giveExperiencePoints(-move);
                    tank.addXp(move);
                }
                return move > 0;
            }
            case BTN_DEPOSIT_ALL -> {
                int space = tank.capacity() - tank.xpStored();
                int move = Math.min(XpMath.totalPointsOf(player), space);
                if (move > 0) {
                    player.giveExperiencePoints(-move);
                    tank.addXp(move);
                }
                return move > 0;
            }
            case BTN_WITHDRAW_LEVEL -> {
                if (player.experienceLevel <= 0) return false;
                int want = XpMath.costForLevel(player.experienceLevel - 1);
                int move = Math.min(want, tank.xpStored());
                if (move > 0) {
                    tank.addXp(move);
                    player.giveExperiencePoints(move);
                }
                return move > 0;
            }
            case BTN_WITHDRAW_ALL -> {
                int move = tank.takeXp(tank.xpStored());
                if (move > 0) player.giveExperiencePoints(move);
                return move > 0;
            }
            default -> {
                return false;
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        if (index < 27) {
            if (!this.moveItemStackTo(stack, 27, 36, true)) return ItemStack.EMPTY;
        } else {
            if (!this.moveItemStackTo(stack, 0, 27, false)) return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }
}
