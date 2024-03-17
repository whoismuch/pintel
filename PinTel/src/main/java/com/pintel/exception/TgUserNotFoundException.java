package com.pintel.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TgUserNotFoundException extends RuntimeException {
    final Logger logger = LoggerFactory.getLogger(TgUserNotFoundException.class);
    public TgUserNotFoundException(String message) {
        super("Пользователь с ником " + message + " не найден");
        logger.warn("Пользователь с ником " + message + " не найден");
    }
}
