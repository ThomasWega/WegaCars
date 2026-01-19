package me.wega.cars.vehicle.transmission.tasks;

import me.wega.cars.VehiclesConfig;
import me.wega.cars.vehicle.Vehicle;
import me.wega.cars.toolkit.date.CustomTimeUnit;
import me.wega.cars.toolkit.task.AbstractTask;
import me.wega.cars.toolkit.task.TaskConfiguration;
import me.wega.cars.toolkit.task.TaskContext;
import me.wega.cars.toolkit.utils.ColorUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import static me.wega.cars.WegaCars.INSTANCE;

public class ManualTransmissionMinigame extends AbstractTask {

    private final @NotNull Vehicle vehicle;
    private final @NotNull Player player;
    private final int randomSlot;
    private int currentTime;

    private @NotNull Result result = Result.UNDECIDED;
    public ManualTransmissionMinigame(@NotNull Vehicle vehicle, @NotNull Player player) {
        super(INSTANCE);
        this.vehicle = vehicle;
        this.player = player;
        this.randomSlot = this.generateSlot();
    }

    private int generateSlot() {
        int randomSlot = ThreadLocalRandom.current().nextInt(9);
        if (randomSlot == player.getInventory().getHeldItemSlot()) {
            return this.generateSlot();
        }

        return randomSlot;
    }

    @Override
    protected @NotNull TaskConfiguration createConfiguration() {
        return TaskConfiguration.builder()
                .interval(1L)
                .timeUnit(CustomTimeUnit.SECONDS)
                .repetitions(VehiclesConfig.MANUAL_BAR_LENGTH.get().longValue())
                .continueCondition(() -> this.result == Result.UNDECIDED && this.vehicle.hasController())
                .build();
    }

    @Override
    public void execute(@NotNull TaskContext ctx) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i <= 8; i++) {
            builder.append(getCharacter(i));
        }

        this.player.showTitle(Title.title(
                ColorUtils.color(builder.toString()),
                ColorUtils.color(VehiclesConfig.MANUAL_BAR_SUBTITLE.get(),
                        Placeholder.parsed("slot", (randomSlot + 1) + "")),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(3), Duration.ZERO)));
        this.currentTime++;
    }

    public void checkSlot(int slot) {
        if (this.result != Result.UNDECIDED) return;

        if (this.currentTime % 2 == 0 || slot != this.randomSlot) {
            this.result = Result.FAILED;
            return;
        }

        this.result = Result.SUCCESS;
    }

    @Override
    public void onComplete(@NotNull TaskContext context, @NotNull CompletionReason reason) {
        if (reason == CompletionReason.REPETITIONS_REACHED) // max repetitions completed, so the player failed minigame
            this.result = Result.TIMED_OUT;

        if (this.vehicle.hasController()) {
            INSTANCE.getTaskScheduler().schedule(new ManualTransmissionBoostTask(this.vehicle, this.result == Result.SUCCESS ? +1 : -3));
            INSTANCE.getSounds().get(this.result == Result.SUCCESS ? "boost-success" : "boost-failure").play(this.player);
            this.vehicle.getController().setManualTransmissionMinigame(null);
        }
    }

    private @NotNull String getCharacter(int index) {
        if (currentTime == index)
            return VehiclesConfig.MANUAL_BAR_CURRENT_COLOR.get() + VehiclesConfig.MANUAL_BAR_CHARACTER.get();

        return (index % 2 == 0 ? VehiclesConfig.MANUAL_BAR_CORRECT_COLOR.get() : VehiclesConfig.MANUAL_BAR_INCORRECT_COLOR.get())
                + VehiclesConfig.MANUAL_BAR_CHARACTER.get();
    }

    enum Result {
        UNDECIDED, FAILED, TIMED_OUT, SUCCESS
    }
}
