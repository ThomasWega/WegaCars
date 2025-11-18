package me.wega.cars.toolkit.command.arg;

import me.wega.cars.toolkit.utils.NumberUtils;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.jetbrains.annotations.NotNull;

public class FormattedLongArgument extends CustomArgument<Long, String> {

    public FormattedLongArgument(@NotNull String nodeName) {
        super(new StringArgument(nodeName), info -> {
            try {
                return NumberUtils.parseFormattedLong(info.input());
            } catch (IllegalArgumentException exception) {
                throw CustomArgumentException.fromString("Invalid value! Please enter a valid number: " + info.input());
            }
        });

        this.replaceSuggestions(ArgumentSuggestions.empty());
    }
}
