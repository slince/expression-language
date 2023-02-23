package io.github.slince.expression.token;

import io.github.slince.expression.Position;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class Token{

    private static final Map<Kind, String> TOKEN_VALUES = new HashMap<>();

    static{
        TOKEN_VALUES.put(Kind.ID, "id");
        TOKEN_VALUES.put(Kind.INT, "123");
        TOKEN_VALUES.put(Kind.FLOAT, "123.45");
        TOKEN_VALUES.put(Kind.STR, "string");
        TOKEN_VALUES.put(Kind.LPAREN, "(");
        TOKEN_VALUES.put(Kind.LBRACKET, "[");
        TOKEN_VALUES.put(Kind.RPAREN, ")");
        TOKEN_VALUES.put(Kind.RBRACKET, "]");

        TOKEN_VALUES.put(Kind.COMMA, ",");
        TOKEN_VALUES.put(Kind.COLON, ":");
        TOKEN_VALUES.put(Kind.SEMICOLON, ";");
        TOKEN_VALUES.put(Kind.DOT, ".");
        TOKEN_VALUES.put(Kind.QUESTION, "?");
        TOKEN_VALUES.put(Kind.QUESTION_DOT, "?.");
        TOKEN_VALUES.put(Kind.DOUBLE_QUESTION, "??");
        TOKEN_VALUES.put(Kind.DOUBLE_DOT, "..");
        TOKEN_VALUES.put(Kind.QUESTION_DOUBLE_DOT, "?..");
        TOKEN_VALUES.put(Kind.ASSIGN, "=");
        TOKEN_VALUES.put(Kind.DOLLAR, "$");
        TOKEN_VALUES.put(Kind.AT, "@");

        TOKEN_VALUES.put(Kind.ADD, "+");
        TOKEN_VALUES.put(Kind.SUB, "-");
        TOKEN_VALUES.put(Kind.MUL, "*");
        TOKEN_VALUES.put(Kind.DIV, "/");
        TOKEN_VALUES.put(Kind.MOD, "%");

        TOKEN_VALUES.put(Kind.AND, "&");
        TOKEN_VALUES.put(Kind.OR, "|");
        TOKEN_VALUES.put(Kind.XOR, "^");
        TOKEN_VALUES.put(Kind.NOT, "~");
        TOKEN_VALUES.put(Kind.SHL, "<<");
        TOKEN_VALUES.put(Kind.SHR, ">>");

        TOKEN_VALUES.put(Kind.LOGIC_AND, "&&");
        TOKEN_VALUES.put(Kind.LOGIC_OR, "||");
        TOKEN_VALUES.put(Kind.LOGIC_NOT, "!");

        TOKEN_VALUES.put(Kind.EQ, "==");
        TOKEN_VALUES.put(Kind.LT, "<");
        TOKEN_VALUES.put(Kind.GT, ">");
        TOKEN_VALUES.put(Kind.NEQ, "!=");
        TOKEN_VALUES.put(Kind.GEQ, ">=");
        TOKEN_VALUES.put(Kind.LEQ, "<=");

        TOKEN_VALUES.put(Kind.INC, "++");
        TOKEN_VALUES.put(Kind.DEC, "--");

        TOKEN_VALUES.put(Kind.EOF, "eof");
    }

    /**
     * Token kind.
     */
    private final Kind kind;

    /**
     * Token raw value.
     */
    private final String literal;

    /**
     * Position.
     */
    private final Position position;

    /**
     * Construct token with default literal
     * @param kind token kind
     */
    public Token(Kind kind, Position position){
        this(kind, TOKEN_VALUES.get(kind), position);
    }

    /**
     * Checks whether the token kind matches any given kinds.
     * @param kinds given kinds.
     * @return true if it matches, false otherwise
     */
    public boolean test(Kind... kinds){
        for (Kind kind : kinds) {
            if (kind == this.kind) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the token literal of the given kind.
     * @param kind the kind
     * @return literal
     */
    public static String literal(Kind kind){
        return TOKEN_VALUES.get(kind);
    }

    public enum Kind {
        ID,  // abc
        INT, // 123
        FLOAT, // 123.45
        STR, // "string"
        LPAREN,   // (
        LBRACKET, //[
        RPAREN,   // )
        RBRACKET, // ]

        COMMA,     // ,
        COLON,     // :
        SEMICOLON, // ;
        DOT,       // .
        QUESTION,  // ?
        QUESTION_DOT, // ?.
        DOUBLE_QUESTION, // ??
        DOUBLE_DOT, // ..
        QUESTION_DOUBLE_DOT, // ?..
        ASSIGN,  // =
        DOLLAR, // $
        AT, // @

        ADD, // +
        SUB, // -
        MUL, // *
        DIV, // /
        MOD, // %

        AND,     // &
        OR,      // |
        XOR,     // ^
        NOT,     // ~
        SHL,     // <<
        SHR,     // >>

        LOGIC_AND,  // &&
        LOGIC_OR,  // ||
        LOGIC_NOT,  // !

        EQ,        // ==
        LT,        // <
        GT,        // >
        NEQ,       // !=
        GEQ,       // >=
        LEQ,       // <=

        INC, // ++
        DEC, // --

        EOF, // eof
    }
}

