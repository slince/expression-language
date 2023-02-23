## 扩展解释器

`ObjectPath` 底层使用表达式引擎，如果你需要提供自定义的 `filter`、`function`或者全局变量，那么你需要创建自己的 `Evaluator` 实例；

```java
import filter.extension.io.github.slince.expression.LambdaFilter;
import extension.io.github.slince.expression.MapExtension;
import io.github.slince.expression.Evaluator;

import java.util.Collections;
import java.util.HashMap;

// 1. 创建自定义的filter；比如：给字符串变量拼接指定后缀；
LambdaFilter<String> suffix = new LambdaFilter("suffix",(obj,args)->{
    return obj + ":" +(String)args[0];
});

// 2. 创建全局变量
HashMap<String, Object> vars = new HashMap<>();
vars.put("aGlobalVarName",yourGlobalVar);

// 3. 创建自定义扩展
MapExtension extension = new MapExtension(vars, Collections.singletonList(suffix));

// 4. 创建表达式解释器
Evaluator evaluator = new Evaluator(Collections.singletonList(extension));
```
至此，你可以在表达式里使用 `suffix` 过滤器和全局变量 `aGlobalVarName`；

```
evaluator.evaluate("some.var|suffix('_hello') + aGlobalVarName");
```

## 一次性扩展

如果你不想 `suffix` 被全局使用，你可以在单次调用时传入 [`Context`](../../expression/src/main/java/io/github/slince/expression/Context.java)；

```java
HashMap<String, AbstractFilter> filters = new HashMap<>();
filters.put("suffix", suffix);
MapContext context = new MapContext(vars, filters);

evaluator.evaluate("some.var|suffix('_hello') + aGlobalVarName", context);
```

> 你应该尽量将 `filter` 注入成全局的；除非有必要否则尽量避免注入全局变量；