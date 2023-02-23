# Expression Language

An expressive expression engine.

[![Build Status](https://img.shields.io/github/actions/workflow/status/slince/expression/ci.yml?style=flat-square)](https://github.com/slince/expression/actions)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.slince.expression/expression?style=flat-square)](https://maven-badges.herokuapp.com/maven-central/io.github.slince.expression/expression)
[![LICENSE](https://img.shields.io/github/license/slince/expression?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.txt)

[中文说明/Chinese Documentation](./README.md)

## Installation

```xml
<dependency>
    <groupId>io.github.slince</groupId>
    <artifactId>expression</artifactId>
    <version>0.0.1</version>
</dependency>
```
ObjectPath:

```xml
<dependency>
    <groupId>io.github.slince</groupId>
    <artifactId>expression-data-path</artifactId>
    <version>0.0.1</version>
</dependency>
```
ObjectPath [Documentation](./docs/zh_CN/objectpath.md)；

## Getting Started

```java
import io.github.slince.expression.Evaluator;
import io.github.slince.expression.MapContext;

import java.util.Arrays;
import java.util.List;

public class Book {

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

    public static void main(String[] args) {
        // Create a book
        Book book = new Book("The Lady of the Camellias", 12.89f, Arrays.asList("Love Story", "France", null));
        MapContext ctx = new MapContext();
        ctx.setVar("book", book);
        // evaluate expression
        // "The Lady of the Camellias"
        System.out.println(Evaluator.INSTANCE.evaluate("book.name", ctx));
        // 3
        System.out.println(Evaluator.INSTANCE.evaluate("book.tags|size", ctx));
        // 22.89f
        System.out.println(Evaluator.INSTANCE.evaluate("book.price + 10", ctx));
    }
} 
```

## Documentation

Read the [Documentation](docs/zh_CN/index.md).

## Issue

Issue Report: [github issues](https://github.com/slince/expression/issues)

## LICENSE

The Apache 2.0 license. See [Apache-2.0](https://opensource.org/licenses/Apache-2.0)