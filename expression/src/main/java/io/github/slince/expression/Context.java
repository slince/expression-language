package io.github.slince.expression;

import io.github.slince.expression.extension.filter.AbstractFilter;
import io.github.slince.expression.extension.function.AbstractFunction;

public interface Context {

    /**
     * Returns the variable with the given name.
     * @param name the variable name.
     * @return the variable
     */
    Object getVar(String name);

    /**
     * Sets a variable.
     * @param name the variable name.
     * @param value the variable
     */
    void setVar(String name, Object value);

    /**
     * Checks whether the variable with the specified name exists
     * @param name the variable name.
     * @return Whether it exists
     */
    boolean hasVar(String name);

    /**
     * Returns the filter with the given name.
     * @param name the filter name.
     * @return the filter
     */
    AbstractFilter getFilter(String name);

    /**
     * Checks whether the filter with the specified name exists
     * @param name the filter name.
     * @return true if exists, false otherwise
     */
    boolean hasFilter(String name);

    /**
     * Returns the function with the given name.
     * @param name the function name.
     * @return the function
     */
    AbstractFunction getFunction(String name);

    /**
     * Checks whether the function with the specified name exists
     * @param name the function name.
     * @return true if exists, false otherwise
     */
    boolean hasFunction(String name);
}
