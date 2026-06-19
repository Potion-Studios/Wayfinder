package net.potionstudios.wayfinder.client.gui.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractScrollWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ScrollableTextWidget extends AbstractScrollWidget {

    private final List<FormattedCharSequence> wrappedLines;
    private static final int LINE_HEIGHT = 9;

    public ScrollableTextWidget(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, Component.empty());
        this.wrappedLines = new ArrayList<>(Minecraft.getInstance().font.split(message, width - this.scrollbarWidth() - 6));
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
    public int scrollbarWidth() {
        return 6;
    }

    @Override
    protected void renderContents(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        PoseStack pose = guiGraphics.pose();
        guiGraphics.enableScissor(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight());
        pose.pushPose();
        pose.translate(0, -this.scrollAmount(), 0);

        for (int i = 0; i < wrappedLines.size(); i++) {
            int drawY = this.getY() + i * LINE_HEIGHT + 2;
            guiGraphics.drawString(Minecraft.getInstance().font, wrappedLines.get(i), this.getX() + 4, drawY, 0x000000, false);
        }

        pose.popPose();
        guiGraphics.disableScissor();
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
