package com.pintel.exception;

public class AnotherServiceException extends Exception {
    public AnotherServiceException(String msg) {
        super("Ошибка на другом сервере. " + msg);
    }
}
