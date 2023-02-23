package io.github.slince.expression.ast;

import io.github.slince.expression.Context;
import io.github.slince.expression.ContextChanger;
import io.github.slince.expression.EvaluationException;
import io.github.slince.expression.Position;

public class Reference extends Identifier implements ContextChanger {

    public Reference(String ident, Position position) {
        super(ident, position);
    }

    @Override
    public Object evaluate(Context context) {
        String id = getIdent();
        if (!context.hasVar(id)) {
            throw new EvaluationException(String.format("Undefined reference, \"%s\" is not defined", id));
        }
        return context.getVar(id);
    }

    @Override
    public void setVar(Context context, Object value) {
        context.setVar(getIdent(), value);
    }
}
