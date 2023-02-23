package io.github.slince.expression.extension.filter;

import io.github.slince.expression.EvaluationException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract public class AbstractFilter{

    /**
     * Returns the filter name.
     * @return the filter name
     */
    abstract public String getName();

    /**
     * Call the filter.
     * @param object the target object.
     * @param arguments arguments
     * @return the result.
     */
    abstract public Object call(Object object, Object ... arguments);

    /**
     * Checks whether the number of arguments is consistent with the expected.
     * @param num expected num
     * @param filter the filter name
     * @param args the arguments
     */
    public static void requireArgNum(int num, String filter, Object[] args){
        if ((0 == num && null == args) || num == args.length) {
            return;
        }
        throw new EvaluationException(String.format("Invalid arguments for filter \"%s\"", filter));
    }

    /**
     * Checks whether the number of arguments is consistent with the expected.
     * @param num expected num
     * @param args the arguments
     */
    public static void requireArgNum(int num, Object[] args){
        if (num == args.length) {
            return;
        }
        throw new EvaluationException("Invalid arguments for filter %s");
    }
}
