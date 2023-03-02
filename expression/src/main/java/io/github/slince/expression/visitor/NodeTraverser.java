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

    /**
     * Traverse the given node.
     * @param node the ast node
     * @return alternate node
     */
    public Node traverse(Node node){
        for (NodeVisitor visitor : visitors) {
            node = this.traverseNode(visitor, node, new NodeMetadataCollection());
        }
        return node;
    }

    private Node traverseNode(NodeVisitor visitor, Node node, NodeMetadataCollection metas){
        NodeMetadata metadata = new NodeMetadata(node);
        metas.addMetadata(metadata);

        Node entered = visitor.enterNode(node, metas);
        if (entered != node) {
            return this.traverseNode(visitor, entered, metas);
        }

        if (node instanceof Visitable) {
            ((Visitable) node).visit(n -> this.traverseNode(visitor, n, metas));
        }

        Node leaved = visitor.leaveNode(node, metas);
        if (leaved != node) {
            return this.traverseNode(visitor, leaved, metas);
        }
        return leaved;
    }
}
