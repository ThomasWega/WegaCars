package me.wega.cars.listener;

import com.jeff_media.customblockdata.CustomBlockData;
import me.wega.cars.WegaCars;
import me.wega.cars.annotation.BukkitListener;
import me.wega.cars.item.VehicleItemKey;
import me.wega.cars.item.VehicleItemType;
import me.wega.cars.item.spring_compressor.SpringCompressorGUI;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@BukkitListener
public class SpringCompressorListener implements Listener {

    @EventHandler
    public void onPlaceSpring(BlockPlaceEvent event) {
        final ItemStack itemStack = event.getItemInHand();
        if (!VehicleItemKey.ITEM_TYPE.has(itemStack.getItemMeta()) || VehicleItemKey.ITEM_TYPE.get(itemStack.getItemMeta()) != VehicleItemType.SPRING_COMPRESSOR) return;

        final CustomBlockData customBlockData = new CustomBlockData(event.getBlockPlaced(), WegaCars.INSTANCE);
        customBlockData.set(VehicleItemKey.ITEM_TYPE.getNamespace(), VehicleItemType.VEHICLE_ITEM_TYPE_DATA_TYPE, VehicleItemType.SPRING_COMPRESSOR);
    }

    @EventHandler
    public void onUseSpring(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        final CustomBlockData data = new CustomBlockData(event.getClickedBlock(), WegaCars.INSTANCE);
        final NamespacedKey key = VehicleItemKey.ITEM_TYPE.getNamespace();

        if (data.has(key) && data.get(key, VehicleItemType.VEHICLE_ITEM_TYPE_DATA_TYPE) == VehicleItemType.SPRING_COMPRESSOR) {
            new SpringCompressorGUI().show(event.getPlayer());
            event.setCancelled(true);
        }
    }
}
