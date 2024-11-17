package net.potionstudios.wayfinder.world.entity.wayfinder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.potionstudios.wayfinder.sounds.WayfinderSounds;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class WayfinderEntity extends PathfinderMob implements GeoEntity, OwnableEntity {

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation DEATH = RawAnimation.begin().thenPlay("death");
    private static final RawAnimation SEARCHING_START = RawAnimation.begin().thenPlay("searching_start");
    private static final RawAnimation SEARCHING_END = RawAnimation.begin().thenPlay("searching_end");
    private static final RawAnimation SEARCHING_LOOP = RawAnimation.begin().thenLoop("searching_loop");
    private static final RawAnimation NO = RawAnimation.begin().thenPlay("no");

    //private static final EntityDataAccessor<Optional<BlockPos>> BLOCK_POS = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    public WayfinderEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (getOwnerUUID() != null)
            compound.putUUID("Owner", getOwnerUUID());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        UUID uuid;
        if (compound.hasUUID("Owner"))
            uuid = compound.getUUID("Owner");
        else {
            String s = compound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(this.getServer(), s);
        }

        if (uuid != null) setOwnerUUID(uuid);
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate));
    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        if (isDeadOrDying())
            return event.setAndContinue(DEATH);
        return event.setAndContinue(IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        return super.mobInteract(player, hand);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 4.0D).add(Attributes.MOVEMENT_SPEED, 0.4D);
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return WayfinderSounds.WAYFINDER_DEATH.get();
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        return switch (level().getRandom().nextInt(6)) {
            case 0 -> WayfinderSounds.WAYFINDER_IDLE0.get();
            case 1 -> WayfinderSounds.WAYFINDER_IDLE1.get();
            case 2 -> WayfinderSounds.WAYFINDER_IDLE2.get();
            case 3 -> WayfinderSounds.WAYFINDER_IDLE3.get();
            case 4 -> WayfinderSounds.WAYFINDER_IDLE4.get();
            default -> WayfinderSounds.WAYFINDER_IDLE5.get();
        };
    }
}
