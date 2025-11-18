package me.wega.cars.toolkit.utils;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Utility class for creating paginated lists of components.
 */
@UtilityClass
public class PaginatedListUtils {

    /**
     * Creates a paginated list of components.
     *
     * @param items The list of items to paginate.
     * @param target The player to whom the list is targeted.
     * @param currentPage The current page number.
     * @param itemsPerPage The number of items per page.
     * @param itemFormatter A function to format each item into a component.
     * @param headerTemplate The template for the header.
     * @param footerTemplate The template for the footer.
     * @param pageButtonTemplate The template for the page buttons.
     * @param selectedPageButtonTemplate The template for the selected page button.
     * @param <T> The type of items in the list.
     * @return A component representing the paginated list.
     */
    public static <T> @NotNull Component createPaginatedList(
            @NotNull List<T> items,
            @NotNull Player target,
            int currentPage,
            int itemsPerPage,
            @NotNull Function<T, @NotNull Component> itemFormatter,
            @NotNull String headerTemplate,
            @NotNull String footerTemplate,
            @NotNull String pageButtonTemplate,
            @NotNull String selectedPageButtonTemplate
    ) {
        final int totalPages = (int) Math.ceil((double) items.size() / itemsPerPage);
        final int startIndex = (currentPage - 1) * itemsPerPage;
        final int endIndex = Math.min(startIndex + itemsPerPage, items.size());

        final List<T> pageItems = items.subList(startIndex, endIndex);

        final Component header = ColorUtils.color(
                headerTemplate,
                Placeholder.parsed("player", target.getName())
        );

        final List<Component> itemList = pageItems.stream()
                .map(itemFormatter)
                .toList();

        final Component content = Component.join(JoinConfiguration.newlines(), itemList);

        final Component footer = createFooter(target, currentPage, totalPages, footerTemplate, pageButtonTemplate, selectedPageButtonTemplate);

        return Component.join(JoinConfiguration.newlines(), header, content, footer);
    }

    /**
     * Creates the footer component for the paginated list.
     *
     * @param target The player to whom the list is targeted.
     * @param currentPage The current page number.
     * @param totalPages The total number of pages.
     * @param footerTemplate The template for the footer.
     * @param pageButtonTemplate The template for the page buttons.
     * @param selectedPageButtonTemplate The template for the selected page button.
     * @return A component representing the footer.
     */
    private static @NotNull Component createFooter(
            @NotNull Player target,
            int currentPage,
            int totalPages,
            @NotNull String footerTemplate,
            @NotNull String pageButtonTemplate,
            @NotNull String selectedPageButtonTemplate
    ) {
        final List<Component> pageButtons = IntStream.rangeClosed(1, totalPages)
                .mapToObj(page -> createPageButton(target, page, currentPage, pageButtonTemplate, selectedPageButtonTemplate))
                .toList();

        final Component pages = Component.join(JoinConfiguration.separator(Component.text(" ")), pageButtons);

        return ColorUtils.color(
                footerTemplate,
                Placeholder.component("pages", pages)
        );
    }

    /**
     * Creates a page button component.
     *
     * @param target The player to whom the list is targeted.
     * @param page The page number for the button.
     * @param currentPage The current page number.
     * @param pageButtonTemplate The template for the page buttons.
     * @param selectedPageButtonTemplate The template for the selected page button.
     * @return A component representing the page button.
     */
    private static @NotNull Component createPageButton(
            @NotNull Player target,
            int page,
            int currentPage,
            @NotNull String pageButtonTemplate,
            @NotNull String selectedPageButtonTemplate
    ) {
        final String template = (page == currentPage) ? selectedPageButtonTemplate : pageButtonTemplate;

        return ColorUtils.color(
                template,
                Placeholder.parsed("player", target.getName()),
                Placeholder.parsed("page", String.valueOf(page))
        );
    }
}