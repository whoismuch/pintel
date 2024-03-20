package com.pintel.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BotMessageEnum {
    EXCEPTION_ILLEGAL_MESSAGE("Нет, к такому меня не готовили!"),
    EXCEPTION_STH_GOES_WRONG_MESSAGE("Что-то пошло не так."),
    HELP_MESSAGE("""
            Привет, это бот для поиска похожих картинок и удаления вотермарок.
            Он поддерживает следующие команды:
            /help - вывести это описание,
            /start - начать общение с ботом,
            /make_selection - запросить подборку по картинке.
            """),
    LOAD_IMAGE("А теперь отправьте картинку, чтобы получить подборку"),
    CHOOSE_SELECTION_TYPE("Выберите, по какому критерию вы хотите получить подборку."),
    CHOOSE_TYPE_COLOR("По цвету"),
    CHOOSE_TYPE_CONCEPT("По содержанию"),
    RESULT_SELECTION("Вот, что я подобрал");

    private final String text;
}
