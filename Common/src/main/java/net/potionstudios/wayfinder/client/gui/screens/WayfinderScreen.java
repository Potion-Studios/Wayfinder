package net.potionstudios.wayfinder.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.client.gui.components.BiomeList;
import net.potionstudios.wayfinder.client.gui.components.ScrollableTextWidget;
import net.potionstudios.wayfinder.network.packets.WayfinderBiomePacket;
import net.potionstudios.wayfinder.network.packets.WayfinderSitPacket;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class WayfinderScreen extends Screen {

    private static final Identifier BOOK_TEXTURE = Wayfinder.id("textures/gui/book_gui.png");
    private static final Identifier LOGO = Wayfinder.id("textures/gui/wayfinder.png");

    protected static final int IMAGE_WIDTH = 288;
    protected static final int IMAGE_HEIGHT = 208;
    protected int leftPos, bottomPos, rightPos, topPos;

    private final List<Identifier> biomes;
    private boolean isSitting;
    private final boolean wasSitting;
    private BiomeList biomeList;
    private Identifier current;
    private Button submitButton;
    private Button stopButton;
    private Button sitButton;
    private Button walkButton;

    public WayfinderScreen(List<Identifier> biomeRegistry, Identifier current, boolean isSitting) {
        super(Component.empty());
        this.biomes = biomeRegistry;
        this.isSitting = isSitting;
        this.current = current;
        this.wasSitting = isSitting;
    }

    @Override
    protected void init() {
        this.leftPos = (this.width - IMAGE_WIDTH) / 2;
        this.bottomPos = (this.height - IMAGE_HEIGHT) / 2 - 15;
        this.rightPos = this.leftPos + IMAGE_WIDTH;
        this.topPos = this.bottomPos + IMAGE_HEIGHT;

        EditBox searchBox = new EditBox(font, (IMAGE_WIDTH / 2) - 25, 15, Component.translatable("gui.wayfinder.search"));
        searchBox.setPosition(rightPos - (IMAGE_WIDTH / 2) + 10, bottomPos + 10);
        searchBox.setResponder(this::updateBiomeList);
        addRenderableWidget(searchBox);

        ScrollableTextWidget descriptionWidget = new ScrollableTextWidget(leftPos + 10, topPos + 10, 116, 86, Component.translatable("gui.wayfinder.description"), AbstractScrollArea.defaultSettings((int)(9.0 / 2.0)));
        descriptionWidget.setPosition(leftPos + 15, 100 + (bottomPos + (IMAGE_HEIGHT / 4) - (100 / 2))- 23);
        addRenderableWidget(descriptionWidget);

        biomeList = new BiomeList(minecraft, (IMAGE_WIDTH / 2) - 25, IMAGE_HEIGHT - 70, 0, 10);
        biomeList.setPosition(rightPos - (IMAGE_WIDTH / 2) + 10, bottomPos + 28);
        biomeList.setBiomes(biomes);
        addRenderableWidget(biomeList);

        int buttonY = bottomPos + IMAGE_HEIGHT - 40;

        this.submitButton = addRenderableWidget(Button.builder(Component.translatable("gui.wayfinder.button.search"), (button) -> {
            PlatformHandler.PLATFORM_HANDLER.sendToServer(new WayfinderBiomePacket(biomeList.getFocused().getBiome()));
            this.onClose();
        }).pos(rightPos - (IMAGE_WIDTH / 2) + 10, buttonY).size(50, 20).build());

        this.stopButton = addRenderableWidget(Button.builder(Component.translatable("gui.wayfinder.button.stop"), (button) -> {
            PlatformHandler.PLATFORM_HANDLER.sendToServer(new WayfinderBiomePacket(Wayfinder.id("clear_packet")));
            current = Wayfinder.id("clear_packet");
        }).pos(rightPos - 70, buttonY).size(50, 20).build());

        this.sitButton = addRenderableWidget(Button.builder(Component.translatable("gui.wayfinder.button.sit"), button -> isSitting = true)
                .pos(leftPos + 20, buttonY).size(50, 20).build());

        this.walkButton = addRenderableWidget(Button.builder(Component.translatable("gui.wayfinder.button.follow"), button -> isSitting = false)
                .pos(leftPos + (IMAGE_WIDTH / 2) - 60, buttonY).size(50, 20).build());
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractRenderState(graphics, mouseX, mouseY, a);

        submitButton.active = biomeList.getFocused() != null;
        stopButton.active = !current.equals(Wayfinder.id("clear_packet"));
        sitButton.active = !isSitting;
        walkButton.active = isSitting;

        int logoSize = 100;
        int centerX = leftPos + (IMAGE_WIDTH / 4) - (logoSize / 2);
        int centerY = bottomPos + (IMAGE_HEIGHT / 4) - (logoSize / 2);

        graphics.blit(RenderPipelines.GUI_TEXTURED, LOGO, centerX, centerY, 0, 0, logoSize, logoSize, logoSize, logoSize);
    }

    @Override
    public void extractBackground(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        this.extractTransparentBackground(graphics);
        graphics.blit(RenderPipelines.GUI_TEXTURED, BOOK_TEXTURE, leftPos, bottomPos, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
    }

    @Override
    public void onClose() {
        super.onClose();
        if (isSitting != wasSitting)
            PlatformHandler.PLATFORM_HANDLER.sendToServer(new WayfinderSitPacket(isSitting));
    }

    @Override
    public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean isDoubleClick) {
        if (super.mouseClicked(event, isDoubleClick)) return true;
        return biomeList.mouseClicked(event, isDoubleClick);
    }

    @Override
    public boolean mouseReleased(@NonNull MouseButtonEvent event) {
        if (super.mouseReleased(event)) return true;
        return biomeList.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(@NonNull MouseButtonEvent event, double mouseX, double mouseY) {
        if (super.mouseDragged(event, mouseX, mouseY)) return true;
        return biomeList.mouseDragged(event, mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (super.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) return true;
        return biomeList.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    public static void openScreen(List<Identifier> biomeRegistry, Identifier current, boolean isSitting) {
        Minecraft.getInstance().setScreen(new WayfinderScreen(biomeRegistry, current, isSitting));
    }

    private void updateBiomeList(String searchText) {
        biomeList.setBiomes(getFilteredBiomes(searchText));
    }

    private List<Identifier> getFilteredBiomes(String searchText) {
        return biomes.stream()
                .filter(biome -> biome.toLanguageKey().toLowerCase().contains(searchText.toLowerCase()))
                .toList();
    }
}
