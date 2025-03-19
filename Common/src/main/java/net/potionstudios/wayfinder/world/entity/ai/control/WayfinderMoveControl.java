package net.potionstudios.wayfinder.world.entity.ai.control;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

public class WayfinderMoveControl extends FlyingMoveControl {

    private final WayfinderEntity wayfinder;
    private final double phaseOffset;

    public WayfinderMoveControl(WayfinderEntity wayfinder, double phaseOffset) {
        super(wayfinder, 30, true);
        this.wayfinder = wayfinder;
        this.phaseOffset = phaseOffset;
    }

    @Override
    public void tick() {
        if (wayfinder.isSitting()) { // When sitting the wayfinder should not fly
            wayfinder.setNoGravity(false);
        } else if (wayfinder.getOwner() != null && wayfinder.getOwner().distanceToSqr(wayfinder) < 10 && wayfinder.getTargetBiomeBlockPos().isEmpty() && !wayfinder.isScared()) {
            double currentY = wayfinder.getY();
            double ownerY = wayfinder.getOwner().getY();

            if (currentY - ownerY > 2) {
                wayfinder.setNoGravity(false);
                wayfinder.applyGravity();
                return;
            }

            wayfinder.setNoGravity(true);

            // Calculate a smooth floating offset using sine wave
            double floatOffset = Math.sin(mob.tickCount * 0.1 + phaseOffset) * 0.05;

            // Custom Logic from before
            double targetY = Math.max(ownerY, Math.min(currentY + floatOffset, ownerY + 2.0));

            if (wayfinder.level().getBlockState(new BlockPos((int) mob.getX(), (int) targetY, (int) mob.getZ())).isAir())
                wayfinder.setPos(mob.getX(), targetY, mob.getZ());
        } else super.tick();
    }
}
