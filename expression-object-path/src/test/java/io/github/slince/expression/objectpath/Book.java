package io.github.slince.expression.objectpath;

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