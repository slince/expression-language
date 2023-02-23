package io.github.slince.expression;

import io.github.slince.expression.extension.filter.AbstractFilter;
import io.github.slince.expression.extension.function.AbstractFunction;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class MapContext implements Context {

    /**
     * Variable map.
     */
    private final Map<String, Object> vars;

    /**
     * Filter map.
     */
    private final Map<String, AbstractFilter> filters;

    /**
     * Function map.
     */
    private final Map<String, AbstractFunction> functions;

    public MapContext(){
        this(new HashMap<>(), Collections.emptyMap(), Collections.emptyMap());
    }

    public MapContext(Map<String, Object> vars){
        this(vars, Collections.emptyMap(), Collections.emptyMap());
    }

    public MapContext(Map<String, Object> vars, Map<String, AbstractFilter> filters){
        this(vars, filters, Collections.emptyMap());
    }

    @Override
    public void setVar(String name, Object value) {
        vars.put(name, value);
    }

    @Override
    public Object getVar(String name) {
        return vars.get(name);
    }

    @Override
    public boolean hasVar(String name) {
        return vars.containsKey(name);
    }

    @Override
    public AbstractFilter getFilter(String name) {
        return filters.get(name);
    }

    @Override
    public boolean hasFilter(String name) {
        return filters.containsKey(name);
    }

    @Override
    public AbstractFunction getFunction(String name) {
        return functions.get(name);
    }

    @Override
    public boolean hasFunction(String name) {
        return functions.containsKey(name);
    }
}
