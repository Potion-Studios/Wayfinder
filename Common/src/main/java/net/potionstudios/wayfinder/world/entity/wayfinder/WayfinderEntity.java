package net.potionstudios.wayfinder.world.entity.wayfinder;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Dynamic;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.DebugPackets;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.OldUsersConverter;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.potionstudios.wayfinder.PlatformHandler;
import net.potionstudios.wayfinder.Wayfinder;
import net.potionstudios.wayfinder.advancements.critereon.WayfinderCriteriaTriggers;
import net.potionstudios.wayfinder.network.packets.WayfinderCloseScreenPacket;
import net.potionstudios.wayfinder.network.packets.WayfinderOpenScreenPacket;
import net.potionstudios.wayfinder.sounds.WayfinderSounds;
import net.potionstudios.wayfinder.tags.WayfinderBiomeTags;
import net.potionstudios.wayfinder.world.entity.WayfinderEntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoAnimatable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.IntFunction;

public class WayfinderEntity extends PathfinderMob implements GeoEntity, OwnableEntity, VariantHolder<WayfinderEntity.Variant> {
    private static final EntityDataAccessor<Integer> DATA_TYPE_ID = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache animatableInstanceCache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_1 = RawAnimation.begin().then("idle1", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation IDLE_2 = RawAnimation.begin().then("idle2", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation IDLE_3 = RawAnimation.begin().then("idle3", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation IDLE_4 = RawAnimation.begin().then("idle4", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation IDLE_5 = RawAnimation.begin().then("idle5", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation DEATH = RawAnimation.begin().then("death", Animation.LoopType.HOLD_ON_LAST_FRAME);
    private static final RawAnimation SEARCHING_START = RawAnimation.begin().thenPlay("searching_start");
    private static final RawAnimation SEARCHING_END = RawAnimation.begin().then("searching_end", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation SEARCHING_LOOP = RawAnimation.begin().thenLoop("searching_loop");
    private static final RawAnimation NO = RawAnimation.begin().then("no", Animation.LoopType.PLAY_ONCE);
    private static final RawAnimation SIT_IDLE_1 = RawAnimation.begin().thenLoop("sit_idle1");
    private static final RawAnimation SIT_IDLE_2 = RawAnimation.begin().thenLoop("sit_idle2");
    private static final RawAnimation SIT_IDLE_3 = RawAnimation.begin().thenLoop("sit_idle3");
    private static final RawAnimation SIT_IDLE_4 = RawAnimation.begin().thenLoop("sit_idle4");
    private static final RawAnimation SCARED = RawAnimation.begin().thenLoop("scared");

    private static final EntityDataAccessor<Optional<BlockPos>> BLOCK_POS = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Optional<UUID>> DATA_OWNERUUID_ID = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Boolean> DATA_SCARED = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_SITTING = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> DATA_SHIELD = SynchedEntityData.defineId(WayfinderEntity.class, EntityDataSerializers.INT);

    private float phaseOffset;
    private int foundBiomeTick = -20 * Wayfinder.CONFIG.wayfinder.COOLDOWN.value();
    private int completedJourneys;

    protected static final ImmutableList<SensorType<? extends Sensor<? super WayfinderEntity>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES, SensorType.NEAREST_PLAYERS, SensorType.HURT_BY
    );
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.PATH,
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.DANGER_DETECTED_RECENTLY,
            MemoryModuleType.IS_PANICKING
    );

    public WayfinderEntity(Level level, Player owner) {
        this(WayfinderEntityType.WAYFINDER.get(), level);
        setOwner(owner);
    }

    public WayfinderEntity(EntityType<? extends WayfinderEntity> entityType, Level level) {
        super(entityType, level);
        phaseOffset = random.nextFloat() * (float) (2 * Math.PI);
        moveControl = new FlyingMoveControl(this, 20, true);
        //moveControl = new WayfinderMoveControl(this, phaseOffset);
        completedJourneys = 0;
        setPersistenceRequired();
    }

    @Override
    protected Brain.@NotNull Provider<WayfinderEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }

    @Override
    protected @NotNull Brain<?> makeBrain(@NotNull Dynamic<?> dynamic) {
        return WayfinderAi.makeBrain(this.brainProvider().makeBrain(dynamic));
    }

    @Override
    public @NotNull Brain<WayfinderEntity> getBrain() {
        return (Brain<WayfinderEntity>) super.getBrain();
    }

    @Override
    protected void sendDebugPackets() {
        super.sendDebugPackets();
        DebugPackets.sendEntityBrain(this);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_TYPE_ID, 0);
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
        compound.putString("Type", getVariant().getSerializedName());
        compound.putBoolean("Sitting", entityData.get(DATA_SITTING));
        compound.putFloat("Offset", phaseOffset);
        compound.putInt("Shield", entityData.get(DATA_SHIELD));
        if (getTargetBiomeBlockPos().isPresent())
            compound.putLong("BlockPos", getTargetBiomeBlockPos().get().asLong());
        compound.putInt("CompletedJourneys", completedJourneys);
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

        setVariant(Variant.byName(compound.getString("Type")));
        entityData.set(DATA_SITTING, compound.getBoolean("Sitting"));
        entityData.set(DATA_SHIELD, compound.getInt("Shield"));
        phaseOffset = compound.getFloat("Offset");
        if (compound.contains("BlockPos"))
            setTargetBlockPos(Optional.of(BlockPos.of(compound.getLong("BlockPos"))));
        completedJourneys = compound.getInt("CompletedJourneys");
    }

    @Override
    public void onSyncedDataUpdated(@NotNull EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if (!level().isClientSide() && dataAccessor.equals(DATA_SCARED) && getOwner() != null)
            if (entityData.get(DATA_SCARED))
                PlatformHandler.PLATFORM_HANDLER.sendToPlayer(new WayfinderCloseScreenPacket(), (Player) getOwner());
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        FlyingPathNavigation navigation = new FlyingPathNavigation(this, level);
        navigation.setCanOpenDoors(false);
        navigation.setCanFloat(true);
        return navigation;
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
        controllers.add(new AnimationController<>(this, "controller", 0, this::predicate)
                .triggerableAnim("searching_start", SEARCHING_START).triggerableAnim("searching_end", SEARCHING_END)
                .triggerableAnim("idle", IDLE_1).triggerableAnim("idle2", IDLE_2).triggerableAnim("idle3", IDLE_3));
    }

    private <E extends GeoAnimatable> PlayState predicate(AnimationState<E> event) {
        if (isDeadOrDying())
            return event.setAndContinue(DEATH);
        else if (isScared())
            return event.setAndContinue(SCARED);

        AnimationController<E> controller = event.getController();
        RawAnimation currentAnimation = controller.getCurrentRawAnimation();
        boolean finished = controller.hasAnimationFinished() || currentAnimation == null;

        if (isSitting())
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
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return animatableInstanceCache;
    }

    @Override
    protected @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (level() instanceof ServerLevel serverLevel) {
            if (isScared()) return InteractionResult.FAIL;
	        if (player.getItemInHand(hand).is(Items.GLOW_BERRIES)) {
				this.playSound(SoundEvents.GENERIC_EAT);
				if (getHealth() < getMaxHealth())
					setHealth(getHealth() + 1);
				else level().broadcastEntityEvent(this, (byte) 14);

				player.getItemInHand(hand).shrink(1);
		        return InteractionResult.SUCCESS;
	        }
            if (player.getUUID().equals(getOwnerUUID())) {
                if ((foundBiomeTick + (Wayfinder.CONFIG.wayfinder.COOLDOWN.value() * 20)) > serverLevel.getServer().getTickCount()) {
                    no();
                    serverLevel.broadcastEntityEvent(this, (byte) 13);
                    return InteractionResult.FAIL;
                }
                List<ResourceLocation> biomeList = new ArrayList<>();
                for (Holder<Biome> key : serverLevel.getChunkSource().getGenerator().getBiomeSource().possibleBiomes())
                    if (!key.is(WayfinderBiomeTags.WAYFINDER_EXCLUDED))
                        key.unwrapKey().ifPresent(biome -> biomeList.add(biome.location()));
                ResourceLocation current;
                if (getTargetBiomeBlockPos().isEmpty()) current = Wayfinder.id("clear_packet");
                else current = serverLevel.getBiome(getTargetBiomeBlockPos().get()).unwrapKey().get().location();
                PlatformHandler.PLATFORM_HANDLER.sendToPlayer(new WayfinderOpenScreenPacket(biomeList, current, isSitting()), player);
                return InteractionResult.SUCCESS;
            } else if (getOwner() == null && !PlatformHandler.PLATFORM_HANDLER.hasWayfinder(player)) {
                setOwner(player);
                return mobInteract(player, hand);
            } else no();
            return InteractionResult.FAIL;
        }
        return super.mobInteract(player, hand);
    }

    public void startBiomeSearch(ResourceLocation biome) {
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
                        getBrain().setMemory(MemoryModuleType.WALK_TARGET,
                                new WalkTarget(result.getFirst(), 1, 50000)
                        );

                        foundBiomeTick = serverLevel.getServer().getTickCount();
                        triggerAnim("controller", "searching_end");
                        playSound(SoundEvents.ENCHANTMENT_TABLE_USE);

                        getBrain().setActiveActivityIfPossible(Activity.WORK);
                    } else no();
                }));
    }

    public void no() {
        triggerAnim("controller", "no");
        playSound(WayfinderSounds.WAYFINDER_NO.get());
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    public void sit() {
        setSitting(true);
        getBrain().setActiveActivityIfPossible(Activity.REST);
    }

    public void stand() {
        triggerAnim("controller", "idle" + (getRandom().nextInt(3) + 1));
        setSitting(false);
    }

    public int getCompletedJourneys() {
        return completedJourneys;
    }

    public void incrementCompletedJourneys() {
        completedJourneys++;
    }

    public boolean isSitting() {
        return entityData.get(DATA_SITTING);
    }

    private void setSitting(boolean sitting) {
        entityData.set(DATA_SITTING, sitting);
    }

    public boolean isSearching() {
        return getTargetBiomeBlockPos().isPresent();
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
        return shield() != SHIELD.NONE;
    }

    public Optional<BlockPos> getTargetBiomeBlockPos() {
        return entityData.get(BLOCK_POS);
    }

    public void setTargetBlockPos(Optional<BlockPos> pos) {
        entityData.set(BLOCK_POS, pos);
    }

    public final boolean unableToMoveToOwner() {
        return isSitting() || isPassenger() || getOwner() == null || getOwner().isSpectator() || getOwner().isDeadOrDying() || getTargetBiomeBlockPos().isPresent();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.GRAVITY, 0.06D)
                .add(Attributes.MAX_HEALTH, 20)
                .add(Attributes.FLYING_SPEED, 3D)
                .add(Attributes.MOVEMENT_SPEED, 2D)
                .add(Attributes.FALL_DAMAGE_MULTIPLIER, 0);
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
        if (Wayfinder.CONFIG.wayfinder.DISABLE_SOUNDS_WHEN_SITTING.value() || getRandom().nextBoolean())
            return SoundEvents.EMPTY;
        else if (isScared())
            return WayfinderSounds.WAYFINDER_SCARED.get();
        return switch (getRandom().nextInt(7)) {
            case 0 -> WayfinderSounds.WAYFINDER_IDLE0.get();
            case 1 -> WayfinderSounds.WAYFINDER_IDLE1.get();
            case 2 -> WayfinderSounds.WAYFINDER_IDLE2.get();
            case 3 -> WayfinderSounds.WAYFINDER_IDLE3.get();
            case 4 -> WayfinderSounds.WAYFINDER_IDLE4.get();
            case 5 -> WayfinderSounds.WAYFINDER_IDLE5.get();
            default -> SoundEvents.AMETHYST_BLOCK_CHIME;
        };
    }

    @Override
    public boolean isSilent() {
        return super.isSilent() || !Wayfinder.CONFIG.wayfinder.ENABLE_SOUNDS.value();
    }

    @Override
    protected void customServerAiStep() {
        level().getProfiler().push("wayfinderBrain");
        getBrain().tick((ServerLevel) level(), this);
        level().getProfiler().pop();
        level().getProfiler().push("wayfinderActivityUpdate");
        WayfinderAi.updateActivity(this);
        level().getProfiler().pop();
        super.customServerAiStep();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (level().isClientSide()) return false;

        if (isScared())
            if (shield() == SHIELD.FULL) {
                setShield(SHIELD.HALF);
                playSound(WayfinderSounds.WAYFINDER_SHIELD_HIT.get());
                return false;
            } else if (shield() == SHIELD.HALF) {
                setShield(SHIELD.NONE);
                playSound(WayfinderSounds.WAYFINDER_SHIELD_BREAK.get());
                return false;
            }

        boolean hurt = super.hurt(source, amount);

        if (hurt) {
            setScared(true);
            if (isDeadOrDying() && getOwner() != null)
                if (source.getEntity() != null && source.getEntity() instanceof ServerPlayer player && getOwner().is(player))
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
            moveTo((double)x + 0.5, y, (double)z + 0.5, getYRot(), getXRot());
            navigation.stop();
            return true;
        }
    }

    private boolean canTeleportTo(BlockPos pos) {
        if (WalkNodeEvaluator.getPathTypeStatic(this, pos) != PathType.WALKABLE) return false;
        else return level().noCollision(this, getBoundingBox().move(pos.subtract(blockPosition())));
    }

    @Override
    public void setVariant(@NotNull WayfinderEntity.Variant variant) {
        this.entityData.set(DATA_TYPE_ID, variant.getId());
    }

    @Override
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
