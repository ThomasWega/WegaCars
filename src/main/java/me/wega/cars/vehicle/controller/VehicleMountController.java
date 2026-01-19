package me.wega.cars.vehicle.controller;

import me.wega.cars.VehiclesMessages;
import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.vehicle.VehicleStat;
import me.wega.cars.vehicle.transmission.TransmissionType;
import me.wega.cars.vehicle.transmission.tasks.ManualTransmissionMinigame;
import me.wega.cars.toolkit.utils.ColorUtils;
import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.bone.type.Mount;
import com.ticxo.modelengine.api.mount.controller.impl.AbstractMountController;
import com.ticxo.modelengine.api.nms.entity.wrapper.LookController;
import com.ticxo.modelengine.api.nms.entity.wrapper.MoveController;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ThreadLocalRandom;

@Getter
public class VehicleMountController extends AbstractMountController {

    private final @NotNull Vehicle vehicle;
    private final @NotNull Player player;
    private float lastSpeed = 0.0f;
    private float steeringAngle = 0.0f;
    private float lastYaw = 0.0f;

    private int tickTimer = 0;
    private int durabilityTimer = 0;
    private int manualTimer = 0;
    private int driveTimeTimer = 0;

    @Setter
    private @Nullable ManualTransmissionMinigame manualTransmissionMinigame;

    public VehicleMountController(@NotNull Entity entity,
                                  @NotNull Mount mount,
                                  @NotNull Vehicle vehicle,
                                  @NotNull Player player) {
        super(entity, mount);
        this.vehicle = vehicle;
        this.player = player;
    }

    @Override
    public void updateDriverMovement(MoveController controller, ActiveModel model) {
        WegaCars.INSTANCE.getActionBarManager().sendActionBar(this.player, ColorUtils.color(
                VehiclesConfig.FUEL_BAR_FORMAT.get(),
                Placeholder.parsed("bar", this.getFuelBar()),
                Placeholder.parsed("speed", String.format("%.2f", this.lastSpeed)),
                Placeholder.parsed("max_speed", String.format("%.2f", this.vehicle.getTopSpeed())))
        , 0);

        model.getMountManager().ifPresent(mountManager -> {
            if (this.input.isSneak()) {
                if (this.lastSpeed == 0.0f) {
                    mountManager.dismountDriver();
                    controller.move(0.0f, 0.0f, 0.0f, 0.0f);
                    this.vehicle.syncEntities();
                    return;
                }else{
                    this.player.sendMessage(ColorUtils.color(VehiclesMessages.PLEASE_STOP_VEHICLE.get()));
                }
            }

            if (this.vehicle.getFuelRemaining() == 0) {
                this.lastSpeed = 0.0f;
                this.vehicle.syncEntities();
                return;
            }

            this.steeringAngle = this.getNewSteeringAngle();
            this.lastSpeed = this.getNewSpeed();

            this.vehicle.syncEnvironment();

            if (this.lastSpeed != 0) {
                this.updateTimers();
                final float percentage = Math.abs(this.lastSpeed / 100.0f);
                float front = this.input.getFront();
                if (this.lastSpeed > 0.0f)
                    front = 0.98f;
                else if (this.lastSpeed < 0.0f)
                    front = -0.98f;

                final float stability = this.vehicle.getStat(VehicleStat.STABILITY);
                float randomness;
                if (stability == 0.0f)
                    randomness = ThreadLocalRandom.current().nextFloat(-1.0f, 1.0f);
                else
                    randomness = ThreadLocalRandom.current().nextFloat(-1.0f, 1.0f) / this.vehicle.getStat(VehicleStat.STABILITY);

                controller.move(randomness, 0.0F, front, percentage);
            }

            this.vehicle.syncEntities();
        });
    }

    @Override
    public void updatePassengerMovement(MoveController controller, ActiveModel model) {
        model.getMountManager().ifPresent(mountManager -> {
            if (this.input.isSneak()) {
                mountManager.dismountRider(this.entity);
            }
        });
    }

    @Override
    public void updateDirection(LookController controller, ActiveModel model) {
        float rawNewYaw = this.vehicle.getEntity().getYaw() + steeringAngle;

        if (rawNewYaw > 180.0f)
            rawNewYaw = rawNewYaw - 360.0f;
        else if (rawNewYaw < -180.0f)
            rawNewYaw = rawNewYaw + 360.0f;

        this.lastYaw = rawNewYaw;

        controller.setHeadYaw(rawNewYaw);
        controller.setBodyYaw(rawNewYaw);
    }

    private void updateTimers() {
        this.tickTimer++;
        if (this.tickTimer > VehiclesConfig.FUEL_TICK_TIMER.get() * this.vehicle.getStat(VehicleStat.FUEL_EFFICIENCY)) {
            this.tickTimer = 0;
            this.vehicle.decreaseFuel();
        }

        this.durabilityTimer++;
        if (this.durabilityTimer > VehiclesConfig.DURABILITY_TICK_TIMER.get()) {
            this.durabilityTimer = 0;
            this.vehicle.attemptDecreaseDurability();
        }

        this.driveTimeTimer++;
        if (this.driveTimeTimer % 20 == 0) {
            this.driveTimeTimer = 0;
            this.vehicle.setDriveTime(this.vehicle.getDriveTime() + 1);
        }

        if (this.vehicle.getVehicleType().getTransmissionType() == TransmissionType.AUTOMATIC) return;

        this.manualTimer++;
        if (this.manualTimer > VehiclesConfig.MANUAL_TICK_TIMER.get()) {
            this.manualTimer = 0;
            this.manualTransmissionMinigame = new ManualTransmissionMinigame(this.vehicle, this.player);
            WegaCars.INSTANCE.getTaskScheduler().schedule(this.manualTransmissionMinigame);
        }
    }

    private float getNewSteeringAngle() {
        final float speedFactor = 1.0f - Math.clamp(this.lastSpeed / this.vehicle.getTopSpeed(), 0.0f, 1.0f);
        final float maxSteeringAngle = this.vehicle.getStat(VehicleStat.HANDLING);
        final float minSteeringFactor = Math.min(1.0f, this.vehicle.getCachedStats().get(VehicleStat.HANDLING) / 10.0f);
        final float effectiveMaxSteeringAngle = maxSteeringAngle * (minSteeringFactor + speedFactor * (1 - minSteeringFactor));
        final float side = this.input.getSide() * -1; // invert the input

        final float targetSteeringAngle = side * effectiveMaxSteeringAngle;

        return this.lerp(this.steeringAngle, targetSteeringAngle, VehiclesConfig.SMOOTHING_FACTOR.get().floatValue());
    }

    private float getNewSpeed() {
        float rawNewSpeed = this.lastSpeed;
        final float front = this.input.getFront();
        final float acceleration = this.vehicle.getStat(VehicleStat.ACCELERATION);

        if (this.input.isJump())
            rawNewSpeed = Math.max(0.0f, rawNewSpeed - this.vehicle.getStat(VehicleStat.BRAKING_FORCE) * ((float) this.vehicle.getAverageBrakeDurability() / this.vehicle.getMaxBrakesDurability()));
        else if (front > 0.0f)
            rawNewSpeed += acceleration;
        else if (front < 0.0f)
            rawNewSpeed -= acceleration;

        final float topSpeed = this.vehicle.getTopSpeed();
        return Math.clamp(rawNewSpeed, -(topSpeed / 5), topSpeed);
    }

    private String getFuelBar() {
        final int current = this.vehicle.getFuelRemaining();
        final int total = this.vehicle.getMaxFuel();

        final double percentage = (double) current / total;
        final int progressBarLength = VehiclesConfig.FUEL_BAR_LENGTH.get();
        final String character = VehiclesConfig.FUEL_BAR_CHARACTER.get();
        final int filledBars = (int) Math.round(progressBarLength * percentage);

        return VehiclesConfig.FUEL_BAR_COLOR_AVAILABLE.get() + character.repeat(Math.max(0, filledBars)) +
                VehiclesConfig.FUEL_BAR_COLOR.get() + character.repeat(Math.max(0, progressBarLength - filledBars));
    }

    private float lerp(float a, float b, float f) {
        return a * (1.0f - f) + (b * f);
    }
}

