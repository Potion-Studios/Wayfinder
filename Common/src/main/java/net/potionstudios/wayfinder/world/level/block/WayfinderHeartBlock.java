package net.potionstudios.wayfinder.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WayfinderHeartBlock extends HorizontalDirectionalBlock {

    private static final TagKey<Item> EMERALD_TAG = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath("c", "gems/emerald"));

    public static final MapCodec<WayfinderHeartBlock> CODEC = simpleCodec(WayfinderHeartBlock::new);
    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public WayfinderHeartBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVATED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull ItemInteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (!state.getValue(ACTIVATED) && stack.is(EMERALD_TAG)) {
            int cost = getCost(level.getDifficulty());
            if (stack.getCount() >= cost) {
                level.setBlockAndUpdate(pos, state.setValue(ACTIVATED, true));
                if (level.isClientSide()) {
                    RandomSource random = level.getRandom();
                    for (int i = 0; i < random.nextInt(10, 15); i++)
                        level.addParticle(
                                ParticleTypes.HAPPY_VILLAGER,
                                pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 1.5,
                                pos.getY() + 1.0 + (random.nextDouble() - 0.5) * 1.5,
                                pos.getZ() + 0.5 + random.nextDouble() * 0.5,
                                (random.nextDouble() - 0.5) * 0.2, random.nextDouble() * -0.1, (random.nextDouble() - 0.5) * 0.2
                        );
                } else {
                    if (!player.isCreative()) stack.shrink(cost);
                    level.playSound(null, pos, SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.BLOCKS);
                    spawnWayfinder(level, pos.relative(state.getValue(FACING)), player);
                }
                return ItemInteractionResult.SUCCESS;
            }
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    private static void spawnWayfinder(@NotNull Level level ,@NotNull BlockPos pos, @NotNull Player player) {
        level.addFreshEntity(new WayfinderEntity(level, player, pos.getX(), pos.getY(), pos.getZ()));
    }

    private static int getCost(@NotNull Difficulty difficulty) {
        return switch (difficulty) {
            case EASY -> 1;
            case NORMAL -> 2;
            case HARD -> 3;
            default -> 0;
        } * Math.abs(Wayfinder.CONFIG.EMERALD_COST_MULTIPLIER);
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED).add(FACING);
    }
}
