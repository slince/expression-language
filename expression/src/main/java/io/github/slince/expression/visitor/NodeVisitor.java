package io.github.slince.expression.visitor;


import io.github.slince.expression.ast.Node;

public interface NodeVisitor {

     int DEFAULT_PRIORITY = 0;

     Node enterNode(Node node, NodeMetadata metadata);

     Node leaveNode(Node node, NodeMetadata metadata);

     default int getPriority(){
          return DEFAULT_PRIORITY;
     }
}
