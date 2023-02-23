package io.github.slince.expression.ast;

import io.github.slince.expression.Arithmetic;
import io.github.slince.expression.Context;
import io.github.slince.expression.Position;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ConditionalExpr extends Expr{

    private Expr test;
    private Expr consequent;
    private Expr alternate;

    @Override
    public Position getPosition() {
        return test.getPosition();
    }

    @Override
    public void visit(Visitor visitor) {
        test = (Expr) visitor.visit(test);
        consequent = (Expr) visitor.visit(consequent);
        alternate = (Expr) visitor.visit(alternate);
    }

    @Override
    public Object evaluate(Context context) {
        Object test = this.test.evaluate(context);
        return Arithmetic.INSTANCE.toBoolean(test)
                ? this.consequent.evaluate(context)
                : this.alternate.evaluate(context);
    }
}
