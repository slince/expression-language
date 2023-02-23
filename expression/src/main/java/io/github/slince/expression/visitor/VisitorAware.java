package io.github.slince.expression.visitor;

import io.github.slince.expression.ast.Node;

public interface VisitorAware {

     void visit(Visitor visitor);

     interface Visitor{
         Node visit(Node node);
     }
}
