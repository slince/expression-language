package io.github.slince.expression.fixtures;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class Group {
    private final String name;
    private final Float scores;
    private final ClassRoom room;
    private final List<Student> students = new ArrayList<>();
}
