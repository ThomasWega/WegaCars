package me.wega.cars.annotation.reflection;

import com.google.gson.GsonBuilder;
import lombok.experimental.UtilityClass;
import me.wega.cars.annotation.JsonAdapter;
import me.wega.cars.annotation.BukkitListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

@UtilityClass
public class AnnotationsReflection {

    public static @NotNull Set<@NotNull Listener> registerListeners(@NotNull JavaPlugin plugin, @NotNull String packageName) {
        final Reflections reflections = new Reflections(packageName);
        final Set<Class<?>> listenerClasses = reflections.getTypesAnnotatedWith(BukkitListener.class);
        final Set<Listener> registeredListeners = new HashSet<>();

        for (Class<?> clazz : listenerClasses) {
            try {
                Listener listener = (Listener) clazz.getDeclaredConstructor().newInstance();
                Bukkit.getPluginManager().registerEvents(listener, plugin);
                registeredListeners.add(listener);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to register listener annotation: " + clazz.getName(), e);
            }
        }
        return registeredListeners;
    }

    public static void registerGsonAdapters(@NotNull JavaPlugin plugin, @NotNull String packageName, @NotNull GsonBuilder builder) {
        final Reflections reflections = new Reflections(packageName);
        final Set<Class<?>> adapterClasses = reflections.getTypesAnnotatedWith(JsonAdapter.class);

        for (Class<?> clazz : adapterClasses) {
            try {
                JsonAdapter annotation = clazz.getAnnotation(JsonAdapter.class);
                final Object adapter = clazz.getDeclaredConstructor().newInstance();
                final boolean hierarchy = annotation.hierarchy();
                final Class<?> type = annotation.value();
                if (hierarchy)
                    builder.registerTypeHierarchyAdapter(type, adapter);
                else builder.registerTypeAdapter(type, adapter);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Failed to register gson adapter annotation: " + clazz.getName(), e);
            }
        }
    }
}