package me.wega.cars;

import com.google.gson.Gson;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import lombok.Getter;
import me.wega.cars.toolkit.data.DataSavingManager;
import me.wega.cars.toolkit.task.TaskScheduler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.Objects;

@Getter
public final class WegaCars extends JavaPlugin {
    public static WegaCars INSTANCE;
    private final TaskScheduler taskScheduler = new TaskScheduler();
    private final DataSavingManager dataSavingManager = new DataSavingManager();
    private final Gson gson = new Gson();

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
    }

    @Override
    public void onDisable() {
        CommandAPI.onDisable();
        taskScheduler.cancelAllTasks();
    }
}
