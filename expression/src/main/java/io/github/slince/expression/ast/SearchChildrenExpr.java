package io.github.slince.expression.ast;

import io.github.slince.expression.Context;
import io.github.slince.expression.EvaluationException;
import io.github.slince.expression.Position;
import io.github.slince.expression.PropertyAccessor;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class SearchChildrenExpr extends Expr {

    private Expr object;

    private Expr property;

    private final boolean optional;

    @Override
    public Position getPosition() {
        return object.getPosition();
    }

    @Override
    public void visit(Visitor visitor) {
        object = (Expr) visitor.visit(object);
        property = (Expr) visitor.visit(property);
    }

    @Override
    public Object evaluate(Context context) {
        Object object = this.object.evaluate(context);
        Object property = this.property.evaluate(context);
        try {
            if (optional && Objects.isNull(object)) {
                return null;
            }
            return PropertyAccessor.INSTANCE.search(object, (String) property);
        } catch (RuntimeException e) {
            throw new EvaluationException(e);
        }
    }
}
