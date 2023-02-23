
## slice

数组、`list` 裁剪；

```
user.books|slice(0, 2)
user.tags|slice(0, 2)
```
裁剪语法与 java 保持一致，包含起始边界，不包含结束边界。
> 该过滤器要求变量不能为 `null`

## size

```
user.books|size
user.tags|size
```
数组、`collection` 结构求长度；当变量为 `null` 时返回 0；如果你确定变量不是 `null` 的，你可以调用原生方法达到同样目的；

```
user.books.size()
user.tags.length
```

## first/last/get

```
user.books|first
user.tags|last
user.tags|get(0)
```
获取数组、`list` 结构的第一个、最后一个元素、指定索引位置元素；当变量为 `null` 或者空数组、空集合时返回 `null`

## filter

```
user.books|filter(yourLambdaFunction)
```

自定义过滤数组、`collection` 结构的元素，该过滤器要求传参为自定义的 lambda 函数。

```java
Map<String, Object> vars = new HashMap<>();
// 注入过滤函数
vars.put("yourLambdaFunction", v -> v.name != null);
MapContext context = new MapContext(vars);
// 带上下文读取路径
objectpath.read("user.books|filter(yourLambdaFunction)", context)
```
> 过滤器要求变量不是 `null`

## nonNull

```
user.books|nonNull
```

快捷过滤，过滤出数组、`collection` 结构中的非 `null` 元素；如果变量为 `null`,则返回 `null`

## fluent

fluent 用于将数组、`collection` 结构拉平，以便链式捕获每一个元素的指定属性；

```
user.books|fluent.author.name.collect() 
```
上述表达式含义为获取书籍的作者的姓名，等价于java中的 `stream` 操作；

```java
user.books.stream().map(v -> v.getAuthor().getName()).collect(Collectors.toList());
```

> 被`fluent`过滤之后，每一次属性调用均返回 `Fluency` 的实例，如上述中的 `user.books|fluent.author` 的到的是仍然是一个
`Fluency` 实例；如果你想结束 `fluent`，需要手动调用 `collect`还原成 `List` 结构的数据，

> 过滤器要求变量不是 `null`







