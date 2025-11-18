package me.wega.cars.toolkit.builder;

import com.google.gson.JsonObject;
import me.wega.cars.toolkit.utils.ColorUtils;
import me.wega.cars.toolkit.utils.JSONUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.ToString;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
public class ConfigItemBuilder implements Cloneable {
    private @Nullable JsonObject object;
    private @Nullable ConfigurationSection section;
    private @Nullable String material;
    private @Nullable String display;
    private @NotNull List<String> lore = new ArrayList<>();
    private int customModel;
    private @Nullable String base64;
    private @NotNull TagResolver @NotNull [] tagResolvers;

    public ConfigItemBuilder(@NotNull ConfigurationSection section) {
        this(section, new TagResolver[0]);
    }

    public ConfigItemBuilder(@NotNull JsonObject object) {
        this(object, new TagResolver[0]);
    }

    public ConfigItemBuilder(@NotNull ConfigurationSection section, @NotNull TagResolver @NotNull [] tagResolvers) {
        this.section = section;
        this.object = null;
        this.tagResolvers = tagResolvers;
        this.initialize();
    }

    public ConfigItemBuilder(@NotNull JsonObject object, @NotNull TagResolver @NotNull [] tagResolvers) {
        this.object = object;
        this.section = null;
        this.tagResolvers = tagResolvers;
        this.initialize();
    }

    private void initialize() {
        if (section != null) {
            material = section.getString("material");
            display = section.getString("display");
            lore = section.getStringList("lore");
            customModel = section.getInt("customModel");
            base64 = section.getString("base64");
        } else if (object != null) {
            material = JSONUtils.getStringOrDefault(object, "material", null);
            display = JSONUtils.getStringOrDefault(object, "display", null);
            lore = JSONUtils.jsonArrayToList(object.getAsJsonArray("lore"));
            customModel = JSONUtils.getIntOrDefault(object, "customModel", 0);
            base64 = JSONUtils.getStringOrDefault(object, "base64", null);
        }
    }

    public void reload(@NotNull ConfigurationSection section) {
        if (this.section == null) return;

        this.section = section;
        this.initialize();
    }

    public ItemBuilder builder() {
        ItemBuilder builder;
        if (base64 != null)
            builder = new ItemBuilder(SkullCreator.itemFromBase64(base64));
        else {
            Objects.requireNonNull(material, "Material is required component of item");
            builder = new ItemBuilder(Material.getMaterial(material.toUpperCase()));
        }

        if (display != null)
            builder.displayName(ColorUtils.color(display, tagResolvers));

        builder.lore(ColorUtils.color(lore, tagResolvers));

        if (customModel > 0)
            builder.customModel(customModel);

        return builder;
    }

    public ItemStack build() {
        return builder().build();
    }

    public @NotNull ConfigItemBuilder setTagResolvers(@NotNull TagResolver... tagResolvers) {
        this.tagResolvers = tagResolvers;
        return this;
    }

    public @NotNull ConfigItemBuilder addTagResolvers(@NotNull TagResolver... tagResolvers) {
        List<TagResolver> list = new ArrayList<>(List.of(this.tagResolvers));
        list.addAll(List.of(tagResolvers));
        this.tagResolvers = list.toArray(TagResolver[]::new);
        return this;
    }

    @SneakyThrows
    @Override
    public ConfigItemBuilder clone() {
        ConfigItemBuilder clone = (ConfigItemBuilder) super.clone();
        clone.base64 = this.base64;
        clone.customModel = this.customModel;
        clone.display = this.display;
        clone.material = this.material;
        clone.lore = new ArrayList<>(this.lore);
        clone.tagResolvers = this.tagResolvers.clone();
        clone.object = this.object != null ? this.object.deepCopy() : null;
        clone.section = this.section;
        clone.initialize();
        return clone;
    }
}