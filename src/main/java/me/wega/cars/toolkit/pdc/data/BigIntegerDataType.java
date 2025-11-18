package me.wega.cars.toolkit.pdc.data;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class BigIntegerDataType implements PersistentDataType<String, BigInteger> {

    @Override
    public @NotNull Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public @NotNull Class<BigInteger> getComplexType() {
        return BigInteger.class;
    }

    @Override
    public @NotNull String toPrimitive(@NotNull BigInteger complex, @NotNull PersistentDataAdapterContext context) {
        return complex.toString();
    }

    @Override
    public @NotNull BigInteger fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        return new BigInteger(primitive);
    }
}