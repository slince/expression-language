package io.github.slince.expression.ast;

import io.github.slince.expression.Context;
import io.github.slince.expression.EvaluationException;
import io.github.slince.expression.Position;
import io.github.slince.expression.PropertyAccessor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class CallExpr extends Expr
{
    private Expr callee;
    private List<Expr> arguments;

    @Override
    public Position getPosition() {
        return callee.getPosition();
    }

    @Override
    public void visit(Visitor visitor) {
        callee = (Expr) visitor.visit(callee);
        arguments = arguments.stream().map(v -> (Expr)visitor.visit(v)).collect(Collectors.toList());
    }

    @Override
    public Object evaluate(Context context) {
        Object[] arguments = this.arguments.stream().map(v -> v.evaluate(context)).toArray();
        if (callee instanceof Identifier) {
            String callee = (String) this.callee.evaluate(context);
            if (!context.hasFunction(callee)) {
                throw new EvaluationException(String.format("The function %s is not found", callee));
            }
            try {
                return context.getFunction(callee).call(arguments);
            } catch (RuntimeException e) {
                throw new EvaluationException(e);
            }
        } else if (callee instanceof MemberExpr){
            MemberExpr converted = (MemberExpr)callee;
            Object object = converted.getObject().evaluate(context);
            String method = (String)converted.getProperty().evaluate(context);
            try {
                if (((MemberExpr) callee).isOptional() && Objects.isNull(object)) {
                    return null;
                }
                return PropertyAccessor.INSTANCE.call(object, method, arguments);
            } catch (RuntimeException e) {
                throw new EvaluationException(e);
            }
        }
        throw new EvaluationException("Invalid callee");
    }
}
