package io.github.slince.expression.ast;

import io.github.slince.expression.Context;
import io.github.slince.expression.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SliceExpr extends Expr{
    
    private Expr object;

    private Expr start;

    private Expr end;

    @Override
    public Position getPosition() {
        return object.getPosition();
    }

    @Override
    public void visit(Visitor visitor) {
        object = (Expr) visitor.visit(object);
        start = (Expr) visitor.visit(start);
        end = (Expr) visitor.visit(end);
    }

    @Override
    public Object evaluate(Context context) {
        Object value = object.evaluate(context);
        int splitStartIndex = (int)start.evaluate(context);
        int splitEndIndex = (int)end.evaluate(context);
        return context.getFilter("slice").call(value, splitStartIndex, splitEndIndex);
    }
}
