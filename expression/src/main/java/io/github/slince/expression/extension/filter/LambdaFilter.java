package io.github.slince.expression.extension.filter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.BiFunction;

@RequiredArgsConstructor
@Getter
public class LambdaFilter<T> extends AbstractFilter{

    private final String name;
    private final BiFunction<T, Object[], Object> callee;

    @SuppressWarnings("unchecked")
    @Override
    public Object call(Object object, Object... arguments) {
        return callee.apply((T)object, arguments);
    }
}
