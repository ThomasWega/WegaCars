package me.wega.cars.cmd;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import me.wega.cars.WegaCars;
import me.wega.cars.cmd.arg.VehicleItemArgument;
import me.wega.cars.cmd.arg.VehicleTypeArgument;
import me.wega.cars.item.VehicleItemType;
import me.wega.cars.item.lift.VehicleLift;
import me.wega.cars.item.part.VehiclePart;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.vehicle.type.VehicleType;
import me.wega.cars.toolkit.command.arg.EnumArgument;
import me.wega.cars.toolkit.utils.ColorUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class AdminVehiclesCommand extends CommandAPICommand {

    public AdminVehiclesCommand() {
        super("adminvehicles");
        this.withPermission("hrp.admin.vehicles")
                .withSubcommand(new CommandAPICommand("spawn")
                        .withArguments(
                                new VehicleTypeArgument()
                        )
                        .executesPlayer(this::spawnVehicle)
                )
                .withSubcommand(new CommandAPICommand("give")
                        .withSubcommand(new CommandAPICommand("part")
                                .withArguments(
                                        new VehicleItemArgument(),
                                        new PlayerArgument("target").setOptional(true),
                                        new IntegerArgument("amount").setOptional(true)
                                ).executes(this::spawnPart))
                        .withSubcommand(new CommandAPICommand("item")
                                .withArguments(
                                        new EnumArgument<>(VehicleItemType.class),
                                        new PlayerArgument("target").setOptional(true)
                                ).executes(this::spawnItem))
                )
                .withSubcommand(new CommandAPICommand("createlift")
                        .executesPlayer(this::createLift));
    }

    private void spawnVehicle(Player player, CommandArguments args) {
        final VehicleType vehicleType = args.getUnchecked(0);
        assert vehicleType != null;

        Vehicle.createVehicle(UUID.randomUUID(), vehicleType, player.getLocation(), false);
    }

    private void createLift(Player player, CommandArguments args) {
        final VehicleLift vehicleLift = new VehicleLift(player.getLocation());
        WegaCars.INSTANCE.getVehicleLiftManager().add(vehicleLift);

        player.sendMessage(ColorUtils.color("<green>Lift created"));
    }

    private void spawnItem(CommandSender sender, CommandArguments args) {
        final VehicleItemType vehicleItemType = args.getUnchecked(0);

        final CommandSender targetSender = (CommandSender) args.getOptionalUnchecked(1).orElse(sender);
        if (!(targetSender instanceof Player player)) {
            sender.sendMessage(ColorUtils.color("<red>Only players can receive a spring compressor!"));
            return;
        }

        final ItemStack item = vehicleItemType.getItemStack().clone();
        player.getInventory().addItem(item);
        player.sendMessage(ColorUtils.color("<green>Received item"));
    }

    private void spawnPart(CommandSender sender, CommandArguments args) {
        final VehiclePart vehiclePart = args.getUnchecked(0);
        assert vehiclePart != null;

        final CommandSender targetSender = (CommandSender) args.getOptionalUnchecked(1).orElse(sender);
        if (!(targetSender instanceof Player player)) {
            sender.sendMessage(ColorUtils.color("<red>Only players can receive vehicle items!"));
            return;
        }
        final int amount = (int) args.getOptionalUnchecked(2).orElse(1);

        final ItemStack item = vehiclePart.getNewItemStack().clone();
        item.setAmount(amount);
        player.getInventory().addItem(item);
        player.sendMessage(ColorUtils.color("<green>Received vehicle item"));
    }
}
