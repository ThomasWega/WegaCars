package me.wega.cars.toolkit.command.arg;

import dev.jorel.commandapi.arguments.CustomArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;

public class InvSizeArgument extends CustomArgument<Integer, String> {

    public InvSizeArgument() {
        super(new MultiLiteralArgument("size", "9", "18", "27", "36", "45", "54"), info -> Integer.parseInt(info.input()));
    }
}