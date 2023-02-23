package io.github.slince.expression;

import io.github.slince.expression.token.Token;
import io.github.slince.expression.token.TokenStream;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;

/**
 * Lexer
 */
public class Lexer{

    private static final Byte BLANK_CHARACTER = ' ';

    /**
     * Source code.
     */
    private final byte[] source;

    /**
     * End position.
     */
    private final Integer end;

    /**
     * Current position offset.
     */
    private Integer offset = 0;

    /**
     * Column no.
     */
    private Integer column = 0;

    /**
     * Line no.
     */
    private Integer line = 0;

    public Lexer(byte[] source){
        this.source = source;
        this.end = source.length - 1;
    }

    public Lexer(String source){
        this(source.getBytes());
    }

    /**
     * Parses the source code and returns the token stream.
     * @return token stream
     */
    public TokenStream lex(){
        TokenStream stream = new TokenStream();
        while (true) {
            Token token = this.lexToken();
            stream.add(token);
            if (token.test(Token.Kind.EOF)) {
                break;
            }
        }
        return stream;
    }

    /**
     * Lex token.
     * @return token
     */
    private Token lexToken(){
        skipBlankCharacters();
        if (eof()) {
            return new Token(Token.Kind.EOF, position());
        }
        byte ch = current();
        Token token;
        if (CharUtils.isDigit(ch)) {
            Number number = readNumber();
            token = new Token(number.isFloat() ? Token.Kind.FLOAT : Token.Kind.INT, number.getLiteral(), position());
        } else if (ch == '\'') {
            token = new Token(Token.Kind.STR, readString(), position());
        } else if (CharUtils.isLetter(ch)) {
            token = new Token(Token.Kind.ID, readIdentifier(), position());
        } else {
            token = lexPunctuation();
        }
        return token;
    }

    /**
     * Lex punctuation.
     * @return punctuation token
     */
    private Token  lexPunctuation(){
        byte ch = current();
        byte next = look();
        Token.Kind kind = null;
        Position position = position();
        switch (ch){
            case '+':
                if (next == '+') {
                    kind = Token.Kind.INC;
                    next();
                } else {
                    kind = Token.Kind.ADD;
                }
                break;
            case '-':
                if (next == '-') {
                    kind = Token.Kind.DEC;
                    next();
                } else {
                    kind = Token.Kind.SUB;
                }
                break;
            case '*':
                kind = Token.Kind.MUL;
                break;
            case '/':
                kind = Token.Kind.DIV;
                break;
            case '%':
                kind = Token.Kind.MOD;
                break;
            case '=':
                if (next == '=') {
                    kind = Token.Kind.EQ;
                    next();
                } else {
                    kind = Token.Kind.ASSIGN;
                }
                break;
            case '>':
                if (next == '=') {
                    kind = Token.Kind.GEQ;
                    next();
                } else if (next == '>') {
                    kind = Token.Kind.SHR;
                    next();
                } else {
                    kind = Token.Kind.GT;
                }
                break;
            case '<':
                if (next == '=') {
                    kind = Token.Kind.LEQ;
                    next();
                } else if (next == '<') {
                    kind = Token.Kind.SHL;
                    next();
                } else {
                    kind = Token.Kind.LT;
                }
                break;
            case '!':
                if (next == '=') {
                    kind = Token.Kind.NEQ;
                    next();
                } else {
                    kind = Token.Kind.LOGIC_NOT;
                }
                break;
            case '&':
                if (next == '&') {
                    kind = Token.Kind.LOGIC_AND;
                    next();
                } else {
                    kind = Token.Kind.AND;
                }
                break;
            case '|':
                if (next == '|') {
                    kind = Token.Kind.LOGIC_OR;
                    next();
                } else {
                    kind = Token.Kind.OR;
                }
                break;
            case '^':
                kind = Token.Kind.XOR;
                break;
            case '~':
                kind = Token.Kind.NOT;
                break;
            case ',':
                kind = Token.Kind.COMMA;
                break;
            case '.':
                if (next == '.') {
                    kind = Token.Kind.DOUBLE_DOT;
                    next();
                } else {
                    kind = Token.Kind.DOT;
                }
                break;
            case ':':
                kind = Token.Kind.COLON;
                break;
            case ';':
                kind = Token.Kind.SEMICOLON;
                break;
            case '?':
                if (next == '.') {
                    next();
                    if (look() == '.') {
                        kind = Token.Kind.QUESTION_DOUBLE_DOT;
                        next();
                    } else {
                        kind = Token.Kind.QUESTION_DOT;
                    }
                } else if (next == '?') {
                    kind = Token.Kind.DOUBLE_QUESTION;
                    next();
                } else {
                    kind = Token.Kind.QUESTION;
                }
                break;
            case '(':
                kind = Token.Kind.LPAREN;
                break;
            case '[':
                kind = Token.Kind.LBRACKET;
                break;
            case ')':
                kind = Token.Kind.RPAREN;
                break;
            case ']':
                kind = Token.Kind.RBRACKET;
                break;
            case '$':
                kind = Token.Kind.DOLLAR;
                break;
            case '@':
                kind = Token.Kind.AT;
                break;
            default:
                unexpect(ch, position);
        }
        next();
        return new Token(kind, position);
    }

    /**
     * Read a number, int or float.
     * @return number
     */
    private Number readNumber(){
        AtomicBoolean isFloat = new AtomicBoolean(false);
        String raw = readIf(ch -> {
            if (ch == '.') {
                if (isFloat.get()) {
                    return false;
                }
                isFloat.set(true);
                return true;
            }
            return CharUtils.isDigit(ch);
        });
        return new Number(isFloat.get(), raw);
    }

    /**
     * Read a string
     * @return string
     */
    private String readString(){
        next();
        String value = readIf(ch -> ch != '\'');
        next();
        return value;
    }

    /**
     * Read an identifier.
     * @return id
     */
    private String readIdentifier(){
        return readIf(CharUtils::isIdentifier);
    }

    /**
     * Read chars until predicate fails
     * @param predicate predicate
     * @return chars read
     */
    private String readIf(Predicate<Byte> predicate){
        List<Byte> buf = new ArrayList<>();
        while (!eof()) {
            Byte ch = current();
            if (predicate.test(ch)) {
                buf.add(ch);
                next();
            } else {
                break;
            }
        }
        Byte[] bytes = buf.toArray(new Byte[]{});
        return new String(ArrayUtils.toPrimitive(bytes));
    }

    /**
     * Skip blank chars.
     */
    private void skipBlankCharacters(){
        while(!eof() && CharUtils.isWhitespace(current())){
            next();
        }
    }

    /**
     * Returns the current char.
     * @return current char
     */
    private byte current() {
        return source[offset];
    }

    /**
     * Look the next char without moving the position pointer.
     * @return the next char
     */
    private byte look() {
        if (offset + 1 > end) {
            return BLANK_CHARACTER;
        }
        return source[offset + 1];
    }

    /**
     * Move position pointer and return the prev char.
     * @return the prev char.
     */
    @SuppressWarnings("all")
    private byte next(){
        byte ch = source[offset];
        offset ++;
        if (ch == '\n') {
            line ++;
            column = 0;
        } else {
            column ++;
        }
        return ch;
    }

    /**
     * Create a position object.
     * @return position
     */
    private Position position(){
        return new Position(offset, line, column);
    }

    /**
     * Check whether it reaches the end char.
     * @return true if the end is reached, false otherwise
     */
    private boolean eof(){
        return offset > end;
    }

    private void unexpect(byte ch, Position position){
        throw new SyntaxError(String.format("Unrecognized punctuation %s", new String(new byte[]{ch})), position);
    }
}
