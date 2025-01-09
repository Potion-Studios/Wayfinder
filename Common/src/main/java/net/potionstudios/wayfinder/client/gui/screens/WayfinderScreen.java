package net.potionstudios.wayfinder.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class WayfinderScreen extends Screen {

    private static final ResourceLocation BOOK_TEXTURE = Wayfinder.id("textures/gui/book_gui.png");
    private static final int ITEMS_PER_PAGE = 10;

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
    private final Map<ResourceLocation, Biome> biomes = new HashMap<>();
    private int currentPage = 0;
    private final WayfinderEntity wayfinder;

    public WayfinderScreen(WayfinderEntity wayfinder, Registry<Biome> biomeRegistry) {
        super(Component.literal(""));
        this.wayfinder = wayfinder;
        for (ResourceLocation biome : biomeRegistry.keySet())
            this.biomes.put(biome, biomeRegistry.get(biome));
    }

    @Override
    protected void init() {
        this.leftPos = ((this.width - IMAGE_WIDTH) / 2);
        this.bottomPos = (this.height - IMAGE_HEIGHT) / 2 - 15;
        this.rightPos = this.leftPos + IMAGE_WIDTH;
        this.topPos = this.bottomPos + IMAGE_HEIGHT;
        this.startXRightPage = (this.leftPos + (IMAGE_WIDTH / 4) + ((IMAGE_WIDTH) / 3)) - 18;
        this.startXLeftPage = this.leftPos + 15;
        this.pageBackButtonX = this.leftPos + 15;
        this.pageButtonY = this.topPos - 13 - 13;
        this.pageButtonForwardX = this.rightPos - 23 - 22;
        updateBiomeButtons();
    }

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (this.minecraft == null)
            return;
        else if (this.minecraft.level == null)
            this.renderPanorama(guiGraphics, partialTick);

        RenderSystem.setShaderTexture(0, BOOK_TEXTURE);
        guiGraphics.blit(BOOK_TEXTURE, leftPos, bottomPos, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
        this.renderMenuBackground(guiGraphics);
    }

    private void updateBiomeButtons() {
        this.clearWidgets();
        int start = currentPage * ITEMS_PER_PAGE;
        int end = Math.min(start + ITEMS_PER_PAGE, biomes.size());

        for (int i = start; i < end; i++) {
            ResourceLocation biome = (ResourceLocation) biomes.keySet().toArray()[i];
            int buttonX = (i % ITEMS_PER_PAGE) % 2 == 0 ? startXLeftPage : startXRightPage;
            int buttonY = bottomPos + (i % ITEMS_PER_PAGE) / 2 * 20 + 20;
            this.addRenderableWidget(new Button(buttonX, buttonY, 100, 20, Component.translatable("biome." + biome.toLanguageKey()), button -> {
                Wayfinder.LOGGER.info("Teleporting to biome: " + biome);
            }, supplier -> null));
        }

        if (currentPage != 0)
            this.addRenderableWidget(new PageButton(pageBackButtonX, pageButtonY, false, button -> {
                if (currentPage > 0) {
                    currentPage--;
                    updateBiomeButtons();
                }
            }, true));

        if (currentPage < (biomes.size() / ITEMS_PER_PAGE) - 1)
            this.addRenderableWidget(new PageButton(pageButtonForwardX, pageButtonY, true, button -> {
                if (currentPage < (biomes.size() / ITEMS_PER_PAGE) - 1) {
                    currentPage++;
                    updateBiomeButtons();
                }
            }, true));
    }

    public static void openScreen(WayfinderEntity wayfinder, Registry<Biome> biomeRegistry) {
        Minecraft.getInstance().setScreen(new WayfinderScreen(wayfinder, biomeRegistry));
    }
}
