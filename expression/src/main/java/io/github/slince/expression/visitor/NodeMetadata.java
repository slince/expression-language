package io.github.slince.expression.visitor;

import io.github.slince.expression.ast.Node;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
final public class NodeMetadata {

    private final Node node;
    private final Map<String, Object> metadata = new HashMap<>();

    public Object get(String name, Object defaults){
        return metadata.getOrDefault(name, defaults);
    }

    public Object get(String name){
        return metadata.get(name);
    }

    public Object set(String name, Object value){
        return metadata.put(name, value);
    }

    public Object has(String name){
        return metadata.containsKey(name);
    }
}
