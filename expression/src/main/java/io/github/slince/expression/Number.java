package io.github.slince.expression;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Number {

    private final boolean isFloat;

    private final String literal;
}
