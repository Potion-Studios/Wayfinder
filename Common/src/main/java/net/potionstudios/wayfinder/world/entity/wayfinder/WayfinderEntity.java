package net.potionstudios.wayfinder.world.entity.wayfinder;

import com.google.common.collect.ImmutableSet;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.Profiler;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.advancements.WayfinderCriteriaTriggers;
import net.potionstudios.wayfinder.network.packets.WayfinderCloseScreenPacket;
import net.potionstudios.wayfinder.network.packets.WayfinderOpenScreenPacket;
import net.potionstudios.wayfinder.sounds.WayfinderSoundEvents;
import net.potionstudios.wayfinder.tags.WayfinderBiomeTags;
import net.potionstudios.wayfinder.tags.WayfinderEntityTypeTags;
import net.potionstudios.wayfinder.world.entity.WayfinderEntityTypes;
import net.potionstudios.wayfinder.world.entity.ai.control.WayfinderMoveControl;
import net.potionstudios.wayfinder.world.entity.ai.memory.WayfinderMemoryModuleType;
import net.potionstudios.wayfinder.world.entity.ai.sensing.WayfinderSensorType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;
import com.geckolib.animatable.GeoEntity;
import com.geckolib.animatable.instance.AnimatableInstanceCache;
import com.geckolib.animatable.manager.AnimatableManager;
import com.geckolib.animation.*;
import com.geckolib.animation.object.LoopType;
import com.geckolib.animation.object.PlayState;
import com.geckolib.animation.state.AnimationTest;
import com.geckolib.util.GeckoLibUtil;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;

/**
 * Wayfinder entity
 * @author Joseph T. McQuigg
 * @see PathfinderMob
 * @see GeoEntity
 * @see OwnableEntity
 */
public class WayfinderEntity extends PathfinderMob implements GeoEntity, OwnableEntity {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_1 = RawAnimation.begin().then("idle1", LoopType.PLAY_ONCE);
    private static final RawAnimation IDLE_2 = RawAnimation.begin().then("idle2", LoopType.PLAY_ONCE);
    private static final RawAnimation IDLE_3 = RawAnimation.begin().then("idle3", LoopType.PLAY_ONCE);
    private static final RawAnimation IDLE_4 = RawAnimation.begin().then("idle4", LoopType.PLAY_ONCE);
    private static final RawAnimation IDLE_5 = RawAnimation.begin().then("idle5", LoopType.PLAY_ONCE);
    private static final RawAnimation DEATH = RawAnimation.begin().then("death", LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation SEARCHING_START = RawAnimation.begin().thenPlay("searching_start");
    private static final RawAnimation SEARCHING_END = RawAnimation.begin().then("searching_end", LoopType.PLAY_ONCE);
    private static final RawAnimation SEARCHING_LOOP = RawAnimation.begin().thenLoop("searching_loop");
    private static final RawAnimation NO = RawAnimation.begin().then("no",LoopType.PLAY_ONCE);
    private static final RawAnimation SIT_IDLE_1 = RawAnimation.begin().thenLoop("sit_idle1");
    private static final RawAnimation SIT_IDLE_2 = RawAnimation.begin().thenLoop("sit_idle2");
    private static final RawAnimation SIT_IDLE_3 = RawAnimation.begin().thenLoop("sit_idle3");
    private static final RawAnimation SIT_IDLE_4 = RawAnimation.begin().thenLoop("sit_idle4");
    private static final RawAnimation SCARED = RawAnimation.begin().thenLoop("scared");

    private static final EntityDataAccessor<Optional<BlockPos>> START_POS = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Optional<EntityReference<LivingEntity>>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.OPTIONAL_LIVING_ENTITY_REFERENCE);
    private static final EntityDataAccessor<Integer> DATA_SHIELD = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_PANIC = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_REST = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.BOOLEAN);
    private static final Brain.Provider<WayfinderEntity> BRAIN_PROVIDER = Brain.provider(
            List.of(
                    MemoryModuleType.PATH,
                    MemoryModuleType.LOOK_TARGET,
                    MemoryModuleType.NEAREST_LIVING_ENTITIES,
                    MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
                    MemoryModuleType.NEAREST_PLAYERS,
                    MemoryModuleType.WALK_TARGET,
                    MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
                    MemoryModuleType.HURT_BY,
                    MemoryModuleType.DANGER_DETECTED_RECENTLY,
                    MemoryModuleType.IS_PANICKING,
                    WayfinderMemoryModuleType.IS_RESTING.get(),
                    WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get()
            ),
            List.of(
                    SensorType.NEAREST_LIVING_ENTITIES,
                    SensorType.NEAREST_PLAYERS,
                    SensorType.HURT_BY,
                    WayfinderSensorType.WAYFINDER_SCARE_DETECTED.get()
            ),
            var0 -> WayfinderAi.getActivities()
    );

    private int foundBiomeTick = -20 * Wayfinder.CONFIG.wayfinder.COOLDOWN.value();
    private int completedJourneys;

    public WayfinderEntity(Level level, Player owner) {
        this(WayfinderEntityTypes.WAYFINDER.get(), level);
        setOwner(owner);
    }

    public WayfinderEntity(EntityType<? extends WayfinderEntity> entityType, Level level) {
        super(entityType, level);
        moveControl = new WayfinderMoveControl(this);
        completedJourneys = 0;
        setPersistenceRequired();
    }

    @Override
    protected @NonNull Brain<WayfinderEntity> makeBrain(Brain.@NonNull Packed packedBrain) {
        Brain<WayfinderEntity> brain = BRAIN_PROVIDER.makeBrain(this, packedBrain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        return brain;
    }

    @Override
    public @NotNull Brain<WayfinderEntity> getBrain() {
        return (Brain<WayfinderEntity>) super.getBrain();
    }

    public boolean isScaredBy(LivingEntity entity) {
        return entity.is(WayfinderEntityTypeTags.SCARES_WAYFINDER) || this.getLastHurtByMob() == entity;
    }

    public boolean canScare() {
        return !this.isPanic();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TYPE_ID, 0);
        builder.define(DATA_OWNERUUID_ID, Optional.empty());
        builder.define(DATA_SHIELD, 2);
        builder.define(START_POS, Optional.empty());
        builder.define(DATA_PANIC, false);
        builder.define(DATA_REST, false);
    }

    @Override
    protected void addAdditionalSaveData(@NonNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        EntityReference<LivingEntity> entityReference = this.getOwnerReference();
        EntityReference.store(entityReference, output, "Owner");
        output.putString("Type", getVariant().getSerializedName());
        output.putInt("Shield", entityData.get(DATA_SHIELD));
        if (getStartBlockPos().isPresent())
            output.putLong("StartPos", getStartBlockPos().get().asLong());
        output.putInt("CompletedJourneys", completedJourneys);
    }

    @Override
    protected void readAdditionalSaveData(@NonNull ValueInput input) {
        super.readAdditionalSaveData(input);
        EntityReference<LivingEntity> entityreference = EntityReference.readWithOldOwnerConversion(input, "Owner", this.level());
        if (entityreference != null)
            this.entityData.set(DATA_OWNERUUID_ID, Optional.of(entityreference));
        else this.entityData.set(DATA_OWNERUUID_ID, Optional.empty());

        setVariant(Variant.byName(input.getStringOr("Type", "default")));
        entityData.set(DATA_SHIELD, input.getIntOr("Shield", 2));
        setStartBlockPos(input.getLong("StartPos").map(BlockPos::of));
        completedJourneys = input.getIntOr("CompletedJourneys", 0);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
    }

    public @Nullable EntityReference<LivingEntity> getOwnerReference() {
        return (EntityReference) ((Optional) this.entityData.get(DATA_OWNERUUID_ID)).orElse(null);
    }

    public void setOwner(@Nullable Player owner) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(owner).map(EntityReference::of));
        PlatformHandler.PLATFORM_HANDLER.setWayfinder(owner, getUUID());
    }

    public void setOwnerReference(@Nullable EntityReference<LivingEntity> ownerReference) {
        this.entityData.set(DATA_OWNERUUID_ID, Optional.ofNullable(ownerReference));
    }

    private PlayState predicate(AnimationTest<WayfinderEntity> event) {
        if (isDeadOrDying())
            return event.setAndContinue(DEATH);
        else if (isPanic())
            return event.setAndContinue(SCARED);

        AnimationController<WayfinderEntity> controller = event.controller();
        RawAnimation currentAnimation = controller.getCurrentRawAnimation();
        boolean finished = controller.hasAnimationFinished() || currentAnimation == null;

        if (isResting())
            if (finished || (!currentAnimation.equals(SIT_IDLE_1) && !currentAnimation.equals(SIT_IDLE_2) && !currentAnimation.equals(SIT_IDLE_3) && !currentAnimation.equals(SIT_IDLE_4)))
                return switch (getRandom().nextInt(4)) {
                    case 0 -> event.setAndContinue(SIT_IDLE_3);
                    case 1 -> event.setAndContinue(SIT_IDLE_2);
                    case 2 -> event.setAndContinue(SIT_IDLE_4);
                    default -> event.setAndContinue(SIT_IDLE_1);
                };
            else return PlayState.CONTINUE;

        if (isSearching() && (currentAnimation != null && !currentAnimation.equals(SEARCHING_START) && !currentAnimation.equals(SEARCHING_END)))
          return event.setAndContinue(SEARCHING_LOOP);
        else if (finished || (!currentAnimation.equals(IDLE_1) && !currentAnimation.equals(IDLE_2) && !currentAnimation.equals(IDLE_3) && !currentAnimation.equals(IDLE_4) && !currentAnimation.equals(IDLE_5)))
            return switch (getRandom().nextInt(35)) {
            case 0 -> event.setAndContinue(IDLE_3);
            case 1, 2, 3, 4, 5 -> event.setAndContinue(IDLE_4);
            case 6, 7, 8, 9, 10 -> event.setAndContinue(IDLE_5);
            case 11, 12, 13, 14, 15 -> event.setAndContinue(IDLE_2);
            default -> event.setAndContinue(IDLE_1);
        };

        return PlayState.CONTINUE;
    }


    @Override
    public void registerControllers(AnimatableManager.@NonNull ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>("controller", 0, this::predicate)
                .triggerableAnim("searching_start", SEARCHING_START)
                .triggerableAnim("searching_end", SEARCHING_END)
                .triggerableAnim("idle", IDLE_1)
                .triggerableAnim("idle2", IDLE_2)
                .triggerableAnim("idle3", IDLE_3));
    }

    @Override
    public @NonNull AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (level() instanceof ServerLevel serverLevel) {
            if (isPanic()) return InteractionResult.FAIL;
	        if (player.getItemInHand(hand).is(Items.GLOW_BERRIES)) {
				this.playSound(SoundEvents.GENERIC_EAT.value());
				if (getHealth() < getMaxHealth())
					setHealth(getHealth() + 1);
				else level().broadcastEntityEvent(this, (byte) 14);

				player.getItemInHand(hand).shrink(1);
		        return InteractionResult.SUCCESS;
	        }
            if (getOwner() != null && player.is(getOwner())) {
                if ((foundBiomeTick + (Wayfinder.CONFIG.wayfinder.COOLDOWN.value() * 20)) > serverLevel.getServer().getTickCount()) {
                    no();
                    serverLevel.broadcastEntityEvent(this, (byte) 13);
                    return InteractionResult.FAIL;
                }
                List<Identifier> biomeList = new ArrayList<>();
                for (Holder<Biome> key : serverLevel.getChunkSource().getGenerator().getBiomeSource().possibleBiomes())
                    if (!key.is(WayfinderBiomeTags.WAYFINDER_EXCLUDED))
                        key.unwrapKey().ifPresent(biome -> biomeList.add(biome.identifier()));
                Identifier current;
                if (!getBrain().hasMemoryValue(WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get())) current = Wayfinder.id("clear_packet");
                else current = serverLevel.getBiome(getBrain().getMemory(WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get()).get()).unwrapKey().get().identifier();
                PlatformHandler.PLATFORM_HANDLER.sendToPlayer(new WayfinderOpenScreenPacket(biomeList, current, isResting()), player);
                return InteractionResult.SUCCESS;
            } else if (getOwner() == null && !PlatformHandler.PLATFORM_HANDLER.hasWayfinder(player)) {
                setOwner(player);
                return mobInteract(player, hand);
            } else no();
            return InteractionResult.FAIL;
        }
        return super.mobInteract(player, hand);
    }

    public void startBiomeSearch(Identifier biome) {
        ServerLevel serverLevel = (ServerLevel) level();
        if (serverLevel.getBiome(blockPosition()).is(biome)) {
            no();
            return;
        }

        triggerAnim("controller", "searching_start");

        CompletableFuture.supplyAsync(() -> serverLevel.findClosestBiome3d(
                        biomeHolder -> biomeHolder.is(biome),
                        blockPosition(),
                        Wayfinder.CONFIG.wayfinder.MAX_SEARCH_DISTANCE.value(),
                        32, 64))
                .thenAccept(result -> serverLevel.getServer().execute(() -> {
                    if (!isAlive()) return;

                    if (result != null) {
                        BlockPos biomePos = result.getFirst();
                        getBrain().setMemory(WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get(), biomePos);
                        setStartBlockPos(Optional.of(blockPosition()));

                        foundBiomeTick = serverLevel.getServer().getTickCount();
                        triggerAnim("controller", "searching_end");
                        playSound(SoundEvents.ENCHANTMENT_TABLE_USE);
                    } else no();
                }));
    }

    public void no() {
        triggerAnim("controller", "no");
        playSound(WayfinderSoundEvents.WAYFINDER_NO.get());
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    public void sit() {
        getBrain().eraseMemory(MemoryModuleType.WALK_TARGET);
        getBrain().eraseMemory(MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE);
        this.getNavigation().stop();
        getBrain().setMemory(WayfinderMemoryModuleType.IS_RESTING.get(), Unit.INSTANCE);
    }

    public void stand() {
        triggerAnim("controller", "idle" + (getRandom().nextInt(3) + 1));
        getBrain().eraseMemory(WayfinderMemoryModuleType.IS_RESTING.get());
    }

    public int getCompletedJourneys() {
        return completedJourneys;
    }

    public void incrementCompletedJourneys(ServerPlayer player, int distance) {
        if (distance >= 3000)
            PlatformHandler.PLATFORM_HANDLER.increment3kJourneys(player);
        completedJourneys++;
    }

    public boolean isResting() {
        return entityData.get(DATA_REST);
    }

    public boolean isSearching() {
        return getBrain().isActive(Activity.WORK);
    }

    public boolean isPanic() {
        return entityData.get(DATA_PANIC);
    }

    public SHIELD shield() {
        return SHIELD.byHits(entityData.get(DATA_SHIELD));
    }

    public void setShield(SHIELD shield) {
        entityData.set(DATA_SHIELD, shield.hits());
    }

    public boolean hasShield() {
        return shield() != SHIELD.NONE;
    }

    public Optional<BlockPos> getStartBlockPos() {
        return entityData.get(START_POS);
    }

    public void setStartBlockPos(Optional<BlockPos> pos) {
        entityData.set(START_POS, pos);
    }

    public final boolean unableToMoveToOwner() {
        return isResting() || isPassenger() || getOwner() == null || getOwner().isSpectator() || getOwner().isDeadOrDying() || getBrain().hasMemoryValue(WayfinderMemoryModuleType.JOURNEY_TARGET_POS.get());
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                //.add(Attributes.GRAVITY, 0.06D)
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.FLYING_SPEED, 0.7D)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER, 0);
    }

    @Override
    protected @Nullable SoundEvent getDeathSound() {
        return WayfinderSoundEvents.WAYFINDER_DEATH.get();
    }

    @Override
    protected @Nullable SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
        return getRandom().nextBoolean() ? WayfinderSoundEvents.WAYFINDER_HURT0.get() : WayfinderSoundEvents.WAYFINDER_HURT1.get();
    }

    @Override
    protected @Nullable SoundEvent getAmbientSound() {
        if (Wayfinder.CONFIG.wayfinder.DISABLE_SOUNDS_WHEN_SITTING.value() || getRandom().nextBoolean())
            return SoundEvents.EMPTY;
        else if (isPanic())
            return WayfinderSoundEvents.WAYFINDER_SCARED.get();
        return switch (getRandom().nextInt(7)) {
            case 0 -> WayfinderSoundEvents.WAYFINDER_IDLE0.get();
            case 1 -> WayfinderSoundEvents.WAYFINDER_IDLE1.get();
            case 2 -> WayfinderSoundEvents.WAYFINDER_IDLE2.get();
            case 3 -> WayfinderSoundEvents.WAYFINDER_IDLE3.get();
            case 4 -> WayfinderSoundEvents.WAYFINDER_IDLE4.get();
            case 5 -> WayfinderSoundEvents.WAYFINDER_IDLE5.get();
            default -> SoundEvents.AMETHYST_BLOCK_CHIME;
        };
    }

    @Override
    public boolean isSilent() {
        return super.isSilent() || !Wayfinder.CONFIG.wayfinder.ENABLE_SOUNDS.value();
    }

    @Override
    protected void customServerAiStep(@NonNull ServerLevel level) {
        ProfilerFiller profilerFiller = Profiler.get();
        profilerFiller.push("wayfinderBrain");
        getBrain().tick(level, this);
        profilerFiller.pop();
        profilerFiller.push("wayfinderActivityUpdate");
        WayfinderAi.updateActivity(this);
        profilerFiller.pop();
        boolean currentlyPanicking = this.getBrain().isActive(Activity.PANIC);
        if (isPanic() != currentlyPanicking)
            entityData.set(DATA_PANIC, currentlyPanicking);
        boolean currentlyResting = this.getBrain().isActive(Activity.REST);
        if (isResting() != currentlyResting)
            entityData.set(DATA_REST, currentlyResting);
        super.customServerAiStep(level);
    }

    @Override
    public boolean hurtServer(@NonNull ServerLevel level, @NonNull DamageSource damageSource, float amount) {
        if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY))
            return super.hurtServer(level, damageSource, amount);

        if (isPanic())
            if (shield() == SHIELD.FULL) {
                setShield(SHIELD.HALF);
                playSound(WayfinderSoundEvents.WAYFINDER_SHIELD_HIT.get());
                return false;
            } else if (shield() == SHIELD.HALF) {
                setShield(SHIELD.NONE);
                playSound(WayfinderSoundEvents.WAYFINDER_SHIELD_BREAK.get());
                return false;
            }

        boolean hurt = super.hurtServer(level, damageSource, amount);

        if (hurt) {
            getBrain().setMemoryWithExpiry(MemoryModuleType.DANGER_DETECTED_RECENTLY, true, 200L);
            if (isDeadOrDying() && getOwner() != null)
                if (damageSource.getEntity() != null && damageSource.getEntity() instanceof ServerPlayer player && getOwner().is(player))
                    WayfinderCriteriaTriggers.WAYFINDER_OWNER_KILLED.get().trigger(player);
        }

        return hurt;
    }

    @Override
    public void gameEvent(@NotNull Holder<GameEvent> gameEvent, @Nullable Entity entity) {
        super.gameEvent(gameEvent, entity);
        if (entity != null && entity.is(this) && gameEvent.is(GameEvent.ENTITY_DIE.key())) {
            Player owner = (Player) getOwner();
            if (owner == null) return;
            PlatformHandler.PLATFORM_HANDLER.sendToPlayer(new WayfinderCloseScreenPacket(), owner);
            PlatformHandler.PLATFORM_HANDLER.setWayfinder(owner, Util.NIL_UUID);
            PlatformHandler.PLATFORM_HANDLER.incrementWayfinderDeaths(owner);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 12) this.addParticlesAroundSelf(ParticleTypes.HAPPY_VILLAGER);
        else if (id == 13) this.addParticlesAroundSelf(ParticleTypes.ANGRY_VILLAGER);
		else if (id == 14) this.addParticlesAroundSelf(ParticleTypes.HEART);
        else super.handleEntityEvent(id);
    }

    private void addParticlesAroundSelf(ParticleOptions particleOption) {
        for (int i = 0; i < 5; i++) {
            double d = this.random.nextGaussian() * 0.02;
            double e = this.random.nextGaussian() * 0.02;
            double f = this.random.nextGaussian() * 0.02;
            this.level().addParticle(particleOption, this.getRandomX(1.0), this.getRandomY() + 1.0, this.getRandomZ(1.0), d, e, f);
        }
    }

    public boolean shouldTryTeleportToOwner() {
        LivingEntity livingEntity = getOwner();
        return livingEntity != null && distanceToSqr(livingEntity) >= 288.0;
    }

    public void tryToTeleportToOwner() {
        LivingEntity livingEntity = getOwner();
        if (livingEntity != null)
            teleportToAroundBlockPos(livingEntity.blockPosition());
    }

    private void teleportToAroundBlockPos(BlockPos pos) {
        for (int i = 0; i < 10; i++) {
            int j = getRandom().nextIntBetweenInclusive(-3, 3);
            int k = getRandom().nextIntBetweenInclusive(-3, 3);
            if (Math.abs(j) >= 2 || Math.abs(k) >= 2) {
                int l = getRandom().nextIntBetweenInclusive(-1, 1);
                if (maybeTeleportTo(pos.getX() + j, pos.getY() + l, pos.getZ() + k))
                    return;
            }
        }
    }

    private boolean maybeTeleportTo(int x, int y, int z) {
        if (!canTeleportTo(new BlockPos(x, y, z))) {
            return false;
        } else {
            snapTo((double)x + 0.5, y, (double)z + 0.5, getYRot(), getXRot());
            navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        if (WalkNodeEvaluator.getPathTypeStatic(this, pos) != PathType.WALKABLE) return false;
        else return level().noCollision(this, getBoundingBox().move(pos.subtract(blockPosition())));
    }

    public void setVariant(@NotNull WayfinderEntity.Variant variant) {
        this.entityData.set(DATA_TYPE_ID, variant.getId());
    }

    public @NotNull WayfinderEntity.Variant getVariant() {
        return Variant.byId(this.entityData.get(DATA_TYPE_ID));
    }

    public enum Variant implements StringRepresentable {
        DEFAULT(0, "default"),
        LUSH(1, "lush"),
        MUSHROOM(2, "mushroom"),
        BWG(3, "bwg");

        public static final StringRepresentable.EnumCodec<Variant> CODEC = StringRepresentable.fromEnum(Variant::values);
        private static final IntFunction<Variant> BY_ID = ByIdMap.continuous(Variant::getId, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        private final int id;
        private final String name;

        Variant(final int id, final String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public @NotNull String getSerializedName() {
            return this.name;
        }

        public int getId() {
            return this.id;
        }

        public static Variant byName(String name) {
            return CODEC.byName(name, DEFAULT);
        }

        public static Variant byId(int index) {
            return BY_ID.apply(index);
        }
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
