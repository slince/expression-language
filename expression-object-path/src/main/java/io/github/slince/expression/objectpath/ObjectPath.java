package io.github.slince.expression.objectpath;

import io.github.slince.expression.*;
import lombok.NonNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectPath {

    private static final Map<String, Expression> EXPRESSIONS = new ConcurrentHashMap<>();

    private final Evaluator evaluator;
    private final Context context;

    public ObjectPath(Evaluator evaluator, @NonNull Object object){
        this.evaluator = evaluator;
        this.context = createDefaultContext(object);
    }

    /**
     * Create an object path instance.
     * @param object the target object
     * @return object path
     */
    public static ObjectPath create(Object object) {
        return new ObjectPath(Evaluator.INSTANCE, object);
    }

    /**
     * Create default evaluate context.
     * @param object the target object
     * @return context
     */
    private static Context createDefaultContext(Object object){
        Map<String, Object> vars = new HashMap<>();
        vars.put("$", object);
        return new MapContext(vars);
    }

    /**
     * Read the specified path value.
     * @param expr the path expr
     * @return the value read
     */
    public Object read(String expr){
        Expression expression = getExpression(expr);
        return evaluator.evaluate(expression, this.context);
    }

    /**
     * Read the specified path value with the custom context.
     * @param expr the path expr
     * @param context the context
     * @return the value red
     */
    public Object read(String expr, Context context){
        Expression expression = getExpression(expr);
        return evaluator.evaluate(expression, new DelegatingContext(Arrays.asList(context, this.context)));
    }

    private static Expression getExpression(String raw){
        if (EXPRESSIONS.containsKey(raw)) {
            return EXPRESSIONS.get(raw);
        }
        return EXPRESSIONS.computeIfAbsent(raw, v -> Evaluator.INSTANCE.build(raw));
    }
}
