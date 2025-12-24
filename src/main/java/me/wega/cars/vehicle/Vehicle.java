package me.wega.cars.vehicle;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import lombok.Getter;
import lombok.Setter;
import me.wega.cars.item.lift.VehicleLift;
import me.wega.cars.item.part.VehiclePartType;
import me.wega.cars.item.suspension.VehicleSuspension;
import me.wega.cars.vehicle.holder.PartModelHolder;
import me.wega.cars.vehicle.holder.VehicleItemHolder;
import me.wega.cars.vehicle.holder.VehicleWheelHolder;
import me.wega.cars.vehicle.type.VehicleType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.wega.cars.WegaCars.INSTANCE;

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
        return INSTANCE.getVehicleManager().get(uuid);
    }

}
