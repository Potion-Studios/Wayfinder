package net.potionstudios.wayfinder.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScrollableTextWidget extends AbstractTextAreaWidget {

    private final List<FormattedCharSequence> wrappedLines;
    private static final int LINE_HEIGHT = 9;

    public ScrollableTextWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, Component.empty());
        this.wrappedLines = new ArrayList<>(Minecraft.getInstance().font.split(message, width - 12));
    }

    @Override
    protected int getInnerHeight() {
        return wrappedLines.size() * LINE_HEIGHT;
    }

    @Override
    protected double scrollRate() {
        return 4.5;
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        for (int i = 0; i < wrappedLines.size(); i++) {
            int drawY = this.getY() + i * LINE_HEIGHT + 2;
            guiGraphics.drawString(Minecraft.getInstance().font, wrappedLines.get(i), this.getX() + 4, drawY, 0xFF000000, false);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {}

    @Override
    protected void renderBackground(@NotNull GuiGraphics guiGraphics) {}

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                && mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
    }
}
