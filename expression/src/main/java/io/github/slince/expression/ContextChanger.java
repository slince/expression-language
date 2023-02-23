package io.github.slince.expression;

public interface ContextChanger {

    /**
     * Modify the variable
     *
     * @param context the given context
     * @param value new value of variable
     */
    void setVar(Context context, Object value);
}
