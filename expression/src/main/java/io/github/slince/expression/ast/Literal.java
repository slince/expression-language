package io.github.slince.expression.ast;

import io.github.slince.expression.Context;
import io.github.slince.expression.Position;
import lombok.Getter;

@Getter
public class Literal extends Expr{

    private final String raw;
    private final String type;
    private final Object value;

    public Literal(String type, String raw, Object value, Position position) {
        this.type = type;
        this.raw = raw;
        this.value = value;
        this.position = position;
    }

    @Override
    public Object evaluate(Context context) {
        return value;
    }
}
