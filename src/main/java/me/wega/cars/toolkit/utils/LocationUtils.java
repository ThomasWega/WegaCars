package me.wega.cars.toolkit.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for handling and manipulating Bukkit Location objects.
 * This class provides various methods for working with locations in a Minecraft environment.
 */
@UtilityClass
public class LocationUtils {

    /**
     * Formats a Location object into a string representation.
     *
     * @param location The Location to format.
     * @return A string representation of the Location in the format "x, y, z".
     */
    public static @NotNull String formatLocation(@NotNull Location location) {
        return String.format("%d %d %d", Math.round(location.getX()), Math.round(location.getY()), Math.round(location.getZ()));
    }

    /**
     * Calculates a new Location that is looking at a target Location.
     *
     * @param from The starting Location.
     * @param to   The target Location to look at.
     * @return A new Location with updated pitch and yaw to face the target Location.
     */
    public static @NotNull Location lookAt(Location from, Location to) {
        Location fromClone = from.clone();
        double dx = to.getX() - fromClone.getX();
        double dy = to.getY() - fromClone.getY();
        double dz = to.getZ() - fromClone.getZ();

        double yaw = Math.atan2(dz, dx) * 180 / Math.PI;

        yaw = yaw - 90;
        if (yaw < 0)
            yaw = yaw + 360;

        double distance = Math.sqrt(dx * dx + dz * dz);
        double pitch = Math.atan2(-dy, distance) * 180 / Math.PI;

        fromClone.setPitch((float) pitch);
        fromClone.setYaw((float) yaw);

        return fromClone;
    }

    /**
     * Gets a Location that is a specified number of blocks in front of the given Location.
     *
     * @param location The starting Location.
     * @param blocks   The number of blocks to move forward.
     * @return A new Location that is the specified number of blocks in front of the starting Location.
     */
    public static @NotNull Location getLocationInFront(@NotNull Location location, int blocks) {
        YawFace yawFacing = getFacingAxis(location);
        return location.clone().add(yawFacing.getXOffset() * blocks, 0, yawFacing.getZOffset() * blocks);
    }

    /**
     * Determines the facing direction (YawFace) based on the yaw of the given Location.
     *
     * @param location The Location to check.
     * @return The YawFace enum representing the facing direction.
     */
    public static @NotNull LocationUtils.YawFace getFacingAxis(@NotNull Location location) {
        float yaw = (location.getYaw() % 360 + 360) % 360; // Normalize yaw to 0-360
        if (yaw < 45 || yaw >= 315) return YawFace.Z_POSITIVE;
        if (yaw < 135) return YawFace.X_NEGATIVE;
        if (yaw < 225) return YawFace.Z_NEGATIVE;
        return YawFace.X_POSITIVE;
    }

    /**
     * Checks if there is an empty area in the specified direction and radius from the given Location.
     *
     * @param location The starting Location.
     * @param yf       The YawFace direction to check.
     * @param radius   The radius to check for emptiness.
     * @return true if the area is empty, false otherwise.
     */
    public static boolean isEmpty(@NotNull Location location, @NotNull LocationUtils.YawFace yf, int radius) {
        Location boxLoc = location.clone();
        int xOffset = yf.getXOffset();
        int zOffset = yf.getZOffset();

        for (int x = -radius; x <= radius; x++)
            for (int y = 0; y <= 1; y++)  // Check ground level and one block above
                if (!boxLoc.clone().add(x * zOffset, y, x * xOffset).getBlock().getType().isAir()
                        || !boxLoc.clone().add(x * zOffset, y, x * xOffset).add(xOffset, 0, zOffset).getBlock().getType().isAir())
                    return false;

        return true;
    }

    /**
     * Enum representing the four cardinal directions in terms of yaw facing.
     * Each direction is associated with x and z offsets.
     */
    @Getter
    @RequiredArgsConstructor
    public enum YawFace {
        Z_NEGATIVE(0, -1),
        Z_POSITIVE(0, 1),
        X_NEGATIVE(-1, 0),
        X_POSITIVE(1, 0);

        private final int xOffset;
        private final int zOffset;
    }
}