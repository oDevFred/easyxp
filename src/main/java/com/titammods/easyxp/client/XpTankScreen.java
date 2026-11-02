package com.titammods.easyxp.client;

import com.titammods.easyxp.EasyXP;
import com.titammods.easyxp.xp.menu.XpTankMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;

public class XpTankScreen extends AbstractContainerScreen<XpTankMenu> {
    public static final int IMAGE_WIDTH = 176;
    public static final int IMAGE_HEIGHT = 180;

    private static final int BAR_X = 8, BAR_Y = 28, BAR_W = 160, BAR_H = 10;

    private static final int BTN_W = 78, BTN_H = 16;
    private static final int[][] BUTTONS = {
            {8, 44}, {90, 44},
            {8, 64}, {90, 64}
    };
    private static final int[] BTN_IDS = {
            XpTankMenu.BTN_DEPOSIT_LEVEL, XpTankMenu.BTN_DEPOSIT_ALL,
            XpTankMenu.BTN_WITHDRAW_LEVEL, XpTankMenu.BTN_WITHDRAW_ALL
    };
    private static final String[] BTN_KEYS = {
            "deposit_level", "deposit_all", "withdraw_level", "withdraw_all"
    };

    private int hovered = -1;

    public XpTankScreen(XpTankMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    private boolean inRect(double mx, double my, int rx, int ry, int rw, int rh) {
        return mx >= leftPos + rx && mx < leftPos + rx + rw
                && my >= topPos + ry && my < topPos + ry + rh;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(g, mouseX, mouseY, partialTick);
        int x = leftPos, y = topPos;

        g.fill(x, y, x + IMAGE_WIDTH, y + IMAGE_HEIGHT, 0xFFC6C6C6);
        g.fill(x, y, x + IMAGE_WIDTH, y + 1, 0xFFFFFFFF);
        g.fill(x, y + 1, x + 1, y + IMAGE_HEIGHT, 0xFFFFFFFF);
        g.fill(x, y + IMAGE_HEIGHT - 1, x + IMAGE_WIDTH, y + IMAGE_HEIGHT, 0xFF555555);
        g.fill(x + IMAGE_WIDTH - 1, y + 1, x + IMAGE_WIDTH, y + IMAGE_HEIGHT, 0xFF555555);

        g.fill(x + BAR_X - 1, y + BAR_Y - 1, x + BAR_X + BAR_W + 1, y + BAR_Y + BAR_H + 1, 0xFF000000);
        g.fill(x + BAR_X, y + BAR_Y, x + BAR_X + BAR_W, y + BAR_Y + BAR_H, 0xFF3B3B3B);
        int fill = (int) Math.round(BAR_W * (menu.xpStored() / (double) Math.max(1, menu.capacity())));
        g.fill(x + BAR_X, y + BAR_Y, x + BAR_X + fill, y + BAR_Y + BAR_H, 0xFF7EFC20);

        for (int i = 0; i < BUTTONS.length; i++) {
            int bx = BUTTONS[i][0], by = BUTTONS[i][1];
            g.fill(x + bx, y + by, x + bx + BTN_W, y + by + BTN_H, i == hovered ? 0xFFB0B0B0 : 0xFF8B8B8B);
            g.fill(x + bx, y + by, x + bx + BTN_W, y + by + 1, 0xFF37373B);          // sombra sup.
            g.fill(x + bx, y + by + 1, x + bx + 1, y + by + BTN_H, 0xFF37373B);      // sombra esq.
            g.fill(x + bx, y + by + BTN_H - 1, x + bx + BTN_W, y + by + BTN_H, 0xFFFFFFFF); // brilho inf.
            g.fill(x + bx + BTN_W - 1, y + by + 1, x + bx + BTN_W, y + by + BTN_H, 0xFFFFFFFF); // brilho dir.
        }
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor g, int mouseX, int mouseY) {
        g.text(font, title, 8, 6, 0xFF404040, false);
        Component stored = Component.literal(menu.xpStored() + " / " + menu.capacity() + " XP");
        g.centeredText(font, stored, IMAGE_WIDTH / 2, 17, 0xFF3F3F3F);

        for (int i = 0; i < BUTTONS.length; i++) {
            Component label = Component.translatable("gui." + EasyXP.MOD_ID + "." + BTN_KEYS[i]);
            g.text(font, label, BUTTONS[i][0] + (BTN_W - font.width(label)) / 2,
                    BUTTONS[i][1] + (BTN_H - 8) / 2, 0xFF404040, false);
        }
        g.text(font, playerInventoryTitle, 8, 83, 0xFF404040, false);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(g, mouseX, mouseY, partialTick);
        hovered = -1;
        for (int i = 0; i < BUTTONS.length; i++) {
            if (inRect(mouseX, mouseY, BUTTONS[i][0], BUTTONS[i][1], BTN_W, BTN_H)) {
                hovered = i;
                g.setTooltipForNextFrame(font,
                        Component.translatable("gui." + EasyXP.MOD_ID + "." + BTN_KEYS[i]),
                        mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            for (int i = 0; i < BUTTONS.length; i++) {
                if (inRect(event.x(), event.y(), BUTTONS[i][0], BUTTONS[i][1], BTN_W, BTN_H)) {
                    send(BTN_IDS[i]);
                    return true;
                }
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    private void send(int id) {
        if (minecraft != null && minecraft.gameMode != null) {
            minecraft.gameMode.handleInventoryButtonClick(menu.containerId, id);
            minecraft.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
    }
}
