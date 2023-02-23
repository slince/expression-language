## 目录

* [过滤器说明](#过滤器说明)
* [已经支持的过滤器](#已经支持的过滤器)
* [嵌套表达式过滤器](#嵌套表达式过滤器)


## 过滤器说明

过滤器的作用是对变量进行调整，在表达式语法里采用 `|` ，比如：

```
user.name|upper
```
表示对用户姓名进行大写转换；过滤器调用支持传参，传参语法和 java 方法调用是一致的。

```
user.books|slice(0, 1)
```

过滤器的本质是提供的 java 方法；你可以通过 `extension` 扩展补充自定义的 `filter`，详细文档参阅[扩展表达式解释器](extension.md)

> 当过滤器不需要参数的时候，括号 () 是可以省略的。

## 已经支持的过滤器

基本过滤器：

* [`default`](filter/default.md) 给默认值
* [`emptyList`](filter/default.md#emptyList)
* [`emptyString`](filter/default.md#emptyString)
* [`upper`](filter/default.md#upperlower)
* [`lower`](filter/default.md#upperlower)

数组、集合的过滤器：
* [`slice`](filter/list.md#slice)
* [`size`](filter/list.md#size)
* [`first`](filter/list.md#firstlastget)
* [`last`](filter/list.md#firstlastget)
* [`get`](filter/list.md#firstlastget)
* [`filter`](filter/list.md#filter)
* [`nonNull`](filter/list.md#nonnull)
* [`fluent`](filter/list.md#fluent)

## 嵌套表达式过滤器

为了方便灵活地直接在表达式里对数组和 `collection` 结构进行元素过滤，所支持的特殊的嵌套表达式过滤器。语法如下：

```
user.books|@ != null && @.price > 12
```

在嵌套的子表达式中，`@` 表示数组或者 `collection` 中的每一个元素，上述语法表示过滤出售价大于 12 的书籍；

嵌套表达式优先级较低，如果不是位于表达式末尾需要用括号括起来。

```
user.books|(@ != null && @.price > 12)|fluent.author.name.colect()
```
> 改过滤器要求变量不是 `null`




