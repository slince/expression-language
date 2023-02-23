package io.github.slince.expression.ast;

import io.github.slince.expression.Position;
import io.github.slince.expression.visitor.VisitorAware;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract public class AbstractNode implements Node, VisitorAware {

    protected Position position;

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void visit(Visitor visitor) {
    }
}
