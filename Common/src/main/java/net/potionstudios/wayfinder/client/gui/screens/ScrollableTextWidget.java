package net.potionstudios.wayfinder.client.gui.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class ScrollableTextWidget extends AbstractScrollWidget {
    public ScrollableTextWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Override
    protected int getInnerHeight() {
        return 0;
    }

    @Override
    protected double scrollRate() {
        return 0;
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

    }
}
