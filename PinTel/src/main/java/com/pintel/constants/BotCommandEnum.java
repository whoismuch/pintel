package com.pintel.constants;

import com.pintel.exception.CommandNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public enum BotCommandEnum {
    START("start"),
    HELP("help"),
    MAKE_SELECTION("make_selection", List.of(ButtonTextEnum.SELECTION.getText())),

    SEND_NEWSLETTER("send_newsletter");

    private final String commandName;
    private final List<String> variations;

    BotCommandEnum(String name) {
        this.commandName = name;
        this.variations = List.of();
    }

    public String getCommandName() {
        return "/" + this.commandName;
    }

    public static BotCommandEnum getCommand(String inputText) throws CommandNotFoundException {
        if (inputText == null) {
            return null;
        }
        for (BotCommandEnum command : BotCommandEnum.values()) {
            if (inputText.equals(command.getCommandName()) ||
                    (!command.variations.isEmpty() && command.variations.stream()
                            .anyMatch(name -> inputText.equals(name.toLowerCase())))) {
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
