package me.wega.cars.gson;

import com.google.gson.GsonBuilder;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.experimental.UtilityClass;
import me.wega.cars.WegaCars;
import me.wega.cars.annotation.reflection.AnnotationsReflection;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.NotNull;
import me.wega.cars.toolkit.builder.ConfigItemBuilder;
import me.wega.cars.toolkit.json.adapter.*;

@UtilityClass
public class GsonHandler {

    public static @NotNull GsonBuilder newBuilder() {
        return new GsonBuilder()
                .serializeNulls()
                .enableComplexMapKeySerialization()
                .registerTypeHierarchyAdapter(Block.class, new BlockAdapter())
                .registerTypeHierarchyAdapter(World.class, new WorldAdapter())
                .registerTypeAdapter(ItemStack.class, new ItemStackAdapter())
                .registerTypeAdapter(BoundingBox.class, new BoundingBoxAdapter())
                .registerTypeHierarchyAdapter(Component.class, new ComponentAdapter())
                .registerTypeAdapter(ConfigItemBuilder.class, new ConfigItemBuilderAdapter())
                .registerTypeAdapter(Location.class, new LocationAdapter())
                .registerTypeHierarchyAdapter(Sign.class, new SignAdapter())
                .registerTypeAdapter(ProtectedRegion.class, new WGRegionAdapter())
                .registerTypeHierarchyAdapter(World.class, new WorldAdapter());
    }

    public static @NotNull GsonBuilder withAnnotations() {
        final GsonBuilder builder = newBuilder();
        AnnotationsReflection.registerGsonAdapters(WegaCars.INSTANCE, WegaCars.class.getPackageName(), builder);
        return builder;
    }
}
