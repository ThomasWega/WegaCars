package me.wega.cars.toolkit.utils;

import com.google.common.base.Preconditions;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Utility class for converting between Roman numerals and Arabic integers.
 */
@UtilityClass
public class RomanNumerals {
    private static final Map<String, Integer> NUMBER_BY_NUMERAL;

    static {
        final Map<String, Integer> map = new LinkedHashMap<>();
        map.put("M", 1000);
        map.put("CM", 900);
        map.put("D", 500);
        map.put("CD", 400);
        map.put("C", 100);
        map.put("XC", 90);
        map.put("L", 50);
        map.put("XL", 40);
        map.put("X", 10);
        map.put("IX", 9);
        map.put("V", 5);
        map.put("IV", 4);
        map.put("I", 1);
        NUMBER_BY_NUMERAL = Collections.unmodifiableMap(map);
    }

    /**
     * Converts an Arabic integer into Roman numerals.
     *
     * @param arabicInteger the Arabic integer to convert (must be > 0).
     * @return the Roman numeral representation of the integer.
     * @throws IllegalArgumentException if arabicInteger is less than 1.
     */
    public static @NotNull String convertIntegerToRomanNumerals(final int arabicInteger) {
        Preconditions.checkArgument(arabicInteger > 0, "Arabic integer must be greater than 0.");

        final StringBuilder romanNumeralsBuilder = new StringBuilder();
        int remainder = arabicInteger;
        for (Map.Entry<String, Integer> entry : NUMBER_BY_NUMERAL.entrySet()) {
            while (remainder >= entry.getValue()) {
                romanNumeralsBuilder.append(entry.getKey());
                remainder -= entry.getValue();
            }
        }
        return romanNumeralsBuilder.toString();
    }

    /**
     * Converts a Roman numeral string into an Arabic integer.
     *
     * @param romanNumeralsString the Roman numeral string to convert.
     * @return the Arabic integer representation of the Roman numeral.
     * @throws IllegalArgumentException if the input string is invalid.
     */
    public static int convertRomanNumeralsToInteger(@NotNull final String romanNumeralsString) {
        Preconditions.checkArgument(!romanNumeralsString.isEmpty(), "Input string cannot be null.");

        int total = 0;
        String lastNumeral = "";
        final char[] romanNumerals = romanNumeralsString.toUpperCase().toCharArray();

        for (int i = romanNumerals.length - 1; i >= 0; i--) {
            final String numeral = String.valueOf(romanNumerals[i]);
            if (!NUMBER_BY_NUMERAL.containsKey(numeral)) {
                throw new IllegalArgumentException("Invalid Roman numeral character: " + numeral);
            }
            total += getIntegerValueFromAdjacentNumerals(numeral, lastNumeral);
            lastNumeral = numeral;
        }
        return total;
    }

    /**
     * Resolves an Arabic value from two adjacent numerals, accounting for subtraction logic.
     *
     * @param leftNumeral  the current numeral (left of the previous one).
     * @param rightNumeral the last processed numeral (right of the current one).
     * @return the Arabic value, negated if necessary.
     */
    private static int getIntegerValueFromAdjacentNumerals(@NotNull final String leftNumeral, @NotNull final String rightNumeral) {
        final int leftNumeralValue = NUMBER_BY_NUMERAL.get(leftNumeral);
        final int rightNumeralValue = rightNumeral.isEmpty() ? 0 : NUMBER_BY_NUMERAL.get(rightNumeral);

        return rightNumeralValue > leftNumeralValue ? -leftNumeralValue : leftNumeralValue;
    }
}
