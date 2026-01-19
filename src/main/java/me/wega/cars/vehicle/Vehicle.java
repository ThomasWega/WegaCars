package me.wega.cars.vehicle;

import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.item.lift.VehicleLift;
import me.wega.cars.item.part.VehiclePart;
import me.wega.cars.item.part.VehiclePartType;
import me.wega.cars.item.part.VehiclePartVisibility;
import me.wega.cars.item.part.impl.VehicleWheels;
import me.wega.cars.item.suspension.VehicleSuspension;
import me.wega.cars.vehicle.controller.VehicleMountController;
import me.wega.cars.vehicle.enums.VehicleWheelPart;
import me.wega.cars.vehicle.holder.*;
import me.wega.cars.vehicle.type.VehicleType;
import me.wega.cars.toolkit.task.Tasks;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.animation.handler.AnimationHandler;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.*;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
public class Vehicle {

    private final @NotNull UUID uniqueId;
    private final @NotNull VehicleType vehicleType;
    private final @NotNull List<VehicleWheelHolder> tires;

    private @Nullable VehicleSuspension suspension;

    private @NotNull Map<VehiclePartType, VehicleItemHolder> parts = new HashMap<>();
    private @NotNull Map<VehicleStat, Integer> cachedStats = new HashMap<>();

    private @Nullable LivingEntity entity;
    private @Nullable ModeledEntity modeledEntity;
    private @Nullable ActiveModel baseModel;

    private @Nullable VehicleLift currentLift;
    private boolean jackLift;
    private long engineSetTimer;
    private boolean disassembled;
    private int fuelRemaining;
    private int driveTime;
    private int tuningValue;
    private @Nullable String firstSparkplug;

    private @Nullable VehicleMountController controller;
    private @Nullable String environment;
    private final @NotNull Map<Interaction, Vector> interactionVectorMap = new HashMap<>();
    private final @NotNull Map<VehiclePartType, PartModelHolder> partModels = new HashMap<>();
    private final @NotNull List<PartModelHolder> tireModels = new ArrayList<>();

    public Vehicle(@NotNull UUID uniqueId,
                   @NotNull VehicleType vehicleType,
                   @NotNull List<VehicleWheelHolder> tires,
                   boolean disassembled) { // Still WIP and subject to change
        this.uniqueId = uniqueId;
        this.vehicleType = vehicleType;
        this.tires = tires;
        this.disassembled = disassembled;
    }

    public void createBaseModel() {
        this.cacheStats();

        this.createInteractionEntities();
        this.createPartModels();
        this.createTires();

        if (this.modeledEntity != null)
            ModelEngineAPI.removeModeledEntity(this.entity);

        this.modeledEntity = ModelEngineAPI.createModeledEntity(this.entity);
        if (!this.modeledEntity.getModels().containsKey(this.getCurrentModel())) {
            this.baseModel = ModelEngineAPI.createActiveModel(this.getCurrentModel());
            this.baseModel.getMountManager().ifPresent(mountManager -> {
                mountManager.setCanDrive(true);
                mountManager.setCanRide(true);
            });

            this.modeledEntity.addModel(this.baseModel, true);
        }
    }

    private void createInteractionEntities() {
        this.interactionVectorMap.keySet().forEach(Entity::remove);
        this.interactionVectorMap.clear();

        if (this.disassembled) {
            this.spawnInteractionEntity(this.vehicleType.getHoodOffset(), VehicleRepairPart.HOOD,  -1);
            this.spawnInteractionEntity(this.vehicleType.getTrunkOffset(), VehicleRepairPart.TRUNK,  -1);
        }

        this.spawnInteractionEntity(this.vehicleType.getDriveOffset(), VehicleRepairPart.DRIVER,  -1);
    }

    public int getAverageBrakeDurability() {
        int durability = 0;
        for (VehicleWheelHolder wheelHolder : this.tires) {
            if (!wheelHolder.getWheelParts().containsKey(VehicleWheelPart.BRAKE)) {
                durability = 0;
                break;
            }

            durability += wheelHolder.getWheelParts().get(VehicleWheelPart.BRAKE).getDurability();
        }

        return durability / this.tires.size();
    }

    public float getTopSpeed() {
        final float speed = this.getStat(VehicleStat.TOP_SPEED);
        final float unsprungWeight = this.getStat(VehicleStat.UNSPRUNG_WEIGHT);
        final float combinedWeight = this.getStat(VehicleStat.COMBINED_WEIGHT);

        return speed - (unsprungWeight + combinedWeight);
    }

    public int getMaxBrakesDurability() {
        int durability = 0;
        for (VehicleWheelHolder wheelHolder : this.tires) {
            if (!wheelHolder.getWheelParts().containsKey(VehicleWheelPart.BRAKE)) {
                durability = 0;
                break;
            }

            durability += wheelHolder.getWheelParts().get(VehicleWheelPart.BRAKE).getVehiclePart().getMaxDurability();
        }

        return durability / this.tires.size();
    }

    private void spawnInteractionEntity(VehicleInteractionData data, VehicleRepairPart part, int tireIndex) {
        final Interaction interaction = this.entity.getWorld().spawn(this.getOffsetLocation(data.vector()), Interaction.class);
        interaction.setPersistent(false);
        interaction.setInteractionWidth(data.width());
        interaction.setInteractionHeight(data.height());

        VehicleKey.ID.set(interaction, this.uniqueId);
        VehicleKey.REPAIR_PART.set(interaction, part);
        if (tireIndex != -1)
            VehicleKey.TIRE_INDEX.set(interaction, tireIndex);

        this.interactionVectorMap.put(interaction, data.vector());
    }

    public @NotNull String getCurrentModel() {
        if (this.jackLift) return this.vehicleType.getJackLiftModel();
        return this.disassembled ? this.vehicleType.getDisassembledModel() : this.vehicleType.getAssembledModel();
    }

    private void createPartModels() {
        for (PartModelHolder model : this.partModels.values()) {
            model.modeledEntity().destroy();
            model.dummy().remove();
        }

        this.partModels.clear();

        for (VehicleItemHolder holder : this.parts.values()) {
            final VehiclePart vehiclePart = holder.getVehiclePart();

            final VehiclePartVisibility visibility = vehiclePart.getItemType().getVisibility();
            if (visibility == VehiclePartVisibility.NEVER) continue;
            if (visibility == VehiclePartVisibility.DISASSEMBLED_ONLY && !disassembled) continue;

            final Vector offset = this.vehicleType.getOffset(vehiclePart.getItemType());
            final ArmorStand armorStand = (ArmorStand) createEntity(ArmorStand.class, this.getOffsetLocation(offset));
            armorStand.setPersistent(false);
            armorStand.setMarker(true);

            final ModeledEntity partModeledEntity = ModelEngineAPI.createModeledEntity(armorStand);
            final ActiveModel activeModel = ModelEngineAPI.createActiveModel(vehiclePart.getPartModel());
            partModeledEntity.addModel(activeModel, true);

            final PartModelHolder partModelHolder = new PartModelHolder(armorStand, partModeledEntity, activeModel, offset);
            this.partModels.put(vehiclePart.getItemType(), partModelHolder);
        }
    }

    private void createTires() {
        for (PartModelHolder model : this.tireModels) {
            model.modeledEntity().destroy();
            model.dummy().remove();
        }
        this.tireModels.clear();

        int tire = 0;
        for (VehicleTireData tireData : this.vehicleType.getTireOffsetList()) {
            final VehicleInteractionData data = tireData.data();
            this.spawnInteractionEntity(data, VehicleRepairPart.WHEEL, tire);
            if (this.tires.size() <= tire) {
                tire++;
                continue;
            }

            final VehicleWheelHolder wheelHolder = this.tires.get(tire);

            tire++;

            final Map<VehicleWheelPart, VehicleItemHolder> holder = wheelHolder.getWheelParts();
            if (!holder.containsKey(VehicleWheelPart.TIRE)) continue;

            final VehicleWheels vehicleItem = (VehicleWheels) holder.get(VehicleWheelPart.TIRE).getVehiclePart();

            final ArmorStand armorStand = (ArmorStand) createEntity(ArmorStand.class, this.getOffsetLocation(data.vector()));
            armorStand.setPersistent(false);
            armorStand.setMarker(true);

            final ModeledEntity partModeledEntity = ModelEngineAPI.createModeledEntity(armorStand);
            partModeledEntity.getBase().getBodyRotationController().setRotationDuration(0);
            partModeledEntity.getBase().getBodyRotationController().setRotationDelay(0);

            final ActiveModel activeModel = ModelEngineAPI.createActiveModel(wheelHolder.isJackStand() ? vehicleItem.getJackStandModel() : vehicleItem.getPartModel());
            partModeledEntity.addModel(activeModel, true);

            final AnimationHandler handler = activeModel.getAnimationHandler();
            handler.forceStopAllAnimations();

            final String animation = tireData.side().isLeft() ?
                    wheelHolder.getAlignmentCombination().getLeftModel() :
                    wheelHolder.getAlignmentCombination().getRightModel();

            handler.playAnimation(animation, 0.3, 0.3, 1, true);

            final PartModelHolder partModelHolder = new PartModelHolder(armorStand, partModeledEntity, activeModel, data.vector());
            this.tireModels.add(partModelHolder);
        }
    }

    public void checkJackStands() {
        this.setDisassembled(this.tires.stream().allMatch(VehicleWheelHolder::isJackStand));
    }

    public void syncEntities() {
        this.partModels.values().forEach(holder ->
                holder.dummy().teleport(getOffsetLocation(holder.offset())));

        int index = 0;
        for (PartModelHolder holder : this.tireModels) {
            final Location location = this.getOffsetLocation(holder.offset());

            if (this.controller != null && this.vehicleType.getTireOffsetList().get(index).side().isFront()) {
                float newYaw = (this.entity.getYaw() - 180.0f) + this.controller.getSteeringAngle() * 5;
                if (newYaw > 180.0f)
                    newYaw = newYaw - 360.0f;
                else if (newYaw < -180.0f)
                    newYaw = newYaw + 360.0f;

                location.setYaw(newYaw);
            }

            holder.dummy().teleport(location);

            index++;
        }
    }

    public void syncEnvironment() {
        final Material material = this.entity.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
        final boolean raining = !this.entity.getWorld().isClearWeather();

        this.environment = VehiclesConfig.ENVIRONMENT_NAME_TABLE.get().get(material, raining);
    }

    private Location getOffsetLocation(Vector offset) {
        final double angle = Math.toRadians(this.entity.getYaw() + 180.0f);
        final Vector vector = this.controller == null ? new Vector(0, 0, 0) :
                this.entity.getLocation().getDirection().multiply(this.controller.getLastSpeed() * VehiclesConfig.VELOCITY_VECTOR_MULTIPLIER.get());
        vector.setY(0.0f); // we don't need the Y component

        final Location location = this.entity.getLocation().clone()
                .add(this.rotateY(offset, angle))
                .add(vector);

        return location;
    }

    // Rotates this vector around the Y axis, returning a new Vector result.
    private Vector rotateY(Vector vector, double angleInRadians) {
        double cos = Math.cos(angleInRadians);
        double sin = Math.sin(angleInRadians);

        double newX = vector.getX() * cos - vector.getZ() * sin;
        double newZ = vector.getX() * sin + vector.getZ() * cos;

        return new Vector(newX, vector.getY(), newZ);
    }

    public boolean hasMismatchedSparkplug() {
        if (this.firstSparkplug == null) return false;
        if (!this.parts.containsKey(VehiclePartType.SPARKPLUG)) return false;

        final VehiclePart part = this.parts.get(VehiclePartType.SPARKPLUG).getVehiclePart();
        return part.getItemId().equals(this.firstSparkplug);
    }

    public void attemptDecreaseDurability() {
        final int maxRoll = VehiclesConfig.MAX_DURABILITY_ROLL.get();
        this.parts.forEach((type, part) -> {
            if (type.getLifeStat() == null) return;

            final int chance = ThreadLocalRandom.current().nextInt(maxRoll);
            if (chance < this.getStat(type.getLifeStat())) {
                part.decreaseDurability();

                if (type == VehiclePartType.ENGINE && this.hasMismatchedSparkplug()) {
                    part.decreaseDurability(); // double durability loss
                }
            }
        });

        this.tires.forEach(holder -> holder.getWheelParts().values().forEach(itemHolder -> {
            final int chance = ThreadLocalRandom.current().nextInt(maxRoll);
            final VehicleStat stat = itemHolder.getVehiclePart().getItemType().getLifeStat();
            if (stat == null) return;

            if (chance < this.getStat(stat)) {
                itemHolder.decreaseDurability();
            }
        }));
    }

    public boolean isFullyAssembled() {
        if (this.tires.stream().anyMatch(VehicleWheelHolder::isJackStand)) return false;
        if (this.jackLift) return false;

        if (this.suspension == null) return false;
        for (VehiclePartType type : VehiclePartType.values()) {
            if (!type.isActive() || type.isOptional()) continue;

            if (!this.parts.containsKey(type)) return false;
            final VehicleItemHolder holder = this.parts.get(type);
            if (type.getLifeStat() != null && holder.getDurability() <= 0) return false;
        }

        for (VehicleWheelHolder holder : this.tires) {
            final Map<VehicleWheelPart, VehicleItemHolder> map = holder.getWheelParts();
            if (!map.containsKey(VehicleWheelPart.BRAKE) || !map.containsKey(VehicleWheelPart.TIRE))
                return false;
        }

        return true;
    }

    public void setDisassembled(boolean disassembled) {
        this.disassembled = disassembled;
        this.createBaseModel();
    }

    public int getMaxFuel() {
        return (int) (getStat(VehicleStat.FUEL_CAPACITY));
    }

    public void decreaseFuel() {
        this.fuelRemaining = Math.max(this.fuelRemaining - 1, 0);
    }

    public int loadFuel(int amount) {
        final int space = this.getMaxFuel() - this.getFuelRemaining();
        if (amount > space) {
            this.fuelRemaining = this.getMaxFuel();
            return (amount - space);
        }

        this.fuelRemaining += amount;
        return 0;
    }

    private void cacheStats() {
        final Map<VehicleStat, Integer> finalStats = new HashMap<>();
        for (VehicleStat vehicleStat : VehicleStat.values()) {
            finalStats.put(vehicleStat, 0);
        }

        for (VehiclePart item : this.getItems()) {
            for (Map.Entry<VehicleStat, Integer> entry : item.getStatistics(this).entrySet()) {
                final VehicleStat stat = entry.getKey();
                finalStats.put(stat, finalStats.get(stat) + entry.getValue());
            }
        }

        final Map<VehicleStat, Integer> tireStats = this.getTireStats();
        for (Map.Entry<VehicleStat, Integer> entry : tireStats.entrySet()) {
            final VehicleStat stat = entry.getKey();
            finalStats.put(stat, finalStats.get(stat) + entry.getValue());
        }

        this.cachedStats = finalStats;
    }

    private @NotNull Map<VehicleStat, Integer> getTireStats() {
        final Map<VehicleStat, Integer> counter = new HashMap<>();
        final Map<VehicleStat, Integer> tireStats = new HashMap<>();

        VehiclePart lastItem = null;
        boolean mismatched = false;

        for (VehicleWheelHolder holder : this.tires) {
            for (VehicleItemHolder itemHolder : holder.getWheelParts().values()) {
                final boolean wheels = itemHolder.getVehiclePart().getItemType() == VehiclePartType.WHEELS;
                if (wheels && lastItem == null) lastItem = itemHolder.getVehiclePart();

                if (wheels && !lastItem.getItemId().equals(itemHolder.getVehiclePart().getItemId())) mismatched = true;

                for (Map.Entry<VehicleStat, Integer> entry : itemHolder.getVehiclePart().getStatistics(this).entrySet()) {
                    final VehicleStat stat = entry.getKey();
                    tireStats.put(stat, tireStats.getOrDefault(stat, 0) + entry.getValue());
                    counter.put(stat, counter.getOrDefault(stat, 0) + 1);
                }

                if (wheels) {
                    for (Map.Entry<VehicleStat, Integer> entry : this.vehicleType.getAlignmentTable().rowMap().getOrDefault(holder.getAlignmentCombination(), new HashMap<>()).entrySet()) {
                        final VehicleStat stat = entry.getKey();
                        tireStats.put(stat, tireStats.getOrDefault(stat, 0) + entry.getValue());
                    }

                    lastItem = itemHolder.getVehiclePart();
                }
            }
        }

        boolean finalMismatched = mismatched;
        tireStats.replaceAll((key, value) -> {
            if (finalMismatched) return (value / counter.getOrDefault(key, 1)) - VehiclesConfig.MISMATCHED_TIRES_PENALTY.get();

            return value / counter.getOrDefault(key, 1);
        });

        return tireStats;
    }

    private List<VehiclePart> getItems() {
        final List<VehiclePart> vehicleParts = new ArrayList<>();
        for (VehiclePartType type : VehiclePartType.values()) {
            if (!this.parts.containsKey(type)) continue;

            vehicleParts.add(this.parts.get(type).getVehiclePart());
        }

        if (this.suspension != null) {
            vehicleParts.add(this.suspension.shock());
            vehicleParts.add(this.suspension.spring());
        }

        return vehicleParts;
    }

    public static @NotNull Vehicle createVehicle(@NotNull UUID uniqueId,
                                                 @NotNull VehicleType vehicleType,
                                                 @NotNull Location location,
                                                 boolean disassembled) {
        final List<VehicleWheelHolder> holders = new ArrayList<>();
        for (int i = 0; i < vehicleType.getTireOffsetList().size(); i++)
            holders.add(new VehicleWheelHolder(new HashMap<>()));

        final Vehicle vehicle = new Vehicle(uniqueId, vehicleType, holders, disassembled);

        final LivingEntity entity = createEntity(Cow.class, location);
        VehicleKey.ID.set(entity, uniqueId);

        vehicle.setEntity(entity);

        vehicle.createBaseModel();

        WegaCars.INSTANCE.getVehicleManager().add(vehicle.getUniqueId(), vehicle);

        return vehicle;
    }

    private static LivingEntity createEntity(@NotNull Class<? extends LivingEntity> entityClass, @NotNull Location location) {
        final LivingEntity entity = location.getWorld().spawn(location, entityClass);
        entity.setInvulnerable(true);
        entity.setInvisible(true);
        entity.setSilent(true);
        entity.setAI(false);
        entity.setCollidable(false);
        entity.setRemoveWhenFarAway(false);

        return entity;
    }

    public static @Nullable Vehicle fromEntity(@NotNull Entity entity) {
        if (!VehicleKey.ID.has(entity)) return null;

        final UUID id = (UUID) VehicleKey.ID.get(entity);
        final Vehicle vehicle = fromId(id);
        if (vehicle == null) return null;

        vehicle.setEntity((LivingEntity) entity);
        vehicle.getEntity().setAI(false); // safety check

        return vehicle;
    }

    public static @Nullable Vehicle fromId(@NotNull UUID uuid) {
        return WegaCars.INSTANCE.getVehicleManager().get(uuid);
    }

    public void mount(@NotNull Player player) {
        if (this.baseModel == null) return;

        this.interactionVectorMap.keySet().forEach(Entity::remove);
        this.interactionVectorMap.clear();

        this.baseModel.getMountManager().ifPresent(mountManager -> {
            if (mountManager.getDriverBone() == null) return;

            mountManager.setCanDrive(true);
            mountManager.setCanRide(true);

            mountManager.mountDriver(player, (entity, mount) -> {
                this.entity.setAI(true);
                WegaCars.INSTANCE.getVehiclePlayerMapManager().add(player, this);
                this.controller = new VehicleMountController(entity, mountManager.getDriverBone(), this, player);

                return controller;
            });
        });
    }

    public void dismount() {
        this.createInteractionEntities();

        this.entity.setAI(false);
        WegaCars.INSTANCE.getVehiclePlayerMapManager().remove(this.controller.getPlayer());
        this.controller = null;

        WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.later(WegaCars.INSTANCE, 5L, ctx -> this.syncEntities()));
    }

    public float getStat(@NotNull VehicleStat vehicleStat) {
        int level = this.cachedStats.get(vehicleStat);
        if (level < 0) level = 0;

        final float statMultiplier = VehiclesConfig.VEHICLE_STAT_MULTIPLIERS.get().get(vehicleStat);
        final float tuningMultiplier = VehiclesConfig.TUNING_STAT_MODIFIERS.get().get(vehicleStat) * this.tuningValue;

        float value = level * statMultiplier;
        if (value == 0.0f) value = statMultiplier * 0.5f;

        value += tuningMultiplier;

        if (!this.parts.containsKey(VehiclePartType.WHEELS) || this.environment == null) return value;

        final VehicleWheels wheels = (VehicleWheels) this.parts.get(VehiclePartType.WHEELS).getVehiclePart();
        final Integer addition = wheels.getEnvironmentTable().get(this.environment, vehicleStat);
        if (addition == null) return value;

        return value + (addition * statMultiplier);
    }

    public void addStat(@NotNull VehicleStat stat, int value) {
        this.cachedStats.put(stat, this.cachedStats.get(stat) + value);
    }

    public void removeStat(@NotNull VehicleStat stat, int value) {
        this.cachedStats.put(stat, this.cachedStats.get(stat) - value);
    }

    public boolean hasController() {
        return this.controller != null;
    }
}
