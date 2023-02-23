package io.github.slince.expression.visitor;

import io.github.slince.expression.ast.Node;

abstract public class AbstractNodeVisitor implements NodeVisitor{

    @Override
    public Node enterNode(Node node, NodeMetadata metadata) {
        return enterNode(node);
    }

    @Override
    public Node leaveNode(Node node, NodeMetadata metadata) {
        return leaveNode(node);
    }

    public Node enterNode(Node node) {
        return node;
    }

    public Node leaveNode(Node node) {
        return node;
    }
}
