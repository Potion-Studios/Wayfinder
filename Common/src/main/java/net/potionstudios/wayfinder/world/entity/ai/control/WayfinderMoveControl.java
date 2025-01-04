package net.potionstudios.wayfinder.world.entity.ai.control;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.level.levelgen.Heightmap;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

public class WayfinderMoveControl extends MoveControl {

    private final double phaseOffset;

    public WayfinderMoveControl(Mob mob, double phaseOffset) {
        super(mob);
        this.phaseOffset = phaseOffset;
    }

    @Override
    public void tick() {
        WayfinderEntity mob = (WayfinderEntity) this.mob;
        double groundY = mob.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, mob.blockPosition()).getY();
        double currentY = mob.getY();
        if (!mob.isSitting() && (currentY - groundY) <= 2) {
            mob.setNoGravity(true);
            // Calculate a smooth floating offset using sine wave
            double floatOffset = Math.sin(mob.tickCount * 0.1 + phaseOffset) * 0.05;

            // Keep the ghost between 0.5 and 2 blocks above ground
            double targetY = Math.max(groundY + 0.5, Math.min(currentY + floatOffset, groundY + 2.0));

            // Adjust Y position while maintaining fluidity
            mob.setPos(mob.getX(), targetY, mob.getZ());
        } else {
            mob.setNoGravity(false);
            mob.applyGravity();
        }
        super.tick();
    }
}
