## 全局变量

为了辅助表达式书写，当前表达式引擎默认自带全局变量 `EMPTY`;

- `EMPTY.list()` 返回空集合
- `EMPTY.string()` 返回空字符
- `EMPTY.array()` 返回空数组

你可以通过扩展添加自定义全局变量，详见文档[扩展解释器](extension.md)