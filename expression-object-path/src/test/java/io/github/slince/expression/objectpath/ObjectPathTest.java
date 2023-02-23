package io.github.slince.expression.objectpath;

import io.github.slince.expression.EvaluationException;
import io.github.slince.expression.MapContext;
import io.github.slince.expression.objectpath.fixtures.Book;
import io.github.slince.expression.objectpath.fixtures.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class ObjectPathTest {

    public static final User USER;

    static {
        User user1 = new User("Bob", 100, Collections.emptyList(), null);
        User user2 = new User("Sam", 100, Collections.emptyList(), null);
        Book book1 = new Book("Book1", 12.98, user1);
        Book book2 = new Book("Book2", 10.98, user2);

        USER = new User("Allen", 16, Arrays.asList(book1, null, book2), null);
    }
    
    private static final ObjectPath OBJECT_PATH = ObjectPath.create(USER);
    
    @Test
    public void testSimpleRead(){
        Assertions.assertEquals(OBJECT_PATH.read("$.name"), "Allen");
        Assertions.assertEquals(OBJECT_PATH.read("$.age"), 16);
        Assertions.assertEquals(OBJECT_PATH.read("$.books[0].author.name"), "Bob");
        assert OBJECT_PATH.read("$.books[0].author.name").equals("Bob");
    }

    @Test
    public void testArrayIndex(){
        Assertions.assertEquals(OBJECT_PATH.read("$.books|(@ != null && @.price > 11)|fluent.author.name.collect()"), Collections.singletonList("Bob"));
        Assertions.assertEquals(OBJECT_PATH.read("$.tags[0]"), "A");
        Assertions.assertEquals(OBJECT_PATH.read("$.tags|first"), "A");
        Assertions.assertEquals(OBJECT_PATH.read("$.tags|last"), "C");
        Assertions.assertEquals(OBJECT_PATH.read("$.tags|get(0)"), "A");

        Assertions.assertThrows(EvaluationException.class, () -> {
            OBJECT_PATH.read("$.nickNames[0]");
        });
        Assertions.assertNull(OBJECT_PATH.read("$.nickNames|get(0)"));
        Assertions.assertNull(OBJECT_PATH.read("$.nickNames|first"));
        Assertions.assertNull(OBJECT_PATH.read("$.nickNames|last"));
    }

    @Test
    public void testSize(){
        Assertions.assertEquals(OBJECT_PATH.read("$.tags|size"), 3);
        Assertions.assertEquals(OBJECT_PATH.read("$.tags|nonNull|size"), 2);

        // 调用原生方法
        Assertions.assertEquals(OBJECT_PATH.read("$.books.size()"), 3);
        Assertions.assertEquals(OBJECT_PATH.read("$.books|size(1,23)"), 3);
        Assertions.assertEquals(OBJECT_PATH.read("size($.books)"), 3);
        Assertions.assertEquals(OBJECT_PATH.read("$.books|nonNull|size"), 2);
        Assertions.assertEquals(OBJECT_PATH.read("$.tags.length"), 3);
        Assertions.assertEquals(OBJECT_PATH.read("$.tags|nonNull.length"), 2);

        Assertions.assertEquals(OBJECT_PATH.read("$.nickNames|size"), 0);
    }

    @Test
    public void testCall(){
        Assertions.assertEquals(OBJECT_PATH.read("$.books[0].author.getName()"), "Bob");
        Assertions.assertEquals(OBJECT_PATH.read("$.sayHello('', '')"), "Allen");
    }

    @Test
    public void testSlice(){
        Assertions.assertEquals(OBJECT_PATH.read("$.books|slice(0, 1)|size"), 1);
        Assertions.assertEquals(OBJECT_PATH.read("$.tags|slice(0, 1)|size"), 1);
    }

    @Test
    public void testOptionalRead(){
        Assertions.assertNull(OBJECT_PATH.read("$.books[1]"));
        Assertions.assertThrows(EvaluationException.class, ()->{
            Assertions.assertNull(OBJECT_PATH.read("$.books[1].name"));
        });
        Assertions.assertNull(OBJECT_PATH.read("$.books[1]?.name"));
        Assertions.assertEquals(OBJECT_PATH.read("$.books[0]?.name"), "Book1");
    }

    @Test
    public void testFluency(){
        Assertions.assertEquals(OBJECT_PATH.read("$.books|nonNull|fluent.name.collect()|size"), 2);
    }

    @Test
    public void testArray(){
        Assertions.assertEquals(OBJECT_PATH.read("$.tags|slice(0, 1)|size"), 1);
    }

    @Test
    public void testSearch(){
        Assertions.assertEquals(OBJECT_PATH.read("$..name|size"), 5);
    }

    @Test
    public void testContext(){
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("a", 10);
        MapContext ctx = new MapContext(vars);
        Assertions.assertEquals(ObjectPath.create(10).read("$ + a", ctx), 20);
    }
}
