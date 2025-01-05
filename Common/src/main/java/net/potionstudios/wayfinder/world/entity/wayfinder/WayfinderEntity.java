package net.potionstudios.wayfinder.world.entity.wayfinder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.client.gui.screens.WayfinderScreen;
import net.potionstudios.wayfinder.sounds.WayfinderSounds;
import net.potionstudios.wayfinder.world.entity.WayfinderEntities;
import net.potionstudios.wayfinder.world.entity.ai.control.WayfinderMoveControl;
import net.potionstudios.wayfinder.world.entity.ai.goal.FollowOwnerGoal;
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
    private static final RawAnimation DEATH = RawAnimation.begin().then("death", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation SEARCHING_START = RawAnimation.begin().thenPlay("searching_start");
    private static final RawAnimation SEARCHING_END = RawAnimation.begin().thenPlay("searching_end");
    private static final RawAnimation SEARCHING_LOOP = RawAnimation.begin().thenLoop("searching_loop");
    private static final RawAnimation NO = RawAnimation.begin().thenPlay("no");
    private static final RawAnimation SIT = RawAnimation.begin().thenPlay("sit");
    private static final RawAnimation SIT_IDLE = RawAnimation.begin().thenLoop("sit_idle");
    private static final RawAnimation SCARED = RawAnimation.begin().thenLoop("scared");

    //private static final EntityDataAccessor<Optional<BlockPos>> BLOCK_POS = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> DATA_SCARED = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.BOOLEAN);

    private boolean sitting;
    private float phaseOffset;
    private boolean searching;
    private SHIELD shield;

    public WayfinderEntity(Level level, Player owner, double x, double y, double z) {
        this(WayfinderEntities.WAYFINDER.get(), level);
        setPos(x, y, z);
        setOwner(owner);
    }

    public WayfinderEntity(EntityType<? extends WayfinderEntity> entityType, Level level) {
        super(entityType, level);
        phaseOffset = random.nextFloat() * (float) (2 * Math.PI);
        moveControl = new WayfinderMoveControl(this, phaseOffset);
        searching = false;
        setPersistenceRequired();
        shield = SHIELD.FULL;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
        builder.define(DATA_SCARED, false);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (getOwnerUUID() != null)
            compound.putUUID("Owner", getOwnerUUID());
        compound.putBoolean("Sitting", sitting);
        compound.putFloat("Offset", phaseOffset);
        compound.putBoolean("Searching", searching);
        compound.putString("shield", shield.getSerializedName());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        UUID uuid;
        if (compound.hasUUID("Owner"))
            uuid = compound.getUUID("Owner");
        else {
            String s = compound.getString("Owner");
            uuid = OldUsersConverter.convertMobOwnerIfNecessary(getServer(), s);
        }

        if (uuid != null) setOwnerUUID(uuid);

        sitting = compound.getBoolean("Sitting");
        phaseOffset = compound.getFloat("Offset");
        searching = compound.getBoolean("Searching");
        shield = SHIELD.byName(compound.getString("shield"));
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwner(@NotNull Player player) {
        setOwnerUUID(player.getUUID());
        PlatformHandler.PLATFORM_HANDLER.setWayfinder(player, true);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(uuid));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate).triggerableAnim("no", NO).triggerableAnim("sit", SIT));
    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        if (isDeadOrDying())
            return event.setAndContinue(DEATH);
        else if (isScared())
            return event.setAndContinue(SCARED);
        else if (isSitting())
            return event.setAndContinue(SIT_IDLE);
        return event.setAndContinue(IDLE);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!level().isClientSide() && getOwner() == null)
            setOwner(player);

        if (level().isClientSide()){
            if (player.getUUID().equals(getOwnerUUID())) {
                WayfinderScreen.openScreen();
                return InteractionResult.SUCCESS;
            } else triggerAnim("controller", "no");
            return InteractionResult.FAIL;
        }

        //else if (player.getUUID().equals(getOwnerUUID()))
            //Minecraft.getInstance().setScreen(new WayfinderScreen());
        //else triggerAnim("controller", "no");
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    public boolean isSitting() {
        return this.sitting;
    }

    public void setSitting(boolean sitting) {
        this.sitting = sitting;
    }

    public boolean isSearching() {
        return searching;
    }

    public void setSearching(boolean searching) {
        this.searching = searching;
    }

    public boolean isScared() {
        return entityData.get(DATA_SCARED);
    }

    public void setScared(boolean scared) {
        entityData.set(DATA_SCARED, scared);
    }

    public SHIELD shield() {
        return shield;
    }

    public void setShield(SHIELD shield) {
        this.shield = shield;
    }

    public boolean hasShield() {
        return this.shield() != SHIELD.NONE;
    }

    public final boolean unableToMoveToOwner() {
        return isSitting() || isPassenger() || this.getOwner() != null && getOwner().isSpectator() || isSearching();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.MOVEMENT_SPEED, 0.4D)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER, 0)
                .add(Attributes.GRAVITY, 0.02f);
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return WayfinderSounds.WAYFINDER_DEATH.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
	    return getRandom().nextBoolean() ? WayfinderSounds.WAYFINDER_HURT0.get() : WayfinderSounds.WAYFINDER_HURT1.get();
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        if (getRandom().nextBoolean())
            return SoundEvents.EMPTY;
        return switch (getRandom().nextInt(6)) {
            case 0 -> WayfinderSounds.WAYFINDER_IDLE0.get();
            case 1 -> WayfinderSounds.WAYFINDER_IDLE1.get();
            case 2 -> WayfinderSounds.WAYFINDER_IDLE2.get();
            case 3 -> WayfinderSounds.WAYFINDER_IDLE3.get();
            case 4 -> WayfinderSounds.WAYFINDER_IDLE4.get();
            default -> WayfinderSounds.WAYFINDER_IDLE5.get();
        };
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FollowMobGoal(this,1.0D, 10.0F, 2.0F));
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Monster.class, 8.0F, 1, 1));
    }

    @Override
    public boolean onGround() {
        return getY() - level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockPosition()).getY() <= 2;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (isScared())
            if (this.shield() == SHIELD.FULL) {
                this.playSound(WayfinderSounds.WAYFINDER_SHIELD_HIT.get());
                this.setShield(SHIELD.HALF);
                return false;
            } else if (this.shield() == SHIELD.HALF) {
                this.playSound(WayfinderSounds.WAYFINDER_SHIELD_BREAK.get());
                this.setShield(SHIELD.NONE);
                return false;
        }
        boolean hurt = super.hurt(source, amount);
        if (hurt && isDeadOrDying() && getOwner() != null)
            PlatformHandler.PLATFORM_HANDLER.setWayfinder((Player) getOwner(), false);

        if (hurt) setScared(true);

        return hurt;
    }

    @Override
    protected void tickDeath() {
        setDeltaMovement(getDeltaMovement().add(0, -0.04, 0));
        super.tickDeath();
    }

    public enum SHIELD implements StringRepresentable {
        FULL("full"),
        HALF("half"),
        NONE("none");

        private final String name;

        @Override
        public @NotNull String getSerializedName() {
            return name;
        }

        public static SHIELD byName(String name) {
            for (SHIELD shield : values())
                if (shield.getSerializedName().equals(name))
                    return shield;
            return NONE;
        }

        SHIELD(String name) {
            this.name = name;
        }
    }
}
