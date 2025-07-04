package net.potionstudios.wayfinder.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.network.packets.WayfinderBiomePacket;
import net.potionstudios.wayfinder.network.packets.WayfinderSitPacket;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class WayfinderScreen extends Screen {

    private static final ResourceLocation BOOK_TEXTURE = Wayfinder.id("textures/gui/book_gui.png");
    private static final ResourceLocation LOGO = Wayfinder.id("textures/gui/wayfinder.png");

    protected static final int IMAGE_WIDTH = 288;
    protected static final int IMAGE_HEIGHT = 208;
    protected int leftPos;
    protected int bottomPos;
    protected int rightPos;
    protected int topPos;
    protected int startXRightPage;
    protected int startXLeftPage;
    private final List<ResourceLocation> biomes;
    private boolean isSitting;
    private final boolean wasSitting;
    private BiomeList biomeList;
    private ResourceLocation current;

    public WayfinderScreen(List<ResourceLocation> biomeRegistry, ResourceLocation current, boolean isSitting) {
        super(Component.literal(""));
        this.biomes = biomeRegistry;
        this.isSitting = isSitting;
        this.current = current;
        this.wasSitting = isSitting;
    }

    @Override
    protected void init() {
        this.leftPos = ((this.width - IMAGE_WIDTH) / 2);
        this.bottomPos = (this.height - IMAGE_HEIGHT) / 2 - 15;
        this.rightPos = this.leftPos + IMAGE_WIDTH;
        this.topPos = this.bottomPos + IMAGE_HEIGHT;
        this.startXRightPage = (this.leftPos + (IMAGE_WIDTH / 4) + ((IMAGE_WIDTH) / 3)) - 18;
        this.startXLeftPage = this.leftPos + 15;

        EditBox searchBox = new EditBox(font, (IMAGE_WIDTH / 2) - 25, 15, Component.translatable("gui.wayfinder.search"));
        searchBox.setPosition(rightPos - (IMAGE_WIDTH / 2) + 10, topPos - IMAGE_HEIGHT + 10);
        searchBox.setResponder(this::updateBiomeList);
        addRenderableWidget(searchBox);

        ScrollableTextWidget descriptionWidget = new ScrollableTextWidget(leftPos + 10, topPos + 10, 120, 100, Component.translatable("gui.wayfinder.description"));
        descriptionWidget.setPosition(leftPos + 15, 100 + (bottomPos + (IMAGE_HEIGHT / 4) - (100 / 2))- 23);
        addRenderableWidget(descriptionWidget);

        biomeList = new BiomeList(minecraft, (IMAGE_WIDTH / 2) - 25, IMAGE_HEIGHT - 70, 0, 10);
        biomeList.setPosition(rightPos - (IMAGE_WIDTH / 2) + 10, topPos - IMAGE_HEIGHT + 28);
        biomeList.setBiomes(biomes);
        addRenderableWidget(biomeList);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int logoWidth = 100;
        int logoHeight = 100;
        int centerX = leftPos + (IMAGE_WIDTH / 4) - (logoWidth / 2);
        int centerY = bottomPos + (IMAGE_HEIGHT / 4) - (logoHeight / 2);
        guiGraphics.blit(LOGO, centerX, centerY, 0, 0, logoWidth, logoHeight, logoWidth, logoHeight);

        int buttonY = bottomPos + IMAGE_HEIGHT - 40;

        Button selectButton = new Button(rightPos - (IMAGE_WIDTH / 2) + 10, buttonY, 50, 20, Component.translatable("gui.wayfinder.button.select"), button -> {
            PlatformHandler.PLATFORM_HANDLER.sendToServer(new WayfinderBiomePacket(biomeList.getFocused().getBiome()));
            this.onClose();
        }, Button.DEFAULT_NARRATION);
        selectButton.active = biomeList.getFocused() != null;
        addRenderableWidget(selectButton);

        Button clearButton = new Button(rightPos - 70, buttonY, 50, 20, Component.translatable("gui.wayfinder.button.clear"), button -> {
            PlatformHandler.PLATFORM_HANDLER.sendToServer(new WayfinderBiomePacket(Wayfinder.id("clear_packet")));
            current = Wayfinder.id("clear_packet");
        }, Button.DEFAULT_NARRATION);

        clearButton.active = !current.equals(Wayfinder.id("clear_packet"));
        addRenderableWidget(clearButton);

        // X positions for left and right buttons on the left page
        int buttonXLeft = leftPos + 20;
        int buttonXRight = leftPos + (IMAGE_WIDTH / 2) - 60;

        Button sitButton = new Button(buttonXLeft, buttonY, 50, 20, Component.translatable("gui.wayfinder.button.sit"), button -> isSitting = true, Button.DEFAULT_NARRATION);

        sitButton.active = !isSitting;

        addRenderableWidget(sitButton);

        Button walkButton = new Button(buttonXRight, buttonY, 50, 20, Component.translatable("gui.wayfinder.button.follow"), button -> isSitting = false, Button.DEFAULT_NARRATION);

        walkButton.active = isSitting;

        addRenderableWidget(walkButton);
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
        guiGraphics.blit(BOOK_TEXTURE, leftPos, bottomPos, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (isSitting != wasSitting)
            PlatformHandler.PLATFORM_HANDLER.sendToServer(new WayfinderSitPacket(isSitting));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) return true;
        return biomeList.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (super.mouseReleased(mouseX, mouseY, button)) return true;
        return biomeList.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (super.mouseDragged(mouseX, mouseY, button, dragX, dragY)) return true;
        return biomeList.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        return biomeList.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public static void openScreen(List<ResourceLocation> biomeRegistry, ResourceLocation current, boolean isSitting) {
        Minecraft.getInstance().setScreen(new WayfinderScreen(biomeRegistry, current, isSitting));
    }

    private void updateBiomeList(String searchText) {
        biomeList.setBiomes(getFilteredBiomes(searchText));
    }

    private List<ResourceLocation> getFilteredBiomes(String searchText) {
        return biomes.stream()
                .filter(biome -> biome.toLanguageKey().toLowerCase().contains(searchText.toLowerCase()))
                .toList();
    }
}
