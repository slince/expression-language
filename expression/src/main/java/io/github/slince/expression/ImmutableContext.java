package io.github.slince.expression;

import io.github.slince.expression.extension.filter.AbstractFilter;
import io.github.slince.expression.extension.function.AbstractFunction;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ImmutableContext implements Context{

    private final Context decorated;

    @Override
    public Object getVar(String name) {
        return decorated.getVar(name);
    }

    @Override
    public void setVar(String name, Object value) {
        throw new RuntimeException("Illegal operation");
    }

    @Override
    public boolean hasVar(String name) {
        return decorated.hasVar(name);
    }

    @Override
    public AbstractFilter getFilter(String name) {
        return decorated.getFilter(name);
    }

    @Override
    public boolean hasFilter(String name) {
        return decorated.hasFilter(name);
    }

    @Override
    public AbstractFunction getFunction(String name) {
        return decorated.getFunction(name);
    }

    @Override
    public boolean hasFunction(String name) {
        return decorated.hasFunction(name);
    }
}
