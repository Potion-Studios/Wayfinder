package net.potionstudios.wayfinder.client.gui.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

class BiomeList extends ContainerObjectSelectionList<BiomeList.Entry> {
	BiomeList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
		super(minecraft, width, height, y, itemHeight);
	}

	public void setBiomes(@NotNull List<ResourceLocation> biomes) {
		clearEntries();
		biomes.sort(Comparator.comparing(ResourceLocation::toLanguageKey));
		for (ResourceLocation biome : biomes)
			addEntry(new Entry(biome));
	}

	@Override
	protected void renderListBackground(@NotNull GuiGraphics guiGraphics) {}


	@Override
	protected int getScrollbarPosition() {
		return getX() + getWidth();
	}

	class Entry extends ContainerObjectSelectionList.Entry<Entry> {
		private final String biomeName;
		private final ResourceLocation biome;

		public Entry(@NotNull ResourceLocation location) {
			this.biome = location;
			this.biomeName = Component.translatable("biome." + location.toLanguageKey()).getString();
		}

		@Override
		public void render(@NotNull GuiGraphics guiGraphics, int index, int y, int x, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTicks) {
			boolean b = hovered || getSelected() == getEntry(index);
			if (getSelected() == getEntry(index))
				guiGraphics.fill(x, y - 3, x + width, y + height + 3, 0x80404040);
			guiGraphics.drawString(minecraft.font, biomeName, x + 50, y, b ? 0xFFFFA0 : 0, b);
		}

		@Override
		public @NotNull List<? extends GuiEventListener> children() {
			return List.of();
		}

		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (getSelected() == this)
				setSelected(null);
			else setSelected(this);
			return true;
		}

		@Override
		public @NotNull List<? extends NarratableEntry> narratables() {
			return List.of();
		}

		public ResourceLocation getBiome() {
			return biome;
		}
	}
}
