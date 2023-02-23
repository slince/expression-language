package io.github.slince.expression.token;

import io.github.slince.expression.SyntaxError;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TokenStream {

    private final List<Token> tokens;

    private int index = 0;

    public TokenStream(){
        this(new ArrayList<>());
    }

    public void add(Token token){
        tokens.add(token);
    }

    public Token current(){
        return tokens.get(index);
    }

    public Token look(int step){
        return tokens.get(index + step);
    }

    public Token next(){
        return tokens.get(index ++);
    }

    public boolean eof(){
        return test(Token.Kind.EOF);
    }

    public boolean test(Token.Kind ... kinds){
        return current().test(kinds);
    }

    public Token expect(Token.Kind ... kinds) {
        Token cur = current();
        if (cur.test(kinds)) {
            next();
            return cur;
        }
        String expected = Arrays.stream(kinds).map(Token::literal).collect(Collectors.joining(","));
        throw new SyntaxError(String.format("Unexpected token \"%s\" (expected \"%s\")", cur.getLiteral(), expected), cur.getPosition());
    }

    public Token skipIfTest(Token.Kind ... kinds){
        if (test(kinds)) {
            return next();
        }
        return null;
    }
}
