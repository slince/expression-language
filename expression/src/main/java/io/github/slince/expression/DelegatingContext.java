package io.github.slince.expression;

import io.github.slince.expression.extension.filter.AbstractFilter;
import io.github.slince.expression.extension.function.AbstractFunction;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class DelegatingContext implements Context{

    private final List<Context> contexts;

    @Override
    public Object getVar(String name) {
        return contexts.stream().filter(ctx -> ctx.hasVar(name)).findFirst().map(ctx ->ctx.getVar(name)).orElse(null);
    }

    @Override
    public void setVar(String name, Object value) {
        contexts.stream().filter(ctx -> ctx.hasVar(name)).findFirst().ifPresent(ctx -> ctx.setVar(name, value));
    }

    @Override
    public boolean hasVar(String name) {
        return contexts.stream().anyMatch(ctx -> ctx.hasVar(name));
    }

    @Override
    public AbstractFilter getFilter(String name) {
        return contexts.stream().filter(ctx -> ctx.hasFilter(name)).findFirst().map(ctx ->ctx.getFilter(name)).orElse(null);
    }

    @Override
    public boolean hasFilter(String name) {
        return contexts.stream().anyMatch(ctx -> ctx.hasFilter(name));
    }

    @Override
    public AbstractFunction getFunction(String name) {
        return contexts.stream().filter(ctx -> ctx.hasFunction(name)).findFirst().map(ctx ->ctx.getFunction(name)).orElse(null);
    }

    @Override
    public boolean hasFunction(String name) {
        return contexts.stream().anyMatch(ctx -> ctx.hasFunction(name));
    }
}
