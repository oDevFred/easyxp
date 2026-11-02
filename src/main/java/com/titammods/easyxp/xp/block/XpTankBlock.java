package com.titammods.easyxp.xp.block;

import com.mojang.serialization.MapCodec;
import com.titammods.easyxp.registry.ModBlocks;
import com.titammods.easyxp.xp.blockentity.XpTankBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class XpTankBlock extends BaseEntityBlock {
    private final int capacity;
    private final double vacuumRadius;

    public XpTankBlock(Properties props, int capacity, double vacuumRadius) {
        super(props);
        this.capacity = capacity;
        this.vacuumRadius = vacuumRadius;
    }

    public int capacity() {
        return capacity;
    }

    public double vacuumRadius() {
        return vacuumRadius;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return simpleCodec(props -> new XpTankBlock(props, this.capacity, this.vacuumRadius));
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new XpTankBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return createTickerHelper(type, ModBlocks.XP_TANK_BE.get(), XpTankBlockEntity::serverTick);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof XpTankBlockEntity tank) {
            player.openMenu(tank);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof XpTankBlockEntity tank) {
            double fill = tank.xpStored() / (double) Math.max(1, tank.capacity());
            return (int) Math.ceil(fill * 15.0);
        }
        return 0;
    }
}
