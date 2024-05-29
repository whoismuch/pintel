package com.pintel.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ButtonTextEnum {
    SELECTION_BY_COLOR("По цвету"),
    SELECTION_BY_CONCEPT("По содержанию"),
    SELECTION("Рекомендовать");

    private final String text;
}
