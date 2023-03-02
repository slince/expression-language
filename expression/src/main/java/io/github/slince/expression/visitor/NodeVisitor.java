package io.github.slince.expression.visitor;


import io.github.slince.expression.ast.Node;

public interface NodeVisitor {

     int DEFAULT_PRIORITY = 0;

     Node enterNode(Node node, NodeMetadataCollection metas);

     Node leaveNode(Node node, NodeMetadataCollection metas);

     default int getPriority(){
          return DEFAULT_PRIORITY;
     }
}
