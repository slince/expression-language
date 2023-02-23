package io.github.slince.expression.ast;

import io.github.slince.expression.Context;
import io.github.slince.expression.EvaluationException;
import io.github.slince.expression.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class FilterExpr extends Expr{

    private Expr object;
    private Expr filter;
    private List<Expr> arguments;

    @Override
    public Position getPosition() {
        return object.getPosition();
    }

    @Override
    public void visit(Visitor visitor) {
        object = (Expr) visitor.visit(object);
        filter = (Expr) visitor.visit(filter);
        arguments = arguments.stream().map(v -> (Expr)visitor.visit(v)).collect(Collectors.toList());
    }

    @Override
    public Object evaluate(Context context) {
        Object object = this.object.evaluate(context);
        String filter = (String)this.filter.evaluate(context);
        if (!context.hasFilter(filter)) {
            throw new RuntimeException(String.format("The filter %s is not found", filter));
        }
        Object[] arguments = this.arguments.stream().map(v -> v.evaluate(context)).toArray();
        try {
            return context.getFilter(filter).call(object, arguments);
        } catch (RuntimeException e) {
            throw new EvaluationException(e);
        }
    }
}
