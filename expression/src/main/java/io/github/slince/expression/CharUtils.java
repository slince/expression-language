package io.github.slince.expression;

final public class CharUtils {

    public static boolean isWhitespace(byte ch){
        return ch == '\r' || ch == '\n' || ch == '\t' || ch == ' ';
    }

    public static boolean isDigit(byte ch){
        return Character.isDigit(ch);
    }

    public static boolean isLetter(byte ch){
        return Character.isLetter(ch);
    }

    public static boolean isIdentifier(byte ch){
        return isDigit(ch) || isLetter(ch);
    }
}
