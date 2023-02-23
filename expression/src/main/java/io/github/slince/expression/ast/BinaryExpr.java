package io.github.slince.expression.ast;

import io.github.slince.expression.Arithmetic;
import io.github.slince.expression.Context;
import io.github.slince.expression.EvaluationException;
import io.github.slince.expression.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BinaryExpr extends Expr {

    private final String operator;

    private Expr lhs;

    private Expr rhs;

    @Override
    public Position getPosition() {
        return lhs.getPosition();
    }

    @Override
    public void visit(Visitor visitor) {
        lhs = (Expr) visitor.visit(lhs);
        rhs = (Expr) visitor.visit(rhs);
    }

    @Override
    public Object evaluate(Context context) {
        Object left = lhs.evaluate(context);
        Object right = rhs.evaluate(context);
        return doBinaryOperator(operator, left, right);
    }

    protected static Object doBinaryOperator(String operator, Object left, Object right){
        Object result;
        switch (operator) {
            // Arithmetic
            case "+":
                result = Arithmetic.INSTANCE.add(left, right);
                break;
            case "-":
                result = Arithmetic.INSTANCE.subtract(left, right);
                break;
            case "*":
                result = Arithmetic.INSTANCE.multiply(left, right);
                break;
            case "/":
                result = Arithmetic.INSTANCE.divide(left, right);
                break;
            case "%":
                result = Arithmetic.INSTANCE.mod(left, right);
                break;
            // Logical
            case "==":
                result = Arithmetic.INSTANCE.equals(left, right);
                break;
            case "!=":
                result = !Arithmetic.INSTANCE.equals(left, right);
                break;
            case ">":
                result = Arithmetic.INSTANCE.greaterThan(left, right);
                break;
            case ">=":
                result = Arithmetic.INSTANCE.greaterThanOrEqual(left, right);
                break;
            case "<":
                result = Arithmetic.INSTANCE.lessThan(left, right);
                break;
            case "<=":
                result = Arithmetic.INSTANCE.lessThanOrEqual(left, right);
                break;
            // Bitwise
            case "|":
                result = Arithmetic.INSTANCE.or(left, right);
                break;
            case "^":
                result = Arithmetic.INSTANCE.xor(left, right);
                break;
            case "&":
                result = Arithmetic.INSTANCE.and(left, right);
                break;
            case "<<":
                result = Arithmetic.INSTANCE.shl(left, right);
                break;
            case ">>":
                result = Arithmetic.INSTANCE.shr(left, right);
                break;
            default:
                throw new EvaluationException(String.format("Invalid operator %s", operator));
        }
        return result;
    }
}
