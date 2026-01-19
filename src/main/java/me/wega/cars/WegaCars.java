package me.wega.cars;

import com.google.gson.Gson;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import lombok.Getter;
import me.wega.cars.annotation.reflection.AnnotationsReflection;
import me.wega.cars.cmd.AdminVehiclesCommand;
import me.wega.cars.gson.GsonHandler;
import me.wega.cars.item.lift.VehicleLiftManager;
import me.wega.cars.item.lift.tasks.VehicleLiftTask;
import me.wega.cars.item.part.VehiclePartManager;
import me.wega.cars.toolkit.actionbar.ActionBarManager;
import me.wega.cars.toolkit.config.sound.ConfigSounds;
import me.wega.cars.toolkit.data.DataSavingManager;
import me.wega.cars.toolkit.task.TaskScheduler;
import me.wega.cars.toolkit.utils.InitializeStatic;
import me.wega.cars.utils.VehicleEntityLoadHandler;
import me.wega.cars.vehicle.VehicleManager;
import me.wega.cars.vehicle.VehiclePlayerMapManager;
import me.wega.cars.vehicle.type.VehicleTypeManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

@Getter
public final class WegaCars extends JavaPlugin {
    public static WegaCars INSTANCE;
    private final TaskScheduler taskScheduler = new TaskScheduler();
    private final DataSavingManager dataSavingManager = new DataSavingManager();
    private Gson gson;
    private ConfigSounds sounds;
    private final File dataDir = new File(this.getDataFolder(), "data");

    private VehiclePartManager vehiclePartManager;
    private VehicleTypeManager vehicleTypeManager;
    private VehicleManager vehicleManager;
    private VehiclePlayerMapManager vehiclePlayerMapManager;
    private VehicleLiftManager vehicleLiftManager;
    private ActionBarManager actionBarManager;

    @Override
    public void onLoad() {
        INSTANCE = this;
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this)
                .shouldHookPaperReload(true)
        );
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        this.gson = GsonHandler.withAnnotations().create();

        InitializeStatic.initializeAll(VehiclesMessages.class);
        InitializeStatic.initializeAll(VehiclesConfig.class);

        this.initializeManagers();

        this.loadData();
        this.runTasks();
        this.initializeSpawnAreaEntities();

        AnnotationsReflection.registerListeners(this, this.getClass().getPackageName());
        new AdminVehiclesCommand().register();
    }

    @Override
    public void onDisable() {
        this.saveData();
        CommandAPI.onDisable();
        taskScheduler.cancelAllTasks();
    }
    public boolean loadData() {
        return this.sounds.loadAction(true) &&
                this.vehiclePartManager.loadAll() &&
                this.vehicleTypeManager.loadAction(true) &&
                this.vehicleManager.loadAction(false) &&
                this.vehicleLiftManager.loadAction(false);
    }

    public boolean saveData() {
        return this.vehicleManager.saveAction() &&
                this.vehicleLiftManager.saveAction();
    }

    private void initializeManagers() {
        this.actionBarManager = new ActionBarManager(this);
        this.sounds = new ConfigSounds(this, this.dataSavingManager, this.gson, new File(this.getDataFolder(), "sounds.json"));
        this.vehiclePartManager = new VehiclePartManager();
        this.vehicleTypeManager = new VehicleTypeManager();
        this.vehicleManager = new VehicleManager();
        this.vehiclePlayerMapManager = new VehiclePlayerMapManager();
        this.vehicleLiftManager = new VehicleLiftManager();
    }

    private void initializeSpawnAreaEntities() {
        Bukkit.getWorlds().forEach(world -> world.getEntities().forEach(VehicleEntityLoadHandler::handleEntityLoad));
    }


    private void runTasks() {
        this.getTaskScheduler().schedule(new VehicleLiftTask());
    }
}
