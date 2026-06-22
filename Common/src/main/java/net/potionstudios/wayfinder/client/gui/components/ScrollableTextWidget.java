package net.potionstudios.wayfinder.client.gui.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractTextAreaWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class ScrollableTextWidget extends AbstractTextAreaWidget {

    private final List<FormattedCharSequence> wrappedLines;
    private static final int LINE_HEIGHT = 9;

    public ScrollableTextWidget(int x, int y, int width, int height, Component message, ScrollbarSettings scrollbarSettings) {
        super(x, y, width, height, Component.empty(), scrollbarSettings);
        this.wrappedLines = new ArrayList<>(Minecraft.getInstance().font.split(message, width - 12));
    }

    @Override
    protected int getInnerHeight() {
        return wrappedLines.size() * LINE_HEIGHT;
    }

    @Override
    protected void extractContents(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        for (int i = 0; i < wrappedLines.size(); i++) {
            int drawY = this.getY() + i * LINE_HEIGHT + 2;
            graphics.text(Minecraft.getInstance().font, wrappedLines.get(i), this.getX() + 4, drawY, 0xFF000000, false);
        }
    }

    @Override
    protected double scrollRate() {
        return 4.5;
    }

    @Override
    protected void updateWidgetNarration(@NonNull NarrationElementOutput narrationElementOutput) {}

    @Override
    protected void extractBackground(@NonNull GuiGraphicsExtractor graphics) {}

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= this.getX() && mouseX < this.getX() + this.getWidth()
                && mouseY >= this.getY() && mouseY < this.getY() + this.getHeight();
    }
}
