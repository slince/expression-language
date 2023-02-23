package io.github.slince.expression;

import io.github.slince.expression.fixtures.ClassRoom;
import io.github.slince.expression.fixtures.Group;
import io.github.slince.expression.fixtures.Student;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PropertyAccessorTest {

    private static final ClassRoom ROOM = new ClassRoom();
    private static final ClassRoom ROOM2 = new ClassRoom();

    private static final Group GROUP1 = new Group("Group1", 12.88f, ROOM);
    private static final Group GROUP2 = new Group("Group2", 15.88f, ROOM);

    static {
        ROOM.name = "first grade";
        ROOM.setDescription("excellent class");
        ROOM.getGroups().add(GROUP1);
        ROOM.getGroups().add(GROUP2);

        ROOM2.name = "first grade";

        Student allen = new Student("Allen", "a good body", 10, GROUP1, ROOM);
        Student bob = new Student("Bob", "another good body", 12, GROUP2, ROOM);
        Student alice = new Student("Alice", "a good girl", 12, GROUP2, ROOM);
        Student sam = new Student("Sam", "a good body 3", 15, GROUP2, ROOM2);

        GROUP1.getStudents().add(allen);
        GROUP1.getStudents().add(bob);

        GROUP2.getStudents().add(bob);
        GROUP2.getStudents().add(alice);

        ROOM.getStudents().add(allen);
        ROOM.getStudents().add(bob);
        ROOM.getStudents().add(alice);

        ROOM2.getStudents().add(sam);
    }
    @Test
    public void testArrayRead(){
        String[] foo = {"hello", "allen"};
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(foo, "length"), 2);
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(foo, 0), "hello");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(foo, 1), "allen");
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            PropertyAccessor.INSTANCE.read(foo, 2);
        });

        Assertions.assertThrows(RuntimeException.class, () -> {
            PropertyAccessor.INSTANCE.read(foo, "unknownProperty");
        });
    }

    @Test
    public void testArrayWrite(){
        String[] foo = {"hello", "allen"};

        PropertyAccessor.INSTANCE.write(foo, 0, "Hello");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(foo, 0), "Hello");

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            PropertyAccessor.INSTANCE.write(foo, 2, "Hello2");
        });
    }

    @Test
    public void testListCall(){
        List<String> foo = Arrays.asList("hello", "allen");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.call(foo, "size"), 2);
    }

    @Test
    public void testListRead(){
        List<String> foo = Arrays.asList("hello", "allen");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(foo, 0), "hello");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(foo, 1), "allen");
        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            PropertyAccessor.INSTANCE.read(foo, 2);
        });
    }

    @Test
    public void testListWrite(){
        List<String> foo = Arrays.asList("hello", "allen");
        PropertyAccessor.INSTANCE.write(foo, 0, "Hello");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(foo, 0), "Hello");

        Assertions.assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            PropertyAccessor.INSTANCE.write(foo, 2, "Hello2");
        });
    }

    @Test
    public void testMapRead(){
        Map<String, String> foo = new HashMap<>();
        foo.put("hello", "allen");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(foo, "hello"), "allen");
        Assertions.assertNull(PropertyAccessor.INSTANCE.read(foo, "hello2"));
    }

    @Test
    public void testMapWrite(){
        Map<String, String> foo = new HashMap<>();
        foo.put("hello", "allen");
        PropertyAccessor.INSTANCE.write(foo, "hello", "allen");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(foo, "hello"), "allen");
        PropertyAccessor.INSTANCE.write(foo, "hello2", "allen");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(foo, "hello2"), "allen");
    }

    @Test
    public void testObjRead(){
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(ROOM, "name"), "first grade");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(ROOM, "description"), "excellent class");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(ROOM, "studentNum"), 3);
    }

    @Test
    public void testObjWrite(){
        PropertyAccessor.INSTANCE.write(ROOM, "description", "Ordinary class");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(ROOM, "description"), "Ordinary class");
        Assertions.assertThrows(RuntimeException.class, () -> {
            PropertyAccessor.INSTANCE.write(ROOM, "name2", "second grade");
        });
        // 公共属性
        PropertyAccessor.INSTANCE.write(ROOM, "name", "second grade");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.read(ROOM, "name"), "second grade");
        PropertyAccessor.INSTANCE.write(ROOM, "name", "first grade");
    }

    @Test
    public void testCall(){

        Assertions.assertEquals(PropertyAccessor.INSTANCE.call(ROOM, "hello", "aa", 10), "A");
        Assertions.assertEquals(PropertyAccessor.INSTANCE.call(ROOM, "hello", "aa", "aaa"),"B");

        Assertions.assertEquals(PropertyAccessor.INSTANCE.call(ROOM, "hello", null, "aaa"),"B");
        Assertions.assertThrows(MethodKey.AmbiguousException.class,() -> {
            PropertyAccessor.INSTANCE.call(ROOM, "hello", "aa", null);
        });
    }

    @Test
    public void testSearch(){
        List<Object> names = PropertyAccessor.INSTANCE.search(ROOM, "name");
        Assertions.assertEquals(
                names.stream().sorted().collect(Collectors.toList()),
                Stream.of("first grade", "Group1", "Group2", "Allen", "Bob", "Alice").sorted().collect(Collectors.toList())
        );

        List<Object> names2 = PropertyAccessor.INSTANCE.search(ROOM2, "name");
        Assertions.assertEquals(
                names2.stream().sorted().collect(Collectors.toList()),
                Stream.of("first grade", "first grade", "Sam", "Group1", "Group2", "Allen", "Bob", "Alice").sorted().collect(Collectors.toList())
        );
    }
}
