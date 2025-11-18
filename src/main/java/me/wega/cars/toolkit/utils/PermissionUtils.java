package me.wega.cars.toolkit.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.permissions.Permissible;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;

@UtilityClass
public class PermissionUtils {

    /**
     * Get the amount from a permission node.
     * Example: mlp.player.homes.5 would return 5 if the permissionRoot is "mlp.player.homes"
     *
     * @param player         The player to check the permissions of
     * @param permissionRoot The root of the permission to check
     * @param defaultVal     The default value to return if no permission is found
     * @return The amount from the permission node or the default value if no permission is found
     */
    public static int getAmount(@NotNull Permissible player, @NotNull String permissionRoot, int defaultVal) {
        return player.getEffectivePermissions().stream()
                .map(PermissionAttachmentInfo::getPermission)
                .filter(perm -> perm.startsWith(permissionRoot))
                .map(perm -> {
                    try {
                        return Integer.parseInt(perm.substring(perm.lastIndexOf(".") + 1));
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        return 0;
                    }
                })
                .findFirst()
                .orElse(defaultVal);
    }
}
