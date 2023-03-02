package io.github.slince.expression.ast;

import io.github.slince.expression.Position;
import io.github.slince.expression.visitor.Visitable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract public class AbstractNode implements Node, Visitable {

    protected Position position;

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public void visit(Visitor visitor) {
    }
}
