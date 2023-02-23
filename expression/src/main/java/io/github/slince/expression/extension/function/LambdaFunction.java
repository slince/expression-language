package io.github.slince.expression.extension.function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

@RequiredArgsConstructor
@Getter
public class LambdaFunction extends AbstractFunction{

    private final String name;
    private final Function<Object[], Object> callee;

    @Override
    public Object call(Object... arguments) {
        return callee.apply(arguments);
    }
}
