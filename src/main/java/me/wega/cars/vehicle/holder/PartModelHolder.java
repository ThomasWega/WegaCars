package me.wega.cars.vehicle.holder;

import com.ticxo.modelengine.api.model.ActiveModel;
import com.ticxo.modelengine.api.model.ModeledEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public record PartModelHolder(@NotNull LivingEntity dummy,
                              @NotNull ModeledEntity modeledEntity,
                              @NotNull ActiveModel activeModel,
                              @NotNull Vector offset) {

}
