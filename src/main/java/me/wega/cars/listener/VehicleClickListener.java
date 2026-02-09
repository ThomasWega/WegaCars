package me.wega.cars.listener;

import me.wega.cars.WegaCars;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.VehiclesMessages;
import me.wega.cars.annotation.BukkitListener;
import me.wega.cars.event.PlayerVehicleClickEvent;
import me.wega.cars.item.VehicleItemType;
import me.wega.cars.item.lift.VehicleLift;
import me.wega.cars.item.part.VehiclePart;
import me.wega.cars.item.part.VehiclePartType;
import me.wega.cars.item.ratchet.VehicleRatchet;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.vehicle.VehicleKey;
import me.wega.cars.vehicle.VehicleRepairPart;
import me.wega.cars.vehicle.enums.VehicleWheelPart;
import me.wega.cars.vehicle.gui.EngineVehicleGUI;
import me.wega.cars.vehicle.gui.SparkplugVehicleGUI;
import me.wega.cars.vehicle.gui.TiresVehicleGUI;
import me.wega.cars.vehicle.gui.TrunkVehicleGUI;
import me.wega.cars.vehicle.holder.VehicleItemHolder;
import me.wega.cars.vehicle.holder.VehicleTireData;
import me.wega.cars.vehicle.holder.VehicleWheelHolder;
import me.wega.cars.toolkit.task.Tasks;
import me.wega.cars.toolkit.utils.ColorUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

@BukkitListener
public class VehicleClickListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onVehicleClick(PlayerInteractAtEntityEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        final Player player = event.getPlayer();

        if (!(event.getRightClicked() instanceof Interaction interaction)) return;
        if (!VehicleKey.ID.has(interaction)) return;

        final Vehicle vehicle = Vehicle.fromId((UUID) VehicleKey.ID.get(interaction));
        if (vehicle == null) return;

        event.setCancelled(true);

        final ItemStack itemStack = event.getPlayer().getInventory().getItemInMainHand();
        final VehicleRepairPart part = (VehicleRepairPart) VehicleKey.REPAIR_PART.get(interaction);
        if (part == VehicleRepairPart.WHEEL) {
            this.handleTires(player, vehicle, itemStack, interaction);
            return;
        }

        if (part == VehicleRepairPart.TRUNK || part == VehicleRepairPart.HOOD) {
            this.handleTrunk(player, vehicle, itemStack, part);
            return;
        }

        if (part == VehicleRepairPart.DRIVER) {
           this.handleMainHitbox(player, vehicle, itemStack);
        }
    }

    private void handleTires(Player player, Vehicle vehicle, ItemStack itemStack, Interaction interaction) {
        final int index = VehicleKey.TIRE_INDEX.getInt(interaction);
        final VehicleWheelHolder holder = vehicle.getTires().get(index);

        if (holder.isJackStand() && player.isSneaking()) {
            player.getInventory().addItem(VehicleItemType.JACK_STAND.getItemStack().clone());
            holder.setJackStand(false);
            vehicle.checkJackStands();
            vehicle.createBaseModel();
            return;
        }

        if (!holder.isJackStand() && itemStack.getType() != Material.AIR && VehicleItemType.getType(itemStack.getItemMeta()) == VehicleItemType.JACK_STAND) {
            if (!vehicle.isJackLift()) {
                player.sendMessage(ColorUtils.color(VehiclesMessages.SET_JACK_LIFT_FIRST.get()));
                return;
            }

            itemStack.setAmount(itemStack.getAmount() - 1);
            holder.setJackStand(true);
            vehicle.checkJackStands();
            vehicle.createBaseModel();
            return;
        }

        if (vehicle.isDisassembled()) {
            final VehicleItemType type = VehicleItemType.getType(itemStack.getItemMeta());
            if (holder.hasTire() && (type == VehicleItemType.AIR_GUN)) {
                this.takeWheelOff(player, vehicle, type, itemStack, holder);
                return;
            }else{
                final VehiclePart vehiclePart = VehiclePart.fromItem(itemStack);
                if (vehiclePart != null) {
                    this.installWheel(player, vehicle, itemStack, vehiclePart, holder);
                    return;
                }
            }

            final VehicleTireData data = vehicle.getVehicleType().getTireOffsetList().get(index);
            if (type == VehicleItemType.WRENCH) {
                new TiresVehicleGUI(vehicle, holder).show(player);
                return;
            }

            if (data.side().isFront() && type == VehicleItemType.RATCHET) {
                final VehicleRatchet ratchet = VehicleRatchet.fromItem(itemStack);
                if (ratchet == null) {
                    player.sendMessage(ColorUtils.color(VehiclesMessages.NEEDS_RATCHET.get()));
                    return;
                }

                if (ratchet.getRatchetMode() == VehicleRatchet.RatchetMode.NORMAL) {
                    if (!this.hasItem(player, VehicleItemType.SOCKET)) {
                        player.sendMessage(ColorUtils.color(VehiclesMessages.NEEDS_SOCKET.get()));
                        return;
                    }

                    new TiresVehicleGUI(vehicle, holder).show(player);
                    return;
                }

                return;
            }

            player.sendMessage(ColorUtils.color(VehiclesMessages.NEEDS_WRENCH.get()));
        }
    }

    private void handleMainHitbox(Player player, Vehicle vehicle, ItemStack itemStack) {
        if (player.isSneaking()) {
            if (vehicle.isJackLift()) {
                if (vehicle.getTires().stream().anyMatch(VehicleWheelHolder::isJackStand)) {
                    player.sendMessage(ColorUtils.color(VehiclesMessages.REMOVE_JACK_STANDS_FIRST.get()));
                    return;
                }

                vehicle.setJackLift(false);
                player.getInventory().addItem(VehicleItemType.JACK_LIFT.getItemStack().clone());
            }else if (!vehicle.isDisassembled()) {
                VehicleLift liftToUse = null;
                for (VehicleLift lift : WegaCars.INSTANCE.getVehicleLiftManager().getSet()) {
                    final Location liftLocation = lift.getLiftLocation();
                    final int maxDistance = VehiclesConfig.MAX_LIFT_DISTANCE.get();

                    if (vehicle.getEntity().getLocation().distance(liftLocation) > maxDistance) continue;

                    liftToUse = lift;
                }

                if (liftToUse == null) {
                    player.sendMessage(ColorUtils.color(VehiclesMessages.NO_NEARBY_LIFTS.get()));
                    return;
                }

                if (liftToUse.hasVehicle()) {
                    player.sendMessage(ColorUtils.color(VehiclesMessages.ALREADY_SERVICING.get()));
                    return;
                }

                vehicle.setDisassembled(true);

                liftToUse.setCurrentVehicle(vehicle);
                vehicle.setCurrentLift(liftToUse);
            }else{
                final VehicleLift lift = vehicle.getCurrentLift();
                if (lift != null) {
                    vehicle.setCurrentLift(null);
                    lift.setCurrentVehicle(null);
                }

                vehicle.setDisassembled(false);
            }

            return;
        }

        if (!vehicle.isJackLift() && itemStack.getType() != Material.AIR && VehicleItemType.getType(itemStack.getItemMeta()) == VehicleItemType.JACK_LIFT) {
            vehicle.setJackLift(true);
            itemStack.setAmount(itemStack.getAmount() - 1);
            vehicle.createBaseModel();
            return;
        }

        final VehicleItemType itemType = VehicleItemType.getType(itemStack.getItemMeta());
        if (itemType == VehicleItemType.FUEL_CAN) {
            vehicle.setFuelRemaining(vehicle.getMaxFuel());
            if (itemStack.getAmount() == 1)
                player.getInventory().setItemInMainHand(null);
            else
                itemStack.setAmount(itemStack.getAmount() - 1);

            player.sendMessage(ColorUtils.color(VehiclesMessages.REFUELED.get()));
            return;
        }

        if (vehicle.isDisassembled() || !vehicle.isFullyAssembled()) {
            player.sendMessage(ColorUtils.color(VehiclesMessages.CANNOT_DRIVE_DISASSEMBLED.get()));
            return;
        }

        final PlayerVehicleClickEvent event = new PlayerVehicleClickEvent(player, vehicle);
        event.callEvent();
        if (event.isCancelled()) return;

        WegaCars.INSTANCE.getTaskScheduler().schedule(Tasks.sync(WegaCars.INSTANCE, ctx -> vehicle.mount(player)));
    }

    private void handleTrunk(Player player, Vehicle vehicle, ItemStack itemStack, VehicleRepairPart part) {
        final VehicleRatchet ratchet = VehicleRatchet.fromItem(itemStack);
        if (ratchet == null) {
            player.sendMessage(ColorUtils.color(VehiclesMessages.NEEDS_RATCHET.get()));
            return;
        }

        if (ratchet.getRatchetMode() == VehicleRatchet.RatchetMode.SPARKPLUG) {
            if (!this.hasItem(player, VehicleItemType.SPARKPLUG_SOCKET)) {
                player.sendMessage(ColorUtils.color(VehiclesMessages.NEEDS_SOCKET.get()));
                return;
            }

            new SparkplugVehicleGUI(vehicle).show(player);
            return;
        }

        if (!this.hasItem(player, VehicleItemType.SOCKET)) {
            player.sendMessage(ColorUtils.color(VehiclesMessages.NEEDS_SOCKET.get()));
            return;
        }

        if (part == VehicleRepairPart.TRUNK)
            new TrunkVehicleGUI(vehicle).show(player);
        else
            new EngineVehicleGUI(vehicle).show(player);

    }

    private void installWheel(Player player, Vehicle vehicle, ItemStack itemStack, VehiclePart part, VehicleWheelHolder holder) {
        if (part == null || part.getItemType() != VehiclePartType.WHEELS) return;

        final int durability = VehiclePart.getDurability(itemStack);
        if (durability <= 0) {
            player.sendMessage(ColorUtils.color(VehiclesMessages.PART_BROKEN.get()));
            return;
        }

        holder.getWheelParts().put(VehicleWheelPart.TIRE, new VehicleItemHolder(part, VehiclePart.getDurability(itemStack)));
        vehicle.createBaseModel();
        itemStack.setAmount(itemStack.getAmount() - 1);

        WegaCars.INSTANCE.getSounds().get("tire-installed").play(player);
    }

    private void takeWheelOff(Player player, Vehicle vehicle, VehicleItemType vehicleItemType, ItemStack vehicleItem, VehicleWheelHolder holder) {
        if (vehicleItemType == VehicleItemType.AIR_GUN) {
            if (vehicle.isJackLift()) {
                player.sendMessage(ColorUtils.color(VehiclesMessages.CANNOT_USE_IN_JACKLIFT.get()));
                return;
            }

            if (!this.hasItem(player, VehicleItemType.IMPACT_SOCKET)) {
                player.sendMessage(ColorUtils.color(VehiclesMessages.NEEDS_IMPACT_SOCKET.get()));
                return;
            }
        }

        final VehicleItemHolder part = holder.getWheelParts().remove(VehicleWheelPart.TIRE);
        player.getInventory().addItem(part.getItemStack());
        vehicle.createBaseModel();

        WegaCars.INSTANCE.getSounds().get("tire-removed").play(player);
    }
    
    private boolean hasItem(Player player, VehicleItemType type) {
        for (ItemStack itemStack : player.getInventory().getContents()) {
            if (itemStack == null || itemStack.getType() == Material.AIR) continue;
            final VehicleItemType itemType = VehicleItemType.getType(itemStack.getItemMeta());
            if (itemType != type) continue;

            return true;
        }

        return false;
    }
}
