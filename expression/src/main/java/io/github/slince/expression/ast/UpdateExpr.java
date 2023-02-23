package io.github.slince.expression.ast;

import io.github.slince.expression.Arithmetic;
import io.github.slince.expression.Context;
import io.github.slince.expression.ContextChanger;
import io.github.slince.expression.Position;
import lombok.Getter;

@Getter
public class UpdateExpr extends Expr{

    private final boolean prefix;

    private final String operator;

    private Expr argument;

    public UpdateExpr(boolean prefix, String operator, Expr argument, Position position) {
        this.prefix = prefix;
        this.operator = operator;
        this.argument = argument;
        this.position = position;
    }

    @Override
    public void visit(Visitor visitor) {
        argument = (Expr) visitor.visit(argument);
    }

    @Override
    public Object evaluate(Context context) {
        Object value = argument.evaluate(context);
        Object result = null;
        Object changed = null;
        switch (operator) {
            case "++":
                changed = Arithmetic.INSTANCE.add(value, 1);
                result = prefix ? changed : value;
                break;
            case "--":
                changed = Arithmetic.INSTANCE.subtract(value, 1);
                result = prefix ? changed : value;
                break;
        }
        assert argument instanceof ContextChanger;
        ((ContextChanger) argument).setVar(context, changed);
        return result;
    }
}
