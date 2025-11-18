package me.wega.cars.toolkit.command.arg;

import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import lombok.Getter;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

@Getter
public class EnumArgument<E extends Enum<E>> extends CustomArgument<E, String> {

    private final Class<E> enumClass;

    public EnumArgument(@NotNull Class<E> enumClass, @NotNull E... filterOut) {
        super(new StringArgument(StringUtils.uncapitalise(enumClass.getSimpleName())), info -> {
            try {
                E eEnum =  Enum.valueOf(enumClass, info.input().toUpperCase());
                if (!Arrays.asList(filterOut).contains(eEnum))
                    return eEnum;

            } catch (IllegalArgumentException e) {
                throw CustomArgumentException.fromString("Invalid value for enum " + enumClass.getSimpleName() + ": " + info.input());
            }
            throw CustomArgumentException.fromString("Invalid value for enum " + enumClass.getSimpleName() + ": " + info.input());
        });

        this.replaceSuggestions(ArgumentSuggestions.stringCollectionAsync(info ->
                CompletableFuture.completedFuture(
                        Arrays.stream(enumClass.getEnumConstants())
                                .map(Enum::name)
                                .filter(name -> !Arrays.asList(filterOut).contains(Enum.valueOf(enumClass, name)))
                                .map(String::toLowerCase)
                                .toList()
                )
        ));

        this.enumClass = enumClass;
    }
}
