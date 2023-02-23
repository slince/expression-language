package io.github.slince.expression.token;

import java.util.HashMap;
import java.util.Map;

public class Operators {

    public static final Integer DEFAULT_PRECEDENCE = -1;

    private static final Map<String, Integer> BINARY_PRECEDENCES = new HashMap<>();
    private static final Map<String, Boolean> LOGICAL_OPERATORS = new HashMap<>();
    private static final Map<String, Boolean> COALESCE_OPERATORS = new HashMap<>();
    private static final Map<String, Integer> UNARY_PRECEDENCES = new HashMap<>();

    static {
        BINARY_PRECEDENCES.put("||", 10);
        BINARY_PRECEDENCES.put("&&", 15);

        BINARY_PRECEDENCES.put("??", 16);

        BINARY_PRECEDENCES.put("==", 20);
        BINARY_PRECEDENCES.put("!=", 20);
        BINARY_PRECEDENCES.put("<", 20);
        BINARY_PRECEDENCES.put("<=", 20);
        BINARY_PRECEDENCES.put(">", 20);
        BINARY_PRECEDENCES.put(">=", 20);

        BINARY_PRECEDENCES.put("+", 30);
        BINARY_PRECEDENCES.put("-", 30);

        BINARY_PRECEDENCES.put("*", 60);
        BINARY_PRECEDENCES.put("/", 60);
        BINARY_PRECEDENCES.put("%", 60);
    }

    static {
        LOGICAL_OPERATORS.put("||", true);
        LOGICAL_OPERATORS.put("&&", true);
        COALESCE_OPERATORS.put("??", true);

        UNARY_PRECEDENCES.put("!", 50);
        UNARY_PRECEDENCES.put("+", 500);
        UNARY_PRECEDENCES.put("-", 500);
        UNARY_PRECEDENCES.put("++", 500);
        UNARY_PRECEDENCES.put("--", 500);
    }

    /**
     * Checks whether the operator is a binary operator.
     * @param operator the operator
     * @return true if it is, false otherwise
     */
    public static boolean isBinary(String operator) {
        return BINARY_PRECEDENCES.containsKey(operator);
    }

    /**
     * Checks whether the operator is a logical operator (one of binary operators).
     * @param operator the operator
     * @return true if it is, false otherwise
     */
    public static boolean isLogical(String operator){
        return LOGICAL_OPERATORS.containsKey(operator);
    }

    /**
     * Checks whether the operator is a nullable coalesce operator.
     * @param operator the operator
     * @return true if it is, false otherwise
     */
    public static boolean isCoalesce(String operator){
        return COALESCE_OPERATORS.containsKey(operator);
    }

    /**
     * Checks whether the operator is an unary operator.
     * @param operator the operator
     * @return true if it is, false otherwise
     */
    public static boolean isUnary(String operator) {
        return UNARY_PRECEDENCES.containsKey(operator);
    }

    /**
     * Returns the precedence of the binary operator.
     * @param operator the operator
     * @return the precedence
     */
    public static Integer getBinaryPrecedence(String operator) {
        return BINARY_PRECEDENCES.getOrDefault(operator, DEFAULT_PRECEDENCE);
    }

    /**
     * Returns the precedence of the unary operator.
     * @param operator the operator
     * @return the precedence
     */
    public static Integer getUnaryPrecedence(String operator) {
        return UNARY_PRECEDENCES.getOrDefault(operator, DEFAULT_PRECEDENCE);
    }
}