package io.github.slince.expression.ast;

import io.github.slince.expression.Arithmetic;
import io.github.slince.expression.Context;
import io.github.slince.expression.EvaluationException;
import io.github.slince.expression.Position;
import lombok.Getter;

@Getter
public class UnaryExpr extends Expr {

    private final String operator;

    private Expr argument;

    public UnaryExpr(String operator, Expr argument, Position position) {
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
        Object result;
        switch (operator) {
            // Logical
            case "!":
                result = Arithmetic.INSTANCE.logicNot(value);
                break;
            case "~":
                result = Arithmetic.INSTANCE.not(value);
                break;
            case "+":
                result = value;
                break;
            case "-":
                result = Arithmetic.INSTANCE.negate(value);
                break;
            default:
                throw new EvaluationException(String.format("Invalid operator %s", operator));
        }
        return result;
    }
}
