package me.wega.cars.item.part.minigames;

import com.github.stefvanschie.inventoryframework.adventuresupport.ComponentHolder;
import com.github.stefvanschie.inventoryframework.gui.GuiItem;
import com.github.stefvanschie.inventoryframework.gui.type.ChestGui;
import com.github.stefvanschie.inventoryframework.pane.StaticPane;
import lombok.Getter;
import me.wega.cars.VehiclesConfig;
import me.wega.cars.VehiclesMessages;
import me.wega.cars.toolkit.builder.ItemBuilder;
import me.wega.cars.toolkit.utils.ColorUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class TuneupMinigame extends ChestGui {

    private static final int GRIDS = 4;

    private final @NotNull Consumer<Integer> completeConsumer;

    private final @NotNull Tile @NotNull [] @NotNull [] board;
    private int border = 0;

    private int remainingTurns = VehiclesConfig.Minigame.MAX_TURNS.get();

    private final StaticPane gamePane = new StaticPane(0, 1, 4, 4);
    private final StaticPane controlsPane = new StaticPane(5, 1, 3, 3);

    public TuneupMinigame(@NotNull Consumer<Integer> completeConsumer) {
        super(6, ComponentHolder.of(ColorUtils.color(VehiclesConfig.Titles.TUNEUP_MINIGAME_TITLE.get())));
        this.board = new Tile[GRIDS][GRIDS];
        for (int i = 0; i < this.board.length; i++)
            for (int j = 0; j < this.board[i].length; j++)
                this.board[i][j] = new Tile();
        this.completeConsumer = completeConsumer;

        this.initialize();
    }

    private void initialize() {
        this.setOnGlobalClick(event -> event.setCancelled(true));

        // 2 tiles to start
        this.spawn();
        this.spawn();

        this.update();
        this.addPane(this.gamePane);

        this.controlsPane.addItem(new GuiItem(VehiclesConfig.Minigame.UP_ITEM.get().build(), this::up), 1, 0);
        this.controlsPane.addItem(new GuiItem(VehiclesConfig.Minigame.DOWN_ITEM.get().build(), this::down), 1, 2);
        this.controlsPane.addItem(new GuiItem(VehiclesConfig.Minigame.LEFT_ITEM.get().build(), this::left), 0, 1);
        this.controlsPane.addItem(new GuiItem(VehiclesConfig.Minigame.RIGHT_ITEM.get().build(), this::right), 2, 1);

        this.addPane(this.controlsPane);
    }

    @Override
    public void update() {
        this.gamePane.clear();
        for (int i = 0; i < this.board.length; i++)
            for (int j = 0; j < this.board[i].length; j++) {
                final Tile tile = this.board[i][j];
                if (tile.getValue() == 0) continue;

                this.gamePane.addItem(new GuiItem(new ItemBuilder(tile.getColor())
                        .displayName(Component.text(tile.getValue() + ""))
                        .build()), j, i);
            }

        super.update();
    }

    private int getHighestTile() {
        int highest = this.board[0][0].getValue();
        for (Tile[] tiles : this.board)
            for (Tile tile : tiles)
                if (tile.getValue() > highest)
                    highest = tile.getValue();

        return highest;
    }

    /**
     * Spawns a 2 at an empty space every time a move is made
     */
    private void spawn() {
        while (true) {
            final int row = ThreadLocalRandom.current().nextInt(GRIDS);
            final int col = ThreadLocalRandom.current().nextInt(GRIDS);
            if (this.board[row][col].getValue() == 0) {
                this.board[row][col] = new Tile(ThreadLocalRandom.current().nextDouble() < 0.2 ? 4 : 2);
                break;
            }
        }
    }

    private void success(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        player.closeInventory();

        player.sendMessage(ColorUtils.color(VehiclesMessages.MINIGAME_SUCCESSFUL.get()));
        this.completeConsumer.accept(this.remainingTurns);
    }

    private void failure(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        player.closeInventory();

        player.sendMessage(ColorUtils.color(VehiclesMessages.MINIGAME_FAILED.get()));
    }

    private void checkConditions(InventoryClickEvent event) {
        if (this.getHighestTile() == 64) {
            this.success(event);
            return;
        }

        this.remainingTurns--;
        if (this.remainingTurns == 0) {
            this.failure(event);
            return;
        }

        this.setTitle(this.remainingTurns + " turns left");
    }

    private void up(InventoryClickEvent event) {
        for (int i = 0; i < GRIDS; i++) {
            this.border = 0;
            for (int j = 0; j < GRIDS; j++)
                if (this.board[j][i].getValue() != 0)
                    if (this.border <= j)
                        verticalMove(j, i, Direction.UP);
        }

        this.spawn();
        this.update();
        this.checkConditions(event);
    }

    private void down(InventoryClickEvent event) {
        for (int i = 0; i < GRIDS; i++) {
            this.border = (GRIDS - 1);
            for (int j = GRIDS - 1; j >= 0; j--)
                if (this.board[j][i].getValue() != 0)
                    if (this.border >= j)
                        verticalMove(j, i, Direction.DOWN);
        }

        this.spawn();
        this.update();
        this.checkConditions(event);
    }

    private void verticalMove(int row, int col, Direction direction) {
        final Tile initial = this.board[border][col];
        final Tile compare = this.board[row][col];
        if (initial.getValue() == 0 || initial.getValue() == compare.getValue()) {
            if (row > this.border || (direction == Direction.DOWN && (row < this.border))) {
                initial.setValue(initial.getValue() + compare.getValue());
                compare.setValue(0);
            }
        } else {
            if (direction == Direction.DOWN)
                this.border--;
            else
                this.border++;

            verticalMove(row, col, direction);
        }
    }

    private void left(InventoryClickEvent event) {
        for (int i = 0; i < GRIDS; i++) {
            this.border = 0;
            for (int j = 0; j < GRIDS; j++)
                if (this.board[i][j].getValue() != 0)
                    if (this.border <= j)
                        horizontalMove(i, j, Direction.LEFT);
        }

        this.spawn();
        this.update();
        this.checkConditions(event);
    }

    private void right(InventoryClickEvent event) {
        for (int i = 0; i < GRIDS; i++) {
            this.border = (GRIDS - 1);
            for (int j = (GRIDS - 1); j >= 0; j--)
                if (this.board[i][j].getValue() != 0)
                    if (this.border >= j)
                        horizontalMove(i, j, Direction.RIGHT);
        }

        this.spawn();
        this.update();
        this.checkConditions(event);
    }

    private void horizontalMove(int row, int col, Direction direction) {
        final Tile initial = this.board[row][border];
        final Tile compare = this.board[row][col];
        if (initial.getValue() == 0 || initial.getValue() == compare.getValue()) {
            if (col > this.border || (direction == Direction.RIGHT && (col < this.border))) {
                initial.setValue(initial.getValue() + compare.getValue());
                compare.setValue(0);
            }
        } else {
            if (direction == Direction.RIGHT)
                this.border--;
            else
                this.border++;

            horizontalMove(row, col, direction);
        }
    }

    @Getter
    static class Tile {
        private int value;
        private @NotNull Material color;

        public Tile() {
            this(0);
        }

        public Tile(int value) {
            this.value = value;
            this.color = this.getColorForValue(this.value);
        }

        public void setValue(int value) {
            this.value = value;
            this.color = this.getColorForValue(this.value);
        }

        public @NotNull Material getColorForValue(int value) {
            if (value == 2) return Material.GRAY_STAINED_GLASS_PANE;
            if (value == 4) return Material.WHITE_STAINED_GLASS_PANE;
            if (value == 8) return Material.YELLOW_STAINED_GLASS_PANE;
            if (value == 16) return Material.ORANGE_STAINED_GLASS_PANE;
            if (value == 32) return Material.RED_STAINED_GLASS_PANE;
            if (value == 64) return Material.GREEN_STAINED_GLASS_PANE;

            return Material.AIR;
        }
    }

    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
}
