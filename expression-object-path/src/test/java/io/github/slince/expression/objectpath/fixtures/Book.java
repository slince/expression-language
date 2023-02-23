package io.github.slince.expression.objectpath.fixtures;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Book{
    private final String name;
    private final Double price;
    private final User author;
}
