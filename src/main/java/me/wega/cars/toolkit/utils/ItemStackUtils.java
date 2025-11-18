package me.wega.cars.toolkit.utils;

import de.tr7zw.nbtapi.NBT;
import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@UtilityClass
public class ItemStackUtils {

    /**
     * Merges a list of ItemStacks into as few stacks as possible, respecting max stack sizes.
     * Similar items are combined up to their max stack size.
     *
     * @param inputItems the list of ItemStacks to merge
     * @return a new list of merged ItemStacks
     */
    public static @NotNull List<@NotNull ItemStack> merge(@NotNull List<ItemStack> inputItems) {
        final List<ItemStack> merged = new ArrayList<>();
        for (final ItemStack original : inputItems) {
            if (original == null) continue;

            final ItemStack item = original.clone();
            boolean mergedFlag = false;
            for (final ItemStack existing : merged) {
                if (existing.isSimilar(item) && existing.getAmount() < existing.getMaxStackSize()) {
                    final int total = existing.getAmount() + item.getAmount();
                    final int max = existing.getMaxStackSize();
                    if (total <= max) {
                        existing.setAmount(total);
                        mergedFlag = true;
                    } else {
                        existing.setAmount(max);
                        item.setAmount(total - max);
                        // Continue merging the leftover amount
                    }
                    if (mergedFlag) break;
                }
            }
            if (!mergedFlag)
                merged.add(item.clone());
        }
        final List<ItemStack> result = new ArrayList<>();
        for (final ItemStack stack : merged) {
            int amount = stack.getAmount();
            final int max = stack.getMaxStackSize();
            while (amount > max) {
                final ItemStack split = stack.clone();
                split.setAmount(max);
                result.add(split);
                amount -= max;
            }
            if (amount > 0) {
                final ItemStack split = stack.clone();
                split.setAmount(amount);
                result.add(split);
            }
        }
        return result;
    }

    /**
     * Splits a collection of ItemStacks into individual ItemStacks, each with an amount of 1.
     *
     * @param stacks the collection of ItemStacks to split
     * @return a list of individual ItemStacks, each with an amount of 1
     */
    public @NotNull List<@NotNull ItemStack> split(@NotNull Collection<@Nullable ItemStack> stacks) {
        List<ItemStack> resultList = new ArrayList<>();

        for (ItemStack stack : stacks) {
            if (stack == null) continue;
            ItemStack clone = stack.clone();
            int amount = clone.getAmount();
            clone.setAmount(1);
            for (int i = 0; i < amount; i++)
                resultList.add(clone.clone());
        }

        return resultList;
    }

    /**
     * Converts an array of ItemStacks to a NBT string
     * @param itemStacks the ItemStack array to convert
     * @return the NBT string
     */
    public static @NotNull String toNBTStringArray(@Nullable ItemStack @NotNull [] itemStacks) {
        return NBT.itemStackArrayToNBT(itemStacks).toString();
    }

    /**
     * Converts an ItemStack to a NBT string
     * @param itemStack the ItemStack to convert
     * @return the NBT string
     * @see #toNBTStringArray(ItemStack[])
     */
    public static @NotNull String toNBTString(@NotNull ItemStack itemStack) {
        return NBT.itemStackToNBT(itemStack).toString();
    }

    /**
     * Converts a NBT string to an array of ItemStacks
     * @param itemString the NBT string to convert
     * @return the ItemStack array
     */
    public static @Nullable ItemStack fromNBTString(@NotNull String itemString) {
        return NBT.itemStackFromNBT(NBT.parseNBT(itemString));
    }

    /**
     * Converts a NBT string to an array of ItemStacks
     * @param itemString the NBT string to convert
     * @return the ItemStack array
     */
    public static @Nullable ItemStack @Nullable [] fromNBTStringArray(@NotNull String itemString) {
        return NBT.itemStackArrayFromNBT(NBT.parseNBT(itemString));
    }

    /**
     * Gets the display name of an ItemStack.
     * If the ItemStack has a display name, it will return that. Otherwise, it will
     * return the name of the item type with spaces between the words.
     *
     * @param item the ItemStack to get the display name of
     * @return the display name of the ItemStack
     */
    public static @NotNull Component getDisplayName(@NotNull ItemStack item) {
        if(item.getItemMeta().hasDisplayName()) return
                item.getItemMeta().displayName();
        String[] datas = item.getType().name().split("_");
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < datas.length; i++) {
            String data = datas[i].charAt(0) + datas[i].substring(1).toLowerCase();
            builder.append(i == 0? data : " " + data);
        }
        return Component.text(builder.toString());
    }
}
