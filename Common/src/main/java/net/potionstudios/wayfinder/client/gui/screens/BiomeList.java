package net.potionstudios.wayfinder.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

class BiomeList extends ContainerObjectSelectionList<BiomeList.Entry> {
	BiomeList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
		super(minecraft, width, height, y, itemHeight);
	}

	@Override
	public int getRowWidth() {
		return this.width;
	}

	public void setBiomes(@NotNull List<Identifier> biomes) {
		clearEntries();
		if (biomes.isEmpty()) return;
		biomes.stream().sorted().forEach(biome -> addEntry(new Entry(biome)));
	}

	@Override
	protected void renderListBackground(@NotNull GuiGraphics guiGraphics) {}


	class Entry extends ContainerObjectSelectionList.Entry<Entry> {
		private final String biomeName;
		private final Identifier biome;

		public Entry(@NotNull Identifier location) {
			this.biome = location;
			this.biomeName = Component.translatable("biome." + location.toLanguageKey()).getString();
		}

		@Override
		public void renderContent(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
			boolean b = isHovering || getSelected() == this;
			if (getSelected() == this)
				guiGraphics.fill(RenderPipelines.GUI, getX(), getY() - 1, getX() + width, getY() + getHeight() + 1, 0x80404040);
			guiGraphics.drawString(minecraft.font, biomeName, getX() + 5, getY(), b ? 0xFFFFFFA0 : 0xFF000000, false);
		}

		@Override
		public @NotNull List<? extends GuiEventListener> children() {
			return List.of();
		}

		@Override
		public boolean mouseClicked(@NonNull MouseButtonEvent event, boolean isDoubleClick) {
			if (getSelected() == this)
				setSelected(null);
			else setSelected(this);
			return true;
		}

		@Override
		public @NotNull List<? extends NarratableEntry> narratables() {
			return List.of();
		}

		public Identifier getBiome() {
			return biome;
		}
	}
}
