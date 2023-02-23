package io.github.slince.expression.fixtures;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Student {
    private final String name;
    private final String description;
    private final Integer years;
    private final Group group;
    private final ClassRoom room;
}