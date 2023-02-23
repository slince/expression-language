
## default

```
user.name|default('admin')
```

上述表达式表示当用户名为 `null` 时给默认值 `admin`；等价于 `user.name ?? 'admin'`

## emptyList

```
user.books|emptyList
```

该表达式表示，当用户的 `books` 为 `null` 的时候赋值为空集合；等价于 `user.books ?? EMPTY.list()`

## emptyString

```
user.name|emptyString
```
上述表达式表示当用户名为 `null` 时给默认值空字符串；等价于 `user.name ?? EMPTY.string()`

## upper/lower

```
user.name|upper
user.name|lower
```
将用户名转换成大写/小写形式，如果变了是 `null`，则返回空字符串；
> 如果你可以确定变量不是null， 你可以调用原生方法 `user.name.toUpperCase()`