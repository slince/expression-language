package io.github.slince.expression.ast;

import io.github.slince.expression.*;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@Getter
@AllArgsConstructor
public class MemberExpr extends Expr implements ContextChanger {

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
            return PropertyAccessor.INSTANCE.read(object, property);
        } catch (RuntimeException e) {
            throw new EvaluationException(e);
        }
    }

    @Override
    public void setVar(Context context, Object value) {
        Object object = this.object.evaluate(context);
        Object property = this.property.evaluate(context);
        PropertyAccessor.INSTANCE.write(object, property, value);
        // 触发下重设
        if (this.object instanceof ContextChanger) {
            ((ContextChanger) this.object).setVar(context, object);
        }
    }
}
