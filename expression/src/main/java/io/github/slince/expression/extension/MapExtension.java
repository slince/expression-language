package io.github.slince.expression.extension;

import io.github.slince.expression.extension.filter.AbstractFilter;
import io.github.slince.expression.extension.function.AbstractFunction;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class MapExtension extends AbstractExtension{

    private final Map<String, Object> vars;
    private final List<AbstractFilter> filters;
    private final List<AbstractFunction> functions;

    public MapExtension(Map<String, Object> vars){
        this(vars, Collections.emptyList(), Collections.emptyList());
    }

    public MapExtension(Map<String, Object> vars, List<AbstractFilter> filters){
        this(vars, filters, Collections.emptyList());
    }


    @Override
    public Map<String, Object> getVars() {
        return vars;
    }

    @Override
    public List<AbstractFilter> getFilters() {
        return filters;
    }

    @Override
    public List<AbstractFunction> getFunctions() {
        return functions;
    }
}
