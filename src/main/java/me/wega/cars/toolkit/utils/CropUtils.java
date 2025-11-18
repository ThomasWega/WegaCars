package me.wega.cars.toolkit.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class CropUtils {

    public static boolean isCrop(@NotNull Material material) {
        return switch (material) {
            case WHEAT,
                 CARROTS,
                 POTATOES,
                 BEETROOTS,
                 NETHER_WART,
                 COCOA,
                 SWEET_BERRY_BUSH,
                 KELP,
                 BAMBOO,
                 SUGAR_CANE,
                 CACTUS,
                 MUSHROOM_STEM -> true;
            default -> false;
        };
    }
}
