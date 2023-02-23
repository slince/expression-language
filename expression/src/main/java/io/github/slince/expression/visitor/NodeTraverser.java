package io.github.slince.expression.visitor;

import io.github.slince.expression.ast.Node;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

final public class NodeTraverser {

    public static final NodeTraverser EMPTY = new NodeTraverser(Collections.emptyList());

    private final List<NodeVisitor> visitors;

    public NodeTraverser(List<NodeVisitor> visitors){
        visitors.sort(Comparator.comparing(NodeVisitor::getPriority));
        this.visitors = visitors;
    }

    public Node traverse(Node node){
        for (NodeVisitor visitor : visitors) {
            node = this.traverseNode(visitor, node);
        }
        return node;
    }

    private Node traverseNode(NodeVisitor visitor, Node node){
        NodeMetadata metadata = new NodeMetadata(node);
        node = visitor.enterNode(node, metadata);
        if (node instanceof VisitorAware) {
            ((VisitorAware) node).visit(v -> this.traverseNode(visitor, v));
        }
        return visitor.leaveNode(node, metadata);
    }
}
