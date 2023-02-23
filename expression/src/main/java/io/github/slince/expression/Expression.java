package io.github.slince.expression;

import io.github.slince.expression.ast.Node;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Expression {

    /**
     * Source code.
     */
    private final byte[] source;

    /**
     * Abstract syntax tree node.
     */
    private final Node ast;
}
