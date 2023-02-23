package io.github.slince.expression.extension.function;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
abstract public class AbstractFunction {

    /**
     * Returns the function name.
     * @return the function name
     */
    abstract public String getName();

    /**
     * Call the function.
     * @param arguments arguments
     * @return the result
     */
    abstract public Object call(Object ... arguments);
}
