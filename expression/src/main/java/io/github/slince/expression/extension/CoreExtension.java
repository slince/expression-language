package io.github.slince.expression.extension;

import io.github.slince.expression.EvaluationException;
import io.github.slince.expression.PropertyAccessor;
import io.github.slince.expression.extension.filter.AbstractFilter;
import io.github.slince.expression.extension.filter.LambdaFilter;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class CoreExtension extends AbstractExtension{

    private static final List<AbstractFilter> FILTERS;
    private static final Map<String, Object> VARS = new HashMap<>();

    static {
        // Sets the default value if null
        LambdaFilter<Object> defaults = new LambdaFilter<>("default", (obj, args) -> {
            if (args.length != 1) {
                throw new EvaluationException(String.format("The number of filter [default] parameters is required 1, and %d is provided", args.length));
            }
            return Objects.isNull(obj) ? args[0] : obj;
        });
        // Empty list if null
        LambdaFilter<List<?>> emptyList = new LambdaFilter<>("emptyList", (obj, args) -> ListUtils.emptyIfNull(obj));
        // Empty String if null
        LambdaFilter<String> emptyString = new LambdaFilter<>("emptyString", (obj, args) -> StringUtils.defaultString(obj));
        LambdaFilter<String> upper = new LambdaFilter<>("upper", (obj, args) -> Objects.nonNull(obj) ? obj.toUpperCase() : StringUtils.EMPTY);
        LambdaFilter<String> lower = new LambdaFilter<>("lower", (obj, args) -> Objects.nonNull(obj) ? obj.toLowerCase() : StringUtils.EMPTY);
        // Search for descendant elements
        LambdaFilter<Object> search = new LambdaFilter<>("search", (obj, args) -> {
            if (Objects.isNull(obj)) {
                return null;
            }
            AbstractFilter.requireArgNum(1, "search", args);
            return PropertyAccessor.INSTANCE.search(obj, (String) args[0]);
        });
        FILTERS = Arrays.asList(defaults, emptyList, emptyString, upper, lower, search);
        VARS.put("EMPTY", new Empty());
    }

    @Override
    public List<AbstractFilter> getFilters() {
        return FILTERS;
    }

    @Override
    public Map<String, Object> getVars() {
        return VARS;
    }

    public static class Empty{
        private static final String EMPTY_STRING = StringUtils.EMPTY;
        private static final List<Object> EMPTY_LIST = Collections.emptyList();
        private static final Object[] EMPTY_ARRAY = {};

        /**
         * Empty list
         * @return list
         * @param <T> generic
         */
        @SuppressWarnings("unchecked")
        public <T> List<T> list(){
            return (List<T>) EMPTY_LIST;
        }

        /**
         * Empty string
         * @return string
         */
        public String string(){
            return EMPTY_STRING;
        }

        /**
         * Empty array
         * @return array
         */
        public Object[] array(){
            return EMPTY_ARRAY;
        }
    }
}
