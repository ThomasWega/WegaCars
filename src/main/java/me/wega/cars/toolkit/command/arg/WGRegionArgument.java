package me.wega.cars.toolkit.command.arg;

import me.wega.cars.toolkit.utils.WGUtils;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.Getter;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Getter
public class WGRegionArgument extends CustomArgument<ProtectedRegion, String> {

    public WGRegionArgument() {
        super(new StringArgument("regionId"), info -> {
            String regionId = info.input();
            if (regionId.equalsIgnoreCase("__global__"))
                throw CustomArgumentException.fromString("Region __global__ is not allowed");
            Optional<ProtectedRegion> region = WGUtils.getRegion(regionId);
            if (region.isEmpty())
                throw CustomArgumentException.fromString("No region with id: " + regionId + " found");
            return region.get();
        });

        this.replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info -> CompletableFuture.completedFuture(WGUtils.getRegionNames())));
    }
}
