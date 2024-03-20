package com.pintel.constants;

import com.pintel.exception.CommandNotFoundException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BotCommandEnum {
    START("start"),
    HELP("help"),
    MAKE_SELECTION("make_selection"),

    SEND_NEWSLETTER("send_newsletter");

    private final String commandName;

    public String getCommandName() {
        return "/" + this.commandName;
    }

    public static BotCommandEnum getCommand(String inputText) throws CommandNotFoundException {
        if (inputText == null) {
            return null;
        }
        for (BotCommandEnum command : BotCommandEnum.values()) {
            if (inputText.equals(command.getCommandName())) {
                return command;
            }
        }
        if (inputText.startsWith("/")) {
            throw new CommandNotFoundException(inputText);
        } else {
            return null;
        }
    }
}
