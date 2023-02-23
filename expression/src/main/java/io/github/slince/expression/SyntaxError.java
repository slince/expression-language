package io.github.slince.expression;

import lombok.Getter;

@Getter
public class SyntaxError extends RuntimeException {

    /**
     * Error position.
     */
    private final Position position;

    public SyntaxError(String message, Position position){
        super(message);
        this.position = position;
    }
}
