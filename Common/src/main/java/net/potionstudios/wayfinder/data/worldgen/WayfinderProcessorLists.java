package net.potionstudios.wayfinder.data.worldgen;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CandleBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.*;
import net.potionstudios.wayfinder.Wayfinder;

import java.util.Map;

public class WayfinderProcessorLists {

	public static final Map<ResourceKey<StructureProcessorList>, StructureProcessorListFactory> PROCESSOR_LIST_FACTORIES = new Reference2ObjectOpenHashMap<>();

	public static final ResourceKey<StructureProcessorList> DESERT_SHRINE = register("desert_shrine", structureProcessorListHolderGetter -> new StructureProcessorList(
			ImmutableList.of(
					createRuleProcessor(
							createAlwaysTrueRandomBlockMatchTest(Blocks.SANDSTONE, 0.5f, Blocks.SAND),
							createAlwaysTrueRandomBlockMatchTest(Blocks.SANDSTONE, 0.25f, Blocks.SUSPICIOUS_SAND),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 2)),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 3)),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 4)),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.LIT, true)),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 2).setValue(CandleBlock.LIT, true)),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 3).setValue(CandleBlock.LIT, true)),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 4).setValue(CandleBlock.LIT, true))
					)
			)
	));

	public static final ResourceKey<StructureProcessorList> SNOWY_SHRINE = register("snowy_shrine", structureProcessorListHolderGetter -> new StructureProcessorList(
		ImmutableList.of(
				createRuleProcessor(
						createAlwaysTrueRandomBlockMatchTest(Blocks.SNOW_BLOCK, 0.15f, Blocks.PACKED_ICE),
						createAlwaysTrueRandomBlockMatchTest(Blocks.SNOW_BLOCK, 0.15f, Blocks.BLUE_ICE),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 2)),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 3)),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 4)),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.LIT, true)),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 2).setValue(CandleBlock.LIT, true)),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 3).setValue(CandleBlock.LIT, true)),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 4).setValue(CandleBlock.LIT, true))
				)
		)
	));

	public static final ResourceKey<StructureProcessorList> TAIGA_SHRINE = register("taiga_shrine", structureProcessorListHolderGetter -> new StructureProcessorList(
		ImmutableList.of(
				createRuleProcessor(
						createAlwaysTrueRandomBlockMatchTest(Blocks.PODZOL, 0.15f, Blocks.MOSS_BLOCK),
						createAlwaysTrueRandomBlockMatchTest(Blocks.PODZOL, 0.15f, Blocks.GRASS_BLOCK),
						createAlwaysTrueRandomBlockMatchTest(Blocks.MOSSY_COBBLESTONE, 0.1f, Blocks.CRACKED_STONE_BRICKS),
						createAlwaysTrueRandomBlockMatchTest(Blocks.MOSSY_COBBLESTONE, 0.3f, Blocks.MOSSY_STONE_BRICKS),
						createAlwaysTrueRandomBlockMatchTest(Blocks.GRASS_BLOCK, 0.15f, Blocks.MOSS_BLOCK),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 2)),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 3)),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 4)),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.LIT, true)),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 2).setValue(CandleBlock.LIT, true)),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 3).setValue(CandleBlock.LIT, true)),
						createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 4).setValue(CandleBlock.LIT, true))
				)
		)
	));

	public static final ResourceKey<StructureProcessorList> PLAINS_SHRINE = register("plains_shrine", structureProcessorListHolderGetter -> new StructureProcessorList(
			ImmutableList.of(
					createRuleProcessor(
							createAlwaysTrueRandomBlockMatchTest(Blocks.MOSSY_COBBLESTONE, 0.1f, Blocks.CRACKED_STONE_BRICKS),
							createAlwaysTrueRandomBlockMatchTest(Blocks.MOSSY_COBBLESTONE, 0.3f, Blocks.MOSSY_STONE_BRICKS),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 2)),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 3)),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 4)),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.LIT, true)),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 2).setValue(CandleBlock.LIT, true)),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 3).setValue(CandleBlock.LIT, true)),
							createAlwaysTrueRandomBlockMatchTest(Blocks.CANDLE, 0.2f, Blocks.CANDLE.defaultBlockState().setValue(CandleBlock.CANDLES, 4).setValue(CandleBlock.LIT, true))
							)
			)
	));

	private static ProcessorRule createAlwaysTrueRandomBlockMatchTest(Block start, float chance, Block newBlock) {
		return createProcessorRule(createRandomBlockMatchTest(start, chance), AlwaysTrueTest.INSTANCE, newBlock.defaultBlockState());
	}

	private static ProcessorRule createAlwaysTrueRandomBlockMatchTest(Block start, float chance, BlockState newBlock) {
		return createProcessorRule(createRandomBlockMatchTest(start, chance), AlwaysTrueTest.INSTANCE, newBlock);
	}

	private static RandomBlockMatchTest createRandomBlockMatchTest(Block block, float chance) {
		return new RandomBlockMatchTest(block, chance);
	}

	private static ProcessorRule createProcessorRule(RandomBlockMatchTest test, AlwaysTrueTest alwaysTrueTest, net.minecraft.world.level.block.state.BlockState blockState) {
		return new ProcessorRule(test, alwaysTrueTest, blockState);
	}

	private static RuleProcessor createRuleProcessor(ProcessorRule... rules) {
		return new RuleProcessor(ImmutableList.copyOf(rules));
	}

	private static ResourceKey<StructureProcessorList> register(String id, StructureProcessorListFactory factory) {
		ResourceKey<StructureProcessorList> structureProcessorListResourceKey = Wayfinder.key(Registries.PROCESSOR_LIST, id);
		PROCESSOR_LIST_FACTORIES.put(structureProcessorListResourceKey, factory);
		return structureProcessorListResourceKey;
	}

	@FunctionalInterface
	public interface StructureProcessorListFactory  {
		StructureProcessorList generate(HolderGetter<StructureProcessorList> structureProcessorListHolderGetter);
	}
}
