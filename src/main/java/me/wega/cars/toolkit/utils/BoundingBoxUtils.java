package me.wega.cars.toolkit.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility class for {@link BoundingBox}.
 */
@UtilityClass
public class BoundingBoxUtils {

    /**
     * Gets an amount of random locations within the bounding box
     * that are at least a certain distance apart.
     *
     * @param world the world
     * @param boundingBox the bounding box
     * @param count the number of locations to get
     * @param minDistance the minimum distance between the locations
     * @return a set of random locations within the bounding box
     */
    public static @NotNull Set<@NotNull Location> getRandomLocations(@Nullable World world, @NotNull BoundingBox boundingBox, int count, double minDistance) {
        Set<Location> locations = new HashSet<>();
        Random random = ThreadLocalRandom.current();
        int maxAttempts = count * 20;

        for (int attempts = 0; locations.size() < count && attempts < maxAttempts; attempts++) {
            Location newLoc = new Location(
                    world,
                    boundingBox.getMinX() + (boundingBox.getMaxX() - boundingBox.getMinX()) * random.nextDouble(),
                    boundingBox.getMinY() + (boundingBox.getMaxY() - boundingBox.getMinY()) * random.nextDouble(),
                    boundingBox.getMinZ() + (boundingBox.getMaxZ() - boundingBox.getMinZ()) * random.nextDouble()
            );

            if (locations.stream().noneMatch(loc -> loc.distance(newLoc) < minDistance)) {
                locations.add(newLoc);
            }
        }

        return locations;
    }


    /**
     * Get all blocks that are present in the {@link BoundingBox}
     *
     * @param box        Box to check in
     * @param includeAir Whether to include blocks with type {@link Material#AIR}
     * @return Set of blocks present in the box
     */
    public static @NotNull Set<@NotNull Block> getBlocksInBox(@NotNull World world, @NotNull BoundingBox box, boolean includeAir) {
        final Set<Block> blocks = new HashSet<>();
        final Vector min = box.getMin();
        final Vector max = box.getMax();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    if (!includeAir && block.getType().isAir()) continue;

                    blocks.add(block);
                }
            }
        }
        return blocks;
    }
}
