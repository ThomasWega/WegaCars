package me.wega.cars.toolkit.pdc.data;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Arrays;

public class BigIntegerArrayDataType implements PersistentDataType<String, BigInteger[]> {
    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<BigInteger[]> getComplexType() {
        return BigInteger[].class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull BigInteger @NotNull [] complex, @NotNull PersistentDataAdapterContext context) {
        return Arrays.stream(complex)
                .map(BigInteger::toString)
                .reduce((a, b) -> a + "," + b)
                .orElse("");
    }

    @Override
    public @NotNull BigInteger @NotNull [] fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return Arrays.stream(primitive.split(","))
                .map(BigInteger::new)
                .toArray(BigInteger[]::new);
    }
}