# Expression Language

一个富有语言表现力的表达式引擎 Java 实现。

[![Build Status](https://img.shields.io/github/actions/workflow/status/slince/expression/ci.yml?style=flat-square)](https://github.com/slince/expression/actions)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.slince/expression?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/io.github.slince/expression)
[![LICENSE](https://img.shields.io/github/license/slince/expression?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.txt)

[English Documentation](./README_EN.md)

## 安装

```xml
<dependency>
    <groupId>io.github.slince</groupId>
    <artifactId>expression</artifactId>
    <version>0.0.2-RELEASE</version>
</dependency>
```

ObjectPath:

```xml
<dependency>
    <groupId>io.github.slince</groupId>
    <artifactId>expression-data-path</artifactId>
    <version>0.0.2-RELEASE</version>
</dependency>
```
ObjectPath 使用文档见[这里](./docs/zh_CN/objectpath.md)；

## 快速开始

```java
import io.github.slince.expression.Evaluator;
import io.github.slince.expression.MapContext;

import java.util.Arrays;
import java.util.List;

public class Book {
    
    public static void main(String[] args) {
        // 创建一本书实例
        Book book = new Book("The Lady of the Camellias", 12.89f, Arrays.asList("Love Story", "France", null));
        MapContext ctx = new MapContext();
        ctx.setVar("book", book);

        // 执行表达式
        // "The Lady of the Camellias"
        System.out.println(Evaluator.INSTANCE.evaluate("book.name", ctx));
        // 3
        System.out.println(Evaluator.INSTANCE.evaluate("book.tags|size", ctx));
        // 22.89f
        System.out.println(Evaluator.INSTANCE.evaluate("book.price + 10", ctx));
    }
    
    private final String name;
    private final float price;
    private final List<String> tags;

    public Book(String name, float price, List<String> tags) {
        this.name = name;
        this.price = price;
        this.tags = tags;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public List<String> getTags() {
        return tags;
    }
} 
```

## 文档

更多文档请查看[详细文档](docs/zh_CN/index.md)

## 问题反馈

报告 Issue: [github issues](https://github.com/slince/expression/issues)

## LICENSE

The Apache 2.0 license. See [Apache-2.0](https://opensource.org/licenses/Apache-2.0)