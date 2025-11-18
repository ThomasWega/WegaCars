package me.wega.cars.toolkit.gui.impl;

import me.wega.cars.WegaCars;
import me.wega.cars.toolkit.task.TaskContext;
import me.wega.cars.toolkit.task.TaskScheduler;
import me.wega.cars.toolkit.task.Tasks;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

@Getter
public abstract class StorageGUI implements Listener {
    private final JavaPlugin plugin;
    private final TaskScheduler scheduler;
    private final Inventory inv;
    private final Queue<Long> updateQueue = new ArrayDeque<>();
    private final TaskContext updateTaskCtx;
    private ItemStack[] previousContents;
    // identifies if the task should be canceled on the next run
    private boolean cancelOnNext;

    public StorageGUI(@NotNull JavaPlugin plugin,
                      @NotNull TaskScheduler scheduler,
                      @Nullable InventoryHolder holder,
                      int size,
                      @NotNull Component title,
                      @NotNull HumanEntity ent) {
        this.plugin = plugin;
        this.scheduler = scheduler;
        this.inv = Bukkit.createInventory(holder, size, title);
        this.updateTaskCtx = this.runUpdateTask();
        Bukkit.getPluginManager().registerEvents(this, WegaCars.INSTANCE);
        ent.openInventory(inv);
    }

    private TaskContext runUpdateTask() {
        return scheduler.schedule(Tasks.timer(plugin, 0L, 1L, ctx -> {
            if (this.getRealViewers().isEmpty()) {
                HandlerList.unregisterAll(this);
                ctx.cancel();
                return;
            }

            if (previousContents != null && Arrays.equals(previousContents, inv.getContents())) return;

            while (!updateQueue.isEmpty()) {
                updateQueue.poll();
                this.onContentChange(inv.getContents());
            }

            if (cancelOnNext) {
                HandlerList.unregisterAll(this);
                ctx.cancel();
            }
        }));
    }

    private List<HumanEntity> getRealViewers() {
        return inv.getViewers().stream()
                .filter(entity -> entity.getOpenInventory().getTopInventory().equals(inv))
                .toList();
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onInvClose(InventoryCloseEvent event) {
        if (!event.getInventory().equals(this.getInv())) return;
        // is done to not bulk up the listeners on each instance
        HandlerList.unregisterAll(this);
        cancelOnNext = true;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory eventInv = e.getInventory();
        PlayerInventory playerInv = player.getInventory();

        if (!eventInv.equals(inv) && !eventInv.equals(playerInv)) return;

        this.previousContents = inv.getContents().clone();
        this.updateQueue.add(System.currentTimeMillis());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        Inventory eventInv = e.getInventory();
        PlayerInventory playerInv = player.getInventory();
        // InventoryAction action = e.getAction();

        if (!eventInv.equals(inv) && !eventInv.equals(playerInv)) return;

        // OLD IMPLEMENTATION WHICH WORKED KIND OF, BUT HAD EDGE CASES AND WAS NOT FULLY RELIABLE
//        int slot = e.getSlot();
//
//        ItemStack cursor = e.getCursor();
//        ItemStack current = e.getCurrentItem();
//
//        ItemStack[] items = inv.getContents().clone();
//        if (eventInv.equals(inv)) {
//            switch (action) {
//                case MOVE_TO_OTHER_INVENTORY, DROP_ALL_SLOT -> items[slot] = null;
//                case PICKUP_HALF -> {
//                    ItemStack item = items[slot].clone();
//                    item.setAmount(item.getAmount() / 2);
//                    items[slot] = item;
//                }
//                case PICKUP_ONE, DROP_ONE_SLOT -> {
//                    ItemStack item = items[slot].clone();
//                    item.setAmount(item.getAmount() - 1);
//                    items[slot] = item;
//                }
//                case PLACE_ONE -> {
//                    if (items[slot] == null) {
//                        ItemStack cursorOne = cursor.clone();
//                        cursorOne.setAmount(1);
//                        items[slot] = cursorOne;
//                    } else {
//                        ItemStack item = items[slot].clone();
//                        item.setAmount(item.getAmount() + 1);
//                        items[slot] = item;
//                    }
//                }
//                case HOTBAR_SWAP -> {
//                    ItemStack hotbarItem = playerInv.getItem(e.getHotbarButton());
//                    items[slot] = hotbarItem;
//                }
//                case NOTHING, UNKNOWN, CLONE_STACK -> {
//                }
//                default -> items[slot] = cursor;
//            }
//        } else if (eventInv.equals(playerInv)) {
//            if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY)
//                items = InvUtils.merge(items, current);
//        }
//
//        this.onContentChange(items);

        this.previousContents = inv.getContents().clone();
        this.updateQueue.add(System.currentTimeMillis());
    }

    public abstract void onContentChange(@Nullable ItemStack @NotNull [] newItems);
}
