package me.wega.cars.toolkit.command.arg;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

public class BigIntegerArgument extends CustomArgument<BigInteger, String> {

    public BigIntegerArgument(@NotNull String nodeName) {
        super(new StringArgument(nodeName), info -> {
            try {
                return new BigInteger(info.input());
            } catch (NumberFormatException e) {
                throw CustomArgumentException.fromString("Invalid value! Please enter a valid number." + info.input());
            }
        });

        this.replaceSuggestions(ArgumentSuggestions.empty());
    }
}
