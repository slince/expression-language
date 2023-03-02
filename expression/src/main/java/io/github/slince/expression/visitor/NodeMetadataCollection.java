package io.github.slince.expression.visitor;


import io.github.slince.expression.ast.Node;

import java.util.HashMap;
import java.util.Map;

final public class NodeMetadataCollection {

    private final Map<Node, NodeMetadata> metadata = new HashMap<>();

    public void addMetadata(NodeMetadata metadata) {
        this.metadata.put(metadata.getNode(), metadata);
    }

    public NodeMetadata getMetadata(Node node) {
        return metadata.get(node);
    }
}
