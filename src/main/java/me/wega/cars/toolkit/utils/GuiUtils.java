package me.wega.cars.toolkit.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class GuiUtils {

    /**
     * Calculate the rows of a gui. Given the amount of slots, it will return the amount of rows the gui will have.
     * If the size is not enough to fill a row, it will make an extra row.
     *
     * @param size Size of the gui
     * @return The size of the gui
     */
    public static int calculateRowsSafely(int size) {
        if (size > 54) throw new IllegalArgumentException("Size cant be bigger than 54, consider a paginated gui");
        if (size <= 0) return 1;
        return (size / 9) + (size % 9 == 0 ? 0 : 1);
    }
}
