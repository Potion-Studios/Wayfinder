package net.potionstudios.wayfinder.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.potionstudios.wayfinder.Wayfinder;
import org.jetbrains.annotations.NotNull;

public class WayfinderScreen extends Screen {

    private static final ResourceLocation BOOK_TEXTURE = Wayfinder.id("textures/gui/book_gui.png");

    protected static final int IMAGE_WIDTH = 288;
    protected static final int IMAGE_HEIGHT = 208;
    protected int leftPos;
    protected int bottomPos;
    protected int rightPos;
    protected int topPos;
    protected int startXRightPage;
    protected int startXLeftPage;
    protected int pageBackButtonX = this.leftPos + 5;
    protected int pageButtonY = this.topPos - 10;
    protected int pageButtonForwardX = this.rightPos - 5;

    public WayfinderScreen() {
        super(Component.literal(""));
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = ((this.width - IMAGE_WIDTH) / 2);
        this.bottomPos = (this.height - IMAGE_HEIGHT) / 2 - 15;
        this.rightPos = this.leftPos + IMAGE_WIDTH;
        this.topPos = this.bottomPos + IMAGE_HEIGHT;
        this.startXRightPage = (this.leftPos + (IMAGE_WIDTH / 4) + ((IMAGE_WIDTH) / 3)) - 18;
        this.startXLeftPage = this.leftPos + 15;
        this.pageBackButtonX = this.leftPos + 15;
        this.pageButtonY = this.topPos - 13 - 13;
        this.pageButtonForwardX = this.rightPos - 23 - 22;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        RenderSystem.setShaderTexture(0, BOOK_TEXTURE);
        guiGraphics.blit(BOOK_TEXTURE, this.leftPos, this.bottomPos, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

    }
}
