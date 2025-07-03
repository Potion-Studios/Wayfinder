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
        if (wayfinder.isSitting()) {
            wayfinder.setNoGravity(false);
            if (wayfinder.onGround()) wayfinder.setDeltaMovement(0, 0, 0);
        } else if (wayfinder.getOwner() != null && wayfinder.getOwner().distanceToSqr(wayfinder) < 10 && wayfinder.getTargetBiomeBlockPos().isEmpty() && !wayfinder.isScared()) {
            double currentY = wayfinder.getY();
            double ownerY = wayfinder.getOwner().getY();

            if (currentY - ownerY > 3) {
                wayfinder.setNoGravity(false);
                wayfinder.applyGravity();
                return;
            }

            wayfinder.setNoGravity(true);

            double floatOffset = Math.sin(mob.tickCount * 0.1 + phaseOffset) * 0.05;

            double targetY = Math.max(ownerY, Math.min(currentY + floatOffset, ownerY + 2.0));

            if (wayfinder.level().getBlockState(new BlockPos((int) mob.getX(), (int) targetY, (int) mob.getZ())).isAir())
                wayfinder.setPos(mob.getX(), targetY, mob.getZ());
        } else {
            if (this.operation == Operation.MOVE_TO) {
                this.operation = Operation.WAIT;
                this.mob.setNoGravity(true);
                double d = this.wantedX - this.mob.getX();
                double e = this.wantedY - this.mob.getY();
                double f = this.wantedZ - this.mob.getZ();
                double g = d * d + e * e + f * f;
                if (g < (double)2.5000003E-7F) {
                    this.mob.setYya(0.0F);
                    this.mob.setZza(0.0F);
                    return;
                }

                float h = (float)(Mth.atan2(f, d) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), h, 90.0F));
                float i = (float)(this.speedModifier * 0.5);
                this.mob.setSpeed(i);
                double j = Math.sqrt(d * d + f * f);
                if (Math.abs(e) > (double)1.0E-5F || Math.abs(j) > (double)1.0E-5F) {
                    float k = (float)(-(Mth.atan2(e, j) * (double)(180F / (float)Math.PI)));
                    this.mob.setXRot(this.rotlerp(this.mob.getXRot(), k, 30));
                    this.mob.setYya(e > (double)0.0F ? i : -i);
                }
            } else {
                this.mob.setYya(0.0F);
                this.mob.setZza(0.0F);
            }
        }
    }
}
