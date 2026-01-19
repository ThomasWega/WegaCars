package me.wega.cars.listener;

import me.wega.cars.annotation.BukkitListener;
import me.wega.cars.item.ratchet.VehicleRatchet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

@BukkitListener
public class RatchetClickListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onShiftClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        if (!event.hasItem()) return;
        if (!event.getPlayer().isSneaking()) return;

        final ItemStack itemStack = event.getItem();
        if (!VehicleRatchet.isRatchet(itemStack)) return;

        final VehicleRatchet ratchet = VehicleRatchet.fromItem(itemStack);
        ratchet.rotateMode();
        event.setCancelled(true);
    }
}
