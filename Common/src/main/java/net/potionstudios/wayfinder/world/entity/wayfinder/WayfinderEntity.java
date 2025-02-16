package net.potionstudios.wayfinder.world.entity.wayfinder;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FollowMobGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.advancements.critereon.WayfinderCriteriaTriggers;
import net.potionstudios.wayfinder.client.gui.screens.WayfinderScreen;
import net.potionstudios.wayfinder.network.protocol.WayfinderOpenScreenPacket;
import net.potionstudios.wayfinder.sounds.WayfinderSounds;
import net.potionstudios.wayfinder.world.entity.WayfinderEntities;
import net.potionstudios.wayfinder.world.entity.ai.control.WayfinderMoveControl;
import net.potionstudios.wayfinder.world.entity.ai.goal.GoToBiomeGoal;
import net.potionstudios.wayfinder.world.entity.ai.goal.ScaredWayfinderGoal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.Set;
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
    private static final EntityDataAccessor<Boolean> DATA_SITTING = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SHIELD = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.INT);

    private float phaseOffset;
    private boolean searching;
    private boolean hasTarget;

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
        hasTarget = false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
        builder.define(DATA_SCARED, false);
        builder.define(DATA_SITTING, false);
        builder.define(DATA_SHIELD, 2);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (getOwnerUUID() != null)
            compound.putUUID("Owner", getOwnerUUID());
        compound.putBoolean("Sitting", entityData.get(DATA_SITTING));
        compound.putFloat("Offset", phaseOffset);
        compound.putBoolean("Searching", searching);
        compound.putBoolean("HasTarget", hasTarget);
        compound.putInt("Shield", entityData.get(DATA_SHIELD));
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

        entityData.set(DATA_SITTING, compound.getBoolean("Sitting"));
        entityData.set(DATA_SHIELD, compound.getInt("Shield"));
        phaseOffset = compound.getFloat("Offset");
        searching = compound.getBoolean("Searching");
        hasTarget = compound.getBoolean("HasTarget");
    }

    @Override
    public @Nullable UUID getOwnerUUID() {
        return entityData.get(DATA_OWNERUUID_ID).orElse(null);
    }

    public void setOwner(@NotNull Player player) {
        setOwnerUUID(player.getUUID());
        PlatformHandler.PLATFORM_HANDLER.setWayfinder(player, getUUID());
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
        if (!level().isClientSide()) {
            if (player.getUUID().equals(getOwnerUUID())) {
                Set<ResourceLocation> set = new java.util.HashSet<>(Set.of());
                for (ResourceKey<Biome> key : level().registryAccess().registryOrThrow(Registries.BIOME).registryKeySet()) {
                    set.add(key.location());
                }
                PlatformHandler.PLATFORM_HANDLER.sendToPlayer(new WayfinderOpenScreenPacket(set), player);
                //WayfinderScreen.openScreen(level().registryAccess().registryOrThrow(Registries.BIOME).registryKeySet());
                return InteractionResult.SUCCESS;
            } //else triggerAnim("controller", "no");
            return InteractionResult.FAIL;
        } else if (getOwner() == null) {
            setOwner(player);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    public boolean isSitting() {
        return entityData.get(DATA_SITTING);
    }

    public void setSitting(boolean sitting) {
        entityData.set(DATA_SITTING, sitting);
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
        return SHIELD.byHits(entityData.get(DATA_SHIELD));
    }

    public void setShield(SHIELD shield) {
        entityData.set(DATA_SHIELD, shield.hits());
    }

    public boolean hasShield() {
        return this.shield() != SHIELD.NONE;
    }

    public boolean hasTarget() {
        return hasTarget;
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
        goalSelector.addGoal(0, new ScaredWayfinderGoal(this));
        goalSelector.addGoal(0, new FollowMobGoal(this,1.0D, 10.0F, 2.0F));
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
        //goalSelector.addGoal(2, new GoToBiomeGoal(this, biome -> biome.is(Biomes.BADLANDS)));
        //goalSelector.addGoal(1, new AvoidEntityGoal<>(this, Monster.class, 8.0F, 1, 1));
    }

    @Override
    public boolean onGround() {
        return getY() - level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockPosition()).getY() <= 2;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (level().isClientSide()) return false;
        if (isScared())
            if (this.shield() == SHIELD.FULL) {
                this.setShield(SHIELD.HALF);
                this.playSound(WayfinderSounds.WAYFINDER_SHIELD_HIT.get());
                return false;
            } else if (this.shield() == SHIELD.HALF) {
                this.setShield(SHIELD.NONE);
                this.playSound(WayfinderSounds.WAYFINDER_SHIELD_BREAK.get());
                return false;
            }

        boolean hurt = super.hurt(source, amount);

        if (hurt) {
            setScared(true);
            if (isDeadOrDying() && getOwner() != null) {
                PlatformHandler.PLATFORM_HANDLER.setWayfinder((Player) getOwner(), Util.NIL_UUID);
                if (source.getEntity() != null && source.getEntity() instanceof ServerPlayer player && getOwner().is(player))
                    WayfinderCriteriaTriggers.WAYFINDER_OWNER_KILLED.get().trigger(player);
            }
        }

        return hurt;
    }

    @Override
    protected void tickDeath() {
        setDeltaMovement(getDeltaMovement().add(0, -0.04, 0));
        super.tickDeath();
    }

    public enum SHIELD {
        FULL(2),
        HALF(1),
        NONE(0);

        private final int hits;

        SHIELD(int hits) {
	        this.hits = hits;
        }

        public int hits() {
            return hits;
        }

        public static SHIELD byHits(int hits) {
            return switch (hits) {
                case 2 -> FULL;
                case 1 -> HALF;
                default -> NONE;
            };
        }
    }
}
