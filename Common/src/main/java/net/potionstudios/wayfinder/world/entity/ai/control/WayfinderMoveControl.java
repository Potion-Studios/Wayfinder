package net.potionstudios.wayfinder.world.entity.ai.control;

import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.potionstudios.wayfinder.world.entity.wayfinder.WayfinderEntity;

public class WayfinderMoveControl extends FlyingMoveControl {

    private final WayfinderEntity wayfinder;

    public WayfinderMoveControl(WayfinderEntity wayfinder) {
        super(wayfinder, 20, true);
        this.wayfinder = wayfinder;
    }


    @Override
    public void tick() {
        if (wayfinder.isResting() || wayfinder.isPanic()) {
            if (wayfinder.isNoGravity()) wayfinder.setNoGravity(false);
            if (wayfinder.onGround()) wayfinder.setDeltaMovement(0, 0, 0);
        } else {
            wayfinder.setNoGravity(true);
            super.tick();
        }
    }
}
