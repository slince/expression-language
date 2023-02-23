package io.github.slince.expression;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Position {

    /**
     * Offset.
     */
    private final Integer offset;

    /**
     * Line no.
     */
    private final Integer line;

    /**
     * Column no.
     */
    private final Integer column;
}
