package me.wega.cars.toolkit.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.ref.SoftReference;

public class MapImageRenderer extends MapRenderer {
    private final SoftReference<BufferedImage> cacheImage;
    private boolean hasRendered = false;

    public MapImageRenderer(final @NotNull BufferedImage bufferedImage) throws IOException {
        this.cacheImage = new SoftReference<>(this.resize(bufferedImage, new Dimension(128, 128)));
    }

    @Override
    public void render(@NotNull MapView view, @NotNull MapCanvas canvas, @NotNull Player player) {
        if (this.hasRendered)
            return;

        if (this.cacheImage.get() != null) {
            canvas.drawImage(0, 0, this.cacheImage.get());
            this.hasRendered = true;
        } else {
            player.sendMessage(Component.text("Attempted to render the image, but the cached image was null!", NamedTextColor.RED));
            this.hasRendered = true;
        }
    }

    private BufferedImage resize(final BufferedImage image, final Dimension size) throws IOException {
        final BufferedImage resized = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_ARGB);
        final Graphics2D g = resized.createGraphics();
        g.drawImage(image, 0, 0, size.width, size.height, null);
        g.dispose();
        return resized;
    }

}