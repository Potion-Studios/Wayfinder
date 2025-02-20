package net.potionstudios.wayfinder.world.entity.wayfinder;

import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.advancements.critereon.WayfinderCriteriaTriggers;
import net.potionstudios.wayfinder.network.packets.WayfinderOpenScreenPacket;
import net.potionstudios.wayfinder.sounds.WayfinderSounds;
import net.potionstudios.wayfinder.world.entity.WayfinderEntities;
import net.potionstudios.wayfinder.world.entity.ai.goal.FollowOwnerGoal;
import net.potionstudios.wayfinder.world.entity.ai.goal.GoToPosGoal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class WayfinderEntity extends Mob implements GeoEntity, OwnableEntity {

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenPlay("idle");
    private static final RawAnimation DEATH = RawAnimation.begin().then("death", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation SEARCHING_START = RawAnimation.begin().thenPlay("searching_start");
    private static final RawAnimation SEARCHING_END = RawAnimation.begin().thenPlay("searching_end");
    private static final RawAnimation SEARCHING_LOOP = RawAnimation.begin().thenLoop("searching_loop");
    private static final RawAnimation NO = RawAnimation.begin().then("no", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation SIT = RawAnimation.begin().then("sit", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation SIT_IDLE = RawAnimation.begin().thenLoop("sit_idle");
    private static final RawAnimation SCARED = RawAnimation.begin().thenLoop("scared");

    private static final EntityDataAccessor<Optional<BlockPos>> BLOCK_POS = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    protected static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> DATA_SCARED = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SITTING = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SHIELD = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.INT);

    private boolean searching;

    public WayfinderEntity(Level level, Player owner) {
        this(WayfinderEntities.WAYFINDER.get(), level);
        setOwner(owner);
    }

    public WayfinderEntity(EntityType<? extends WayfinderEntity> entityType, Level level) {
        super(entityType, level);
        moveControl = new FlyingMoveControl(this, 30, true);
        searching = false;
        setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
        builder.define(DATA_SCARED, false);
        builder.define(DATA_SITTING, false);
        builder.define(DATA_SHIELD, 2);
        builder.define(BLOCK_POS, Optional.empty());
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        if (getOwnerUUID() != null)
            compound.putUUID("Owner", getOwnerUUID());
        compound.putBoolean("Sitting", entityData.get(DATA_SITTING));
        compound.putBoolean("Searching", searching);
        compound.putInt("Shield", entityData.get(DATA_SHIELD));
        if (gettargetBiomeBlockPos().isPresent())
            compound.putLong("BlockPos", gettargetBiomeBlockPos().get().asLong());
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
        searching = compound.getBoolean("Searching");

        if (compound.contains("BlockPos"))
            setTargetBlockPos(Optional.of(BlockPos.of(compound.getLong("BlockPos"))));
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
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate).triggerableAnim("no", NO).triggerableAnim("sit", SIT).triggerableAnim("searching_start", SEARCHING_START));
    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        if (isDeadOrDying())
            return event.setAndContinue(DEATH);
        else if (isScared())
            return event.setAndContinue(SCARED);
        else if (isSitting())
            return event.setAndContinue(SIT_IDLE);
        else if (isSearching())
            return event.setAndContinue(SEARCHING_LOOP);
        return event.setAndContinue(IDLE);
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (!level().isClientSide()) {
            if (isScared()) return InteractionResult.FAIL;
            if (player.getUUID().equals(getOwnerUUID())) {
                List<ResourceLocation> biomeList = new ArrayList<>();
                for (Holder<Biome> key : ((ServerLevel) level()).getChunkSource().getGenerator().getBiomeSource().possibleBiomes())
                    key.unwrapKey().ifPresent(biome -> biomeList.add(biome.location()));
                PlatformHandler.PLATFORM_HANDLER.sendToPlayer(new WayfinderOpenScreenPacket(biomeList, isSitting()), player);
                return InteractionResult.SUCCESS;
            } else triggerAnim("controller", "no");
            return InteractionResult.FAIL;
        } else if (getOwner() == null) {
            setOwner(player);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public void startBiomeSearch(ResourceLocation biome) {
        triggerAnim("controller", "searching_start");
        setSearching(true);
        Pair<BlockPos, Holder<Biome>> value = ((ServerLevel) level()).
                findClosestBiome3d(biomeHolder -> biomeHolder.is(biome), blockPosition(),
                        Wayfinder.CONFIG.wayfinder.MAX_SEARCH_DISTANCE_IN_CHUNKS, 32, 64);
        if (value != null) setTargetBlockPos(Optional.of(value.getFirst()));
        else triggerAnim("controller", "no");
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    public void sit() {
        triggerAnim("controller", "sit");
        setSitting(true);
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

    public boolean hasTargetBiome() {
        return gettargetBiomeBlockPos().isPresent();
    }

    public Optional<BlockPos> gettargetBiomeBlockPos() {
        return entityData.get(BLOCK_POS);
    }

    public void setTargetBlockPos(Optional<BlockPos> pos) {
        entityData.set(BLOCK_POS, pos);
    }

    public final boolean unableToMoveToOwner() {
        return isSitting() || isPassenger() || getOwner() == null || getOwner().isSpectator() || getOwner().isDeadOrDying();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.FLYING_SPEED, 2D)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER, 0)
                .add(Attributes.GRAVITY, 0.0f);
    }

    @Override
    public void aiStep() {
//        if (!this.onGround() && this.getDeltaMovement().y < 0.0) {
//            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0, 0.75, 1.0));
//        }
        if (this.onGround() && !isSitting()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, 0.1, 0));
        }
        super.aiStep();
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
        goalSelector.addGoal(0, new FollowOwnerGoal(this, getOwner(), 1.2f, 2, 100));
        goalSelector.addGoal(0, new GoToPosGoal(this, getOwner(), gettargetBiomeBlockPos(), 3, 2, 100));
        goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 8.0F));
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
                Player owner = (Player) getOwner();
                PlatformHandler.PLATFORM_HANDLER.setWayfinder(owner, Util.NIL_UUID);
                PlatformHandler.PLATFORM_HANDLER.incrementWayfinderDeaths(owner);
                if (source.getEntity() != null && source.getEntity() instanceof ServerPlayer player && owner.is(player))
                    WayfinderCriteriaTriggers.WAYFINDER_OWNER_KILLED.get().trigger(player);
            }
        }

        return hurt;
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
