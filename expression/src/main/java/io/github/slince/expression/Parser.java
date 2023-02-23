package io.github.slince.expression;

import io.github.slince.expression.ast.*;
import io.github.slince.expression.token.Operators;
import io.github.slince.expression.token.Token;
import io.github.slince.expression.token.TokenStream;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class Parser {

    private final TokenStream tokens;

    /**
     * 解析token流，生产抽象语法树
     * @return 抽象语法树
     */
    public Node parse(){
        return parseExpr();
    }

    private Expr parseExpr() {
        return parseExpr(0);
    }

    private Expr parseExpr(int precedence){
        Expr lhs = parsePrimary();
        Token token = tokens.current();
        while(Operators.isBinary(token.getLiteral()) && Operators.getBinaryPrecedence(token.getLiteral()) > precedence) {
            Integer curPrecedence = Operators.getBinaryPrecedence(token.getLiteral());
            tokens.next();
            Expr rhs = parseExpr(curPrecedence);
            if (Operators.isLogical(token.getLiteral()) || Operators.isCoalesce(token.getLiteral())) {
                lhs = new LogicalExpr(token.getLiteral(), lhs, rhs);
            } else {
                lhs = new BinaryExpr(token.getLiteral(), lhs, rhs);
            }
            token = tokens.current();
        }
        if (precedence == 0) {
            lhs = parseConditionalExpr(lhs);
        }
        return lhs;
    }

    private Expr parseConditionalExpr(Expr expr){
        while (tokens.test(Token.Kind.QUESTION)) {
            tokens.next();
            Expr consequent = parseExpr();
            tokens.expect(Token.Kind.COLON);
            Expr alternate = parseExpr();
            expr = new ConditionalExpr(expr, consequent, alternate);
        }
        return expr;
    }

    private Expr parsePrimary(){
        Token token = tokens.current();
        Expr expr = null;
        switch (token.getKind()) {
            case INT:
                expr = new Literal("int", token.getLiteral(), Integer.parseInt(token.getLiteral()), token.getPosition());
                tokens.next();
                break;
            case FLOAT:
                expr = new Literal("float", token.getLiteral(), Float.parseFloat(token.getLiteral()), token.getPosition());
                tokens.next();
                break;
            case STR:
                expr = new Literal("string", token.getLiteral(), token.getLiteral(), token.getPosition());
                tokens.next();
                break;
            case ID:
                expr = parseValue();
                break;
            case LPAREN:
                expr = parseParenExpr();
                break;
            case INC:
            case DEC:
                expr = parseUpdateExpr(null, true);
                break;
            case ADD:
            case SUB:
            case NOT:
            case LOGIC_NOT:
                expr = parseUnaryExpr();
                break;
            case DOLLAR:
            case AT:
                expr = new Reference(token.getLiteral(), token.getPosition());
                tokens.next();
                break;
            default:
                unexpect(token);
        }
        return parsePostfixExpr(expr);
    }

    private Expr parsePostfixExpr(Expr expr){
        while (!tokens.eof()) {
            Token token = tokens.current();
            boolean end = false;
            switch(token.getKind()){
                // func(); 函数/方法调用
                case LPAREN:
                    if (expr instanceof Reference) {
                        expr = new Identifier(((Reference) expr).getIdent(), expr.getPosition());
                    }
                    expr = new CallExpr(expr, parseArguments());
                    break;
                // a.b、a?.b ; 访问成员
                case DOT:
                case QUESTION_DOT:
                    expr = parseObjectMemberExpr(expr);
                    break;
                case DOUBLE_DOT:
                case QUESTION_DOUBLE_DOT:
                    expr = parseSearchChildrenExpr(expr);
                    break;
                // a['xxx'] list访问成员
                case LBRACKET:
                    expr = parseAccessExpr(expr);
                    break;
                case INC:
                case DEC:
                    expr = parseUpdateExpr(expr, false);
                    break;
                // a|slice(2, 3) 过滤器调用
                case OR:
                    expr = parseFilterExpr(expr);
                    break;
                default:
                    end = true;
            }
            if (end) {
                break;
            }
        }
        return expr;
    }

    private Expr parseValue(){
        Token token = tokens.expect(Token.Kind.ID);
        Expr expr;
        switch (token.getLiteral()) {
            case "true":
            case "TRUE":
                expr = new Literal("bool", token.getLiteral(), true, token.getPosition());
                break;
            case "false":
            case "FALSE":
                expr = new Literal("bool", token.getLiteral(), false, token.getPosition());
                break;
            case "null":
            case "NULL":
                expr = new Literal("null", token.getLiteral(), null, token.getPosition());
                break;
            default:
                expr = new Reference(token.getLiteral(), token.getPosition());
        }
        return expr;
    }

    private Identifier parseIdentifier() {
        Token token = tokens.expect(Token.Kind.ID);
        return new Identifier(token.getLiteral(), token.getPosition());
    }

    private MemberExpr parseObjectMemberExpr(Expr expr){
        Token token = tokens.expect(Token.Kind.DOT, Token.Kind.QUESTION_DOT);
        Identifier property = parseIdentifier();
        return new MemberExpr(expr, property, token.getKind() == Token.Kind.QUESTION_DOT);
    }

    private SearchChildrenExpr parseSearchChildrenExpr(Expr expr){
        Token token = tokens.expect(Token.Kind.DOUBLE_DOT, Token.Kind.QUESTION_DOUBLE_DOT);
        Identifier property = parseIdentifier();
        return new SearchChildrenExpr(expr, property, token.getKind() == Token.Kind.QUESTION_DOUBLE_DOT);
    }

    private Expr parseAccessExpr(Expr expr){
        tokens.expect(Token.Kind.LBRACKET);
        Expr property = parseExpr();
        if (Objects.nonNull(tokens.skipIfTest(Token.Kind.COLON))) {
            expr = new SliceExpr(expr, property, parseExpr());
        } else {
            expr = new MemberExpr(expr, property, false);
        }
        tokens.expect(Token.Kind.RBRACKET);
        return expr;
    }

    private List<Expr> parseArguments(){
        tokens.expect(Token.Kind.LPAREN);
        List<Expr> args = new ArrayList<>();
        while (!tokens.test(Token.Kind.RPAREN)) {
            if (args.size() > 0) {
                tokens.expect(Token.Kind.COMMA);
            }
            args.add(parseExpr());
        }
        tokens.expect(Token.Kind.RPAREN);
        return args;
    }

    private Expr parseParenExpr(){
        tokens.expect(Token.Kind.LPAREN);
        Expr expr = parseExpr();
        tokens.expect(Token.Kind.RPAREN);
        return expr;
    }

    private UnaryExpr parseUnaryExpr(){
        Token token = tokens.expect(Token.Kind.ADD, Token.Kind.SUB, Token.Kind.NOT, Token.Kind.LOGIC_NOT);
        Expr expr = parseExpr();
        return new UnaryExpr(token.getLiteral(), expr, token.getPosition());
    }

    private UpdateExpr parseUpdateExpr(Expr argument, boolean prefix){
        Token token = tokens.expect(Token.Kind.INC, Token.Kind.DEC);
        if (prefix) {
            argument = parsePrimary();
        }
        if (!(argument instanceof Identifier) && !(argument instanceof MemberExpr)) {
            String msg = prefix
                    ? "Invalid right-hand side in prefix operation"
                    : "Invalid left-hand side in postfix operation";
            unexpect(msg, token.getPosition());
        }
        return new UpdateExpr(prefix, token.getLiteral(), argument, prefix ? token.getPosition() : argument.getPosition());
    }

    private FilterExpr parseFilterExpr(Expr obj){
        tokens.expect(Token.Kind.OR);
        if (tokens.test(Token.Kind.ID)) {
            Identifier property = parseIdentifier();
            List<Expr> args = tokens.test(Token.Kind.LPAREN) ? parseArguments() : Collections.emptyList();
            return new FilterExpr(obj, property, args);
        }
        Expr filter = tokens.test(Token.Kind.LPAREN) ? parseParenExpr() : parseExpr();
        return new ArrayFilter(obj, filter);
    }

    private void unexpect(Token token){
        throw new SyntaxError(String.format("Unexpected token \"%s\"", token.getLiteral()), token.getPosition());
    }

    private void unexpect(String msg, Position position){
        throw new SyntaxError(msg, position);
    }
}
