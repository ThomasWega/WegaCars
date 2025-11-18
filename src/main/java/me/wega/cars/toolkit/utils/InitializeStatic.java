package me.wega.cars.toolkit.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Utility class to initialize static fields of a class
 */
@UtilityClass
public class InitializeStatic {

    /**
     * Initializes all static fields of the given class and its subclasses
     *
     * @param clazz Class to initialize
     */
    public static @NotNull Set<Object> initializeAll(@NotNull Class<?> clazz) {
        final Set<Object> instances = new HashSet<>();
        instances.add(initialize(clazz));

        // Initialize static subclasses
        Class<?>[] subclasses = clazz.getDeclaredClasses();
        for (Class<?> subclass : subclasses) {
            instances.addAll(initializeAll(subclass));
        }

        return instances;
    }

    /**
     * Initializes all static fields of the given class and invokes its no-args constructor
     *
     * @param clazz Class to initialize
     * @see #initializeAll(Class)
     */
    @SneakyThrows
    public static <T> @Nullable T initialize(@NotNull Class<T> clazz) {
        // Load the class to initialize static fields
        Class.forName(clazz.getName());

        // Invoke the no-args constructor
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            // If no no-args constructor, return null
            return null;
        } catch (Exception e) {
            // Wrap other exceptions in runtime exception
            throw new RuntimeException("Failed to invoke no-args constructor for " + clazz.getName(), e);
        }
    }
}