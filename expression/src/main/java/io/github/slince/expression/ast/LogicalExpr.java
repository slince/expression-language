package io.github.slince.expression.ast;

import io.github.slince.expression.Arithmetic;
import io.github.slince.expression.Context;
import io.github.slince.expression.EvaluationException;
import lombok.Getter;

import java.util.Objects;

@Getter
public class LogicalExpr extends BinaryExpr {

    public LogicalExpr(String operator, Expr lhs, Expr rhs) {
        super(operator, lhs, rhs);
    }

    @Override
    public Object evaluate(Context context) {
        Object left = getLhs().evaluate(context);
        return doLogicalOperator(getOperator(), left, getRhs(), context);
    }

    protected static Object doLogicalOperator(String operator, Object left, Expr rhs, Context context){
        // 逻辑且和逻辑或不一定需要用到right
        switch (operator) {
            case "||":
                if (Arithmetic.INSTANCE.toBoolean(left)) {
                    return Boolean.TRUE;
                }
                return Arithmetic.INSTANCE.toBoolean(rhs.evaluate(context));
            case "&&":
                if (!Arithmetic.INSTANCE.toBoolean(left)) {
                    return Boolean.FALSE;
                }
                return Arithmetic.INSTANCE.toBoolean(rhs.evaluate(context));
            case "??":
                if (Objects.nonNull(left)) {
                    return left;
                }
                return rhs.evaluate(context);
            default:
                throw new EvaluationException(String.format("Invalid operator %s", operator));
        }
    }
}
