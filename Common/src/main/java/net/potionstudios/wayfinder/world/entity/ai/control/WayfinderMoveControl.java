package net.potionstudios.wayfinder.world.entity.ai.control;

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
            return;
        } else if (wayfinder.getOwner() != null && wayfinder.getOwner().distanceToSqr(wayfinder) < 10 && wayfinder.gettargetBiomeBlockPos().isEmpty() && !wayfinder.isScared()) {
            //wayfinder.setNoGravity(true);
            double currentY = wayfinder.getY();
            double ownerY = wayfinder.getOwner().getY();
            // Calculate a smooth floating offset using sine wave
            double floatOffset = Math.sin(mob.tickCount * 0.1 + phaseOffset) * 0.05;

            // Custom Logic from before
            double targetY = Math.max(ownerY, Math.min(currentY + floatOffset, ownerY + 2.0));

            // Adjust Y position while maintaining fluidity
            setWantedPosition(mob.getX(), targetY, mob.getZ(), this.speedModifier);
            operation = Operation.MOVE_TO;
        } else if (wayfinder.onGround()) {
          setWantedPosition(mob.getX(), mob.getY() + 0.1, mob.getZ(), this.speedModifier);
        }
        super.tick();
    }
}
