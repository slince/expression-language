package io.github.slince.expression.extension;

import io.github.slince.expression.extension.filter.AbstractFilter;
import io.github.slince.expression.extension.function.AbstractFunction;

import java.util.Collections;
import java.util.List;
import java.util.Map;

abstract public class AbstractExtension {

    /**
     * Return provided global variables.
     * @return the variable collection
     */
    public Map<String, Object> getVars(){
        return Collections.emptyMap();
    }

    /**
     * Return provided filters
     *
     * @return the filter collection
     */
    public List<AbstractFilter> getFilters(){
        return Collections.emptyList();
    }

    /**
     * Return provided functions
     * @return the function collection
     */
    public List<AbstractFunction> getFunctions(){
        return Collections.emptyList();
    }
}
