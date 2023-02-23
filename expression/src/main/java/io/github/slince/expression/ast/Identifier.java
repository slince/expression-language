package io.github.slince.expression.ast;

import io.github.slince.expression.Context;
import io.github.slince.expression.Position;
import lombok.Getter;

@Getter
public class Identifier extends Expr {

    private final String ident;

    public Identifier(String ident, Position position) {
        this.ident = ident;
        this.position = position;
    }

    @Override
    public Object evaluate(Context context) {
        return ident;
    }
}
