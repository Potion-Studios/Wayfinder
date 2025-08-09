package net.potionstudios.wayfinder.world.entity.ai.control;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
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
        double groundY = findGroundY();
        double minY = groundY + 0.5;
        double maxY = groundY + 2.0;

        if (wayfinder.isSitting()) {
            if (wayfinder.isNoGravity()) wayfinder.setNoGravity(false);
            if (wayfinder.onGround()) wayfinder.setDeltaMovement(0, 0, 0);
            return;
        }

        boolean shouldFloatNearOwner = wayfinder.getOwner() != null
                && wayfinder.getOwner().distanceToSqr(wayfinder) < 10
                && wayfinder.getTargetBiomeBlockPos().isEmpty()
                && !wayfinder.isScared();

        if (shouldFloatNearOwner) {
            if (!wayfinder.isNoGravity()) wayfinder.setNoGravity(true);

            double bobOffset = Math.sin(mob.tickCount * 0.1 + phaseOffset) * 0.05;
            double targetY = Mth.clamp(wayfinder.getOwner().getY() + bobOffset, minY, maxY);

            double dy = targetY - wayfinder.getY();
            wayfinder.setDeltaMovement(
                    wayfinder.getDeltaMovement().x,
                    dy * 0.3,
                    wayfinder.getDeltaMovement().z
            );
        }
        else if (this.operation == Operation.MOVE_TO) {
            // Clamp MOVE_TO vertical target
            this.wantedY = Mth.clamp(this.wantedY, minY, maxY);
            super.tick();
            if (!wayfinder.isNoGravity()) wayfinder.setNoGravity(true);
        }
        else {
            this.mob.setYya(0.0F);
            this.mob.setZza(0.0F);
        }

        // Absolute safety net: clamp final Y position
        if (wayfinder.getY() > maxY) {
            wayfinder.setPos(wayfinder.getX(), maxY, wayfinder.getZ());
        }
    }

    private double findGroundY() {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos().set(
                wayfinder.getX(),
                wayfinder.getY(),
                wayfinder.getZ()
        );

        // Scan downward until we find a solid block or reach world bottom
        while (pos.getY() > wayfinder.level().getMinBuildHeight()) {
            if (!wayfinder.level().getBlockState(pos).isAir()) {
                return pos.getY() + 1; // stand just above this block
            }
            pos.move(0, -1, 0);
        }

        // Fallback if nothing found (void situation)
        return wayfinder.level().getMinBuildHeight();
    }


}
