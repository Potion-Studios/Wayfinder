package net.potionstudios.wayfinder.world.entity.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.potionstudios.wayfinder.advancements.critereon.WayfinderCriteriaTriggers;

public class WayfinderHeartBlockEntity extends BlockEntity {
	public WayfinderHeartBlockEntity(BlockPos pos, BlockState blockState) {
		super(WayfinderBlockEntityType.WAYFINDER_HEART.get(), pos, blockState);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, WayfinderHeartBlockEntity blockEntity) {
		ServerLevel serverLevel = (ServerLevel) level;
		if (serverLevel.getServer().getTickCount() % 20 == 0) {
			AABB aabb = new AABB(pos).inflate(5);
			serverLevel.getEntitiesOfClass(ServerPlayer.class, aabb).forEach(serverPlayer -> {
				if (!serverPlayer.isSpectator())
					WayfinderCriteriaTriggers.WAYFINDER_HEART_BLOCK.get().trigger(serverPlayer);
			});
		}
	}
}
