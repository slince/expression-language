package io.github.slince.expression.extension;

import io.github.slince.expression.EvaluationException;
import io.github.slince.expression.extension.filter.AbstractFilter;
import io.github.slince.expression.extension.filter.LambdaFilter;
import io.github.slince.expression.extension.function.AbstractFunction;
import io.github.slince.expression.extension.function.LambdaFunction;
import lombok.NonNull;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListExtension extends AbstractExtension{

    private static final List<AbstractFilter> FILTERS;

    private static final List<AbstractFunction> FUNCTIONS;

    static {
        // list/array slice
        LambdaFilter<Object> slice = new LambdaFilter<>("slice", ListExtension::slice);
        LambdaFilter<Object> first = new LambdaFilter<>("first", ListExtension::first);
        LambdaFilter<Object> last = new LambdaFilter<>("last", ListExtension::last);
        LambdaFilter<Object> get = new LambdaFilter<>("get", ListExtension::get);
        // collection/array size
        LambdaFilter<Object> size = new LambdaFilter<>("size", ListExtension::size);
        LambdaFilter<Object> fluent = new LambdaFilter<>("fluent", ListExtension::fluent);

        @SuppressWarnings("unchecked")
        LambdaFilter<Object> filter = new LambdaFilter<>("filter", (obj, args) -> {
            AbstractFilter.requireArgNum(1, "filter", args);
            return ListExtension.filter(obj, (Predicate<Object>) args[0]);
        });
        LambdaFilter<Object> nonNull = new LambdaFilter<>("nonNull", (obj, args) -> {
            if (Objects.isNull(obj)) {
                return null;
            }
            return ListExtension.filter(obj, Objects::nonNull);
        });
        FILTERS = Arrays.asList(slice, first, last, get, size, fluent, filter, nonNull);
    }

    static {
        // 长度
        LambdaFunction size = new LambdaFunction("size", (args) -> {
            if (args.length != 1) {
                throw new EvaluationException(String.format("Invalid arguments for function %s", "size"));
            }
            return ListExtension.size(args[0]);
        });
        FUNCTIONS = Collections.singletonList(size);
    }

    @Override
    public List<AbstractFilter> getFilters() {
        return FILTERS;
    }

    @Override
    public List<AbstractFunction> getFunctions() {
        return FUNCTIONS;
    }

    /**
     * Convert array or collection to fluency instance.
     * @param obj array or collection
     * @param args arguments
     * @return fluency
     */
    @SuppressWarnings("unchecked")
    public static Fluency fluent(@NonNull Object obj, Object[] args){
        if (obj.getClass().isArray()) {
            return new Fluency((Object[])obj);
        }
        if (obj instanceof Collection) {
            return new Fluency((Collection<Object>)obj);
        }
        throw new EvaluationException(String.format("Invalid type %s for filter [fluent]", obj.getClass().getName()));
    }

    /**
     * Array or list slice.
     * @param obj array or list
     * @param args the begin and end index
     * @return new array or list
     */
    @SuppressWarnings("unchecked")
    public static Object slice(@NonNull Object obj, Object[] args) {
        AbstractFilter.requireArgNum(2, "slice", args);
        if (obj.getClass().isArray()) {
            return Arrays.copyOfRange((Object[]) obj, (int) args[0], (int) args[1]);
        }
        if (obj instanceof List) {
            return ((List<Object>)obj).subList((int) args[0], (int) args[1]);
        }
        throw new EvaluationException(String.format("Invalid type %s for filter [slice]", obj.getClass().getName()));
    }

    /**
     * Return array or collection size.
     * @param obj array or collection
     * @return size
     */
    public static int size(Object obj){
        return size(obj, null);
    }

    /**
     * Return array or collection size.
     * @param obj array or collection
     * @param args arguments
     * @return size
     */
    public static int size(Object obj, Object[] args) {
        if (Objects.isNull(obj)) {
            return 0;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj);
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).size();
        }
        throw new EvaluationException(String.format("Invalid type %s for filter [size]", obj.getClass().getName()));
    }

    /**
     * Find the first element of the array or list
     * @param obj array or collection
     * @return the first element，null if not found.
     */
    public static Object first(Object obj, Object[] args){
        if (Objects.isNull(obj)) {
            return null;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0 ? null : Array.get(obj, 0);
        }
        if (obj instanceof List) {
            List<?> converted = (List<?>) obj;
            return converted.size() > 0 ? converted.get(0) : null;
        }
        throw new EvaluationException(String.format("Invalid type %s for filter [first]", obj.getClass().getName()));
    }

    /**
     * Find the last element of the array or list
     * @param obj array or collection
     * @return the last element，null if not found.
     */
    public static Object last(Object obj, Object[] args){
        if (Objects.isNull(obj)) {
            return null;
        }
        if (obj.getClass().isArray()) {
            int length = Array.getLength(obj);
            return length == 0 ? null : Array.get(obj, length - 1);
        }
        if (obj instanceof List) {
            List<?> converted = (List<?>) obj;
            int length = converted.size();
            return length == 0 ? null: converted.get(length - 1);
        }
        throw new EvaluationException(String.format("Invalid type %s for filter [last]", obj.getClass().getName()));
    }

    /**
     * Find the element of the array or list
     * @param obj array or collection
     * @return the element，null if not found.
     */
    public static Object get(Object obj, Object[] args){
        AbstractFilter.requireArgNum(1, "get", args);
        if (Objects.isNull(obj)) {
            return null;
        }
        if (obj.getClass().isArray()) {
            return Array.get(obj, (int)args[0]);
        }
        if (obj instanceof List) {
            return ((List<?>) obj).get((int)args[0]);
        }
        throw new EvaluationException(String.format("Invalid type %s for filter [get]", obj.getClass().getName()));
    }

    /**
     * Custom filter array or collection
     * @param obj array or collection
     * @param predicate predicate
     * @return the filtered array or list
     */
    public static Object filter(@NonNull Object obj, Predicate<Object> predicate){
        if (obj.getClass().isArray()) {
            return Arrays.stream((Object[])obj).filter(predicate).toArray();
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).stream().filter(predicate).collect(Collectors.toList());
        }
        throw new EvaluationException(String.format("Invalid type %s for filter", obj.getClass().getName()));
    }
}
