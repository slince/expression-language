
## ObjectPath

ObjectPath 是构建在[表达式引擎](../../expression)的一个应用实例，底层使用表达式解释器[`Evaluator`](../../expression/src/main/java/io/github/slince/expression/Evaluator.java)

### 案例

```java
@RequiredArgsConstructor
@Getter
public class User {
    
    private final String name;
    private final List<Book> books;
    
    @RequiredArgsConstructor
    @Getter
    public static class Book{
        private final String name;
        private final Double price;
        private final User author;
    }
}
```

创建数据对象结构：

```java
User user1 = new User("Sam", Collections.emptyList());
User user2 = new User("Bob", Collections.emptyList());

User.Book book1 = new User.Book("Book1", 12.98, user1);
User.Book book2 = new User.Book("Book2", 10.98, user2);

User user = new User("Allen", Arrays.asList(book1, null, book2));
```

创建 [`ObjectPath`](../../expression-object-path/src/main/java/io/github/slince/expression/objectpath/ObjectPath.java) 对象.

```java
import objectpath.io.github.slince.expression.ObjectPath;

// 使用默认的 evaluator 创建 ObjectPath 实例
ObjectPath objectpath = ObjectPath.create(user);

// 简单调用读取属性
assert objectpath.read("$.name").equals("Allen");

// 多层级链式读取
assert objectpath.read("$.books[0].author.name").equals("Sam");
```
在 `ObjectPath` 中路径表达式语法和[表达式基本语法](basic.md)是一致的；特殊地地方在于根变量会被默认设置成 `$`

### 自定义 `ObjectPath` 使用的解释器

```java
import objectpath.io.github.slince.expression.ObjectPath;
import io.github.slince.expression.Evaluator;

// 如果你需要扩展的 evaluator
Evaluator evaluator = new Evaluator(someYourExtensions);
ObjectPath objectpath = new ObjectPath(evaluator,yourObject);
objectpath.read("$.the.path.to.your.property");
```

如何扩展解释器见[扩展解释器文档](extension.md)

