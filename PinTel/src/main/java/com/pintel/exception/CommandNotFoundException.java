package com.pintel.exception;

public class CommandNotFoundException extends RuntimeException {
    public CommandNotFoundException(String msg) {
        super("Команда " + msg + " не найдена");
    }
}
