package io.github.slince.expression;

import io.github.slince.expression.ast.BinaryExpr;
import io.github.slince.expression.fixtures.Book;
import io.github.slince.expression.fixtures.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EvaluatorTest {

    public static final User USER;

    static {
        User user1 = new User("Bob", 100, Collections.emptyList());
        User user2 = new User("Sam", 100, Collections.emptyList());
        Book book1 = new Book("Book1", 12.98, user1);
        Book book2 = new Book("Book2", 10.98, user2);

        USER = new User("Allen", 16, Arrays.asList(book1, null, book2));
    }

    private static final MapContext CTX;

    static {
        Map<String, Object> vars = new HashMap<>();
        vars.put("user", USER);
        CTX = new MapContext(vars);
    }

    @Test
    public void testParse(){
        String expr = "1 + 2";
        Assertions.assertInstanceOf(BinaryExpr.class, Evaluator.INSTANCE.build(expr).getAst());
    }

    @Test
    public void testNumber(){
        Assertions.assertEquals(12, Evaluator.INSTANCE.evaluate("10 + 2"));
        Assertions.assertEquals(12.5, Evaluator.INSTANCE.evaluate("10 + 2.5"));
    }

    @Test
    public void testPrecedence(){
        Assertions.assertEquals(9, Evaluator.INSTANCE.evaluate("(1 + 2) * 3"));
        Assertions.assertEquals(7, Evaluator.INSTANCE.evaluate("1 + 2 * 3"));
        Assertions.assertEquals(7, Evaluator.INSTANCE.evaluate("1 + (2 * 3)"));
        Assertions.assertEquals(true, Evaluator.INSTANCE.evaluate("1 + 2 * 3 > 6"));
        Assertions.assertEquals(true, Evaluator.INSTANCE.evaluate("false || 3"));
        Assertions.assertEquals(false, Evaluator.INSTANCE.evaluate("false || 3 > 4"));
        Assertions.assertEquals(true, Evaluator.INSTANCE.evaluate("false || 3 ?? 4"));
    }

    @Test
    public void testNullCoalesce(){
        Assertions.assertEquals(7, Evaluator.INSTANCE.evaluate("null ?? 4 + 3"));
        Assertions.assertEquals(7, Evaluator.INSTANCE.evaluate("(null ?? 4) + 3"));
        Assertions.assertEquals(10, Evaluator.INSTANCE.evaluate("(7 ?? 4) + 3"));
        Assertions.assertEquals(7, Evaluator.INSTANCE.evaluate("null ?? 7 ?? 3"));
        Assertions.assertEquals(false, Evaluator.INSTANCE.evaluate("null ?? 7 > 8 ?? 10 > 9"));

        HashMap<String, Object> vars = new HashMap<>();
        vars.put("a", 10);
        vars.put("b", null);
        vars.put("c", null);
        MapContext ctx = new MapContext(vars);
        Assertions.assertEquals(10, Evaluator.INSTANCE.evaluate("a ?? b ?? c", ctx));
        Assertions.assertEquals(10, Evaluator.INSTANCE.evaluate("b ?? a ?? c", ctx));
        Assertions.assertEquals(10, Evaluator.INSTANCE.evaluate("b ?? c ?? a", ctx));
    }

    @Test
    public void testConditional(){
        Assertions.assertEquals(10, Evaluator.INSTANCE.evaluate("9 > 8 ? 10 : 12"));
        Assertions.assertEquals(10, Evaluator.INSTANCE.evaluate("9 > 8 ? 10 : 12"));

        Assertions.assertEquals(6, Evaluator.INSTANCE.evaluate("9 > 8 ? 12 > 10 ? 6 : 7 : 12"));
        Assertions.assertEquals(6, Evaluator.INSTANCE.evaluate("9 > 8 ? (12 > 10 ? 6 : 7) : 12"));
        Assertions.assertEquals(12, Evaluator.INSTANCE.evaluate("9 > 8 ? 12 > (10 ? 6 : 7) ? 12 : 10 : 8"));
        Assertions.assertEquals(9, Evaluator.INSTANCE.evaluate("9 > 8 ? 7 ? 9 : 10 : 12"));
    }

    @Test
    public void testEmpty(){
        Assertions.assertEquals(0, Evaluator.INSTANCE.evaluate("null ?? EMPTY.list()|size"));
        Assertions.assertEquals(0, Evaluator.INSTANCE.evaluate("(null ?? EMPTY.list())|size"));
        Assertions.assertEquals("", Evaluator.INSTANCE.evaluate("null ?? EMPTY.string()"));
    }

    @Test
    public void testArrayFilter(){
        String expr = "user.books|(@ != null && @.price > 11)|fluent.author.name.collect()|first";
        Assertions.assertEquals(Evaluator.INSTANCE.evaluate(expr, CTX), "Bob");
    }

    @Test
    public void testSearch(){
        Assertions.assertEquals(
                ((List<Object>)Evaluator.INSTANCE.evaluate("user?..name", CTX)).stream().sorted().collect(Collectors.toList()),
                Stream.of("Book1", "Book2", "Allen", "Bob", "Sam").sorted().collect(Collectors.toList())
        );
    }

    @Test
    public void testOptionalMember(){
        Assertions.assertEquals(Evaluator.INSTANCE.evaluate("user.books|get(2)?.name", CTX), "Book2");
        Assertions.assertNull(Evaluator.INSTANCE.evaluate("user.books|get(1)?.name", CTX));
        Assertions.assertNull(Evaluator.INSTANCE.evaluate("null?..name", CTX));
    }

    @Test
    public void testImmutableContext(){
        Evaluator.INSTANCE.evaluate("user.age ++", CTX);
        Assertions.assertEquals(17, USER.getAge());

        Assertions.assertThrows(RuntimeException.class, () -> {
            Context ctx2 = new ImmutableContext(CTX);
            Evaluator.INSTANCE.evaluate("user.age ++", ctx2);
        });
    }
}
