package net.potionstudios.wayfinder.world.level.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.sounds.WayfinderSounds;
import net.potionstudios.wayfinder.world.entity.block.WayfinderBlockEntityType;
import net.potionstudios.wayfinder.world.entity.block.WayfinderHeartBlockEntity;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

public class WayfinderHeartBlock extends BaseEntityBlock {
    public static final MapCodec<WayfinderHeartBlock> CODEC = simpleCodec(WayfinderHeartBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final TagKey<Item> EMERALD_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "gems/emerald"));

    public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

    public WayfinderHeartBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(ACTIVATED, false).setValue(FACING, Direction.NORTH));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected @NonNull InteractionResult useItemOn(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hitResult) {
        if (level.isClientSide()) return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        if (!state.getValue(ACTIVATED) && stack.is(EMERALD_TAG) && !PlatformHandler.PLATFORM_HANDLER.hasWayfinder(player)) {
            int cost = getCost(player);
            if (stack.getCount() >= cost) {
                level.scheduleTick(pos, this, 20 * Wayfinder.CONFIG.wayfinderHeartBlock.ACTIVATION_COOLDOWN.value());
                level.setBlockAndUpdate(pos, state.setValue(ACTIVATED, true));
                if (!player.isCreative()) stack.shrink(cost);
                level.playSound(null, pos, WayfinderSounds.WAYFINDER_SUMMON.get(), SoundSource.BLOCKS);
                spawnWayfinder(level, pos.above(), (ServerPlayer) player);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.FAIL;
    }

    @Override
    protected void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (state.getValue(ACTIVATED))
            level.setBlockAndUpdate(pos, state.setValue(ACTIVATED, false));
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (state.getValue(ACTIVATED) && random.nextBoolean())
            for (int i = 0; i < random.nextInt(5, 10); i++)
                level.addParticle(
                        ParticleTypes.HAPPY_VILLAGER,
                        pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.8,
                        pos.getY() + 1.3 + (random.nextDouble() - 0.5) * 0.8,
                        pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.8,
                        (random.nextDouble() - 0.5) * 0.2,random.nextDouble() * 0.1,(random.nextDouble() - 0.5) * 0.2
                );
    }

    private static void spawnWayfinder(@NotNull Level level , @NotNull BlockPos pos, @NotNull ServerPlayer player) {
        WayfinderEntity wayfinder = new WayfinderEntity(level, player);
        wayfinder.setPos(pos.getX(), pos.getY() + 1, pos.getZ());
        level.addFreshEntity(wayfinder);
        CriteriaTriggers.SUMMONED_ENTITY.trigger(player, wayfinder);
    }

    private static int getCost(@NotNull Player player) {
        int deaths = PlatformHandler.PLATFORM_HANDLER.getWayfinderDeaths(player);
        if (deaths == 0) return 1;
        else return deaths * Math.abs(Wayfinder.CONFIG.wayfinderHeartBlock.EMERALD_DEATH_COST_MULTIPLIER.value());
    }

    @Override
    public @Nullable BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        builder.add(ACTIVATED).add(FACING);
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return WayfinderBlockEntityType.WAYFINDER_HEART.get().create(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> blockEntityType) {
        return createTickerHelper(level, blockEntityType, WayfinderBlockEntityType.WAYFINDER_HEART.get());
    }

    public static <T extends BlockEntity> BlockEntityTicker<T> createTickerHelper(
            Level level, BlockEntityType<T> serverType, BlockEntityType<? extends WayfinderHeartBlockEntity> clientType
    ) {
        return level.isClientSide() ? null : createTickerHelper(serverType, clientType, WayfinderHeartBlockEntity::serverTick);
    }

    @Override
    protected @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }
}
