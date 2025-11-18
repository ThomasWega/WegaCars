package me.wega.cars.toolkit.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@UtilityClass
public final class BlockUtils {

    public static Set<Block> getConnected(@NotNull final Block index, @NotNull Predicate<Block> filter) {
        return Arrays.stream(BlockFace.values())
                .map(index::getRelative)
                .filter(filter)
                .collect(Collectors.toSet());
    }

    /**
     * Return the opposite {@link BlockFace} of the specified yaw value
     * @param yaw the yaw to get the opposite face from
     * @return the opposite face represented by {@link BlockFace}
     */
    public static @NotNull BlockFace getOppositeBlockFace(float yaw) {
        final BlockFace face;
        if (yaw >= 135 || yaw < -135) face = BlockFace.NORTH;
        else if (yaw < -45) face = BlockFace.EAST;
        else if (yaw < 45) face = BlockFace.SOUTH;
        else face = BlockFace.WEST;
        return face.getOppositeFace();
    }
}